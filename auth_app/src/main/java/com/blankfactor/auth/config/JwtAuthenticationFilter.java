package com.blankfactor.auth.config;

import com.blankfactor.auth.service.JwtService;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.io.IOException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.util.Collections;

@Component
@Log4j2
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final int BEARER_HEADER_LENGTH = 7;
    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String FIREBASE_TOKEN_ISSUER = "https://securetoken.google.com/";

    private final HandlerExceptionResolver handlerExceptionResolver;

    private final JwtService jwtService;

    private final UserDetailsService userDetailsService;

    public JwtAuthenticationFilter(
            JwtService jwtService,
            UserDetailsService userDetailsService,
            HandlerExceptionResolver handlerExceptionResolver
    ) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
        this.handlerExceptionResolver = handlerExceptionResolver;
    }

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException, java.io.IOException {
        final String authHeader = request.getHeader(AUTHORIZATION_HEADER);

        log.debug("Start of filtering");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            log.debug("Authorization header is missing. Continuing without authentication.");
            filterChain.doFilter(request, response);
            return;
        }

        String token = authHeader.substring(BEARER_HEADER_LENGTH);

        try {
            if (isFirebaseToken(token)) {
                authenticateWithFirebase(token);
            } else {
                systemToken(token, request);
            }

            filterChain.doFilter(request, response);

        } catch (ExpiredJwtException e) {
            log.error("JWT token has expired.", e);
            handlerExceptionResolver.resolveException(
                    request,
                    response,
                    null,
                    new ExpiredJwtException(null, null, "JWT token has expired")
            );
        } catch (JwtException e) {
            log.error("JWT token is invalid.", e);
            handlerExceptionResolver.resolveException(
                    request,
                    response,
                    null,
                    new JwtException("JWT token is invalid")
            );
        } catch (FirebaseAuthException e) {
            log.error("An error occurred during Firebase JWT authentication.", e);
            handlerExceptionResolver.resolveException(
                    request,
                    response,
                    null,
                    e
            );
        } catch (Exception e) {
            log.error("An error occurred during JWT authentication.", e);
            handlerExceptionResolver.resolveException(
                    request,
                    response,
                    null,
                    e
            );
        }
    }

    private static boolean isFirebaseToken(String token) throws FirebaseAuthException {
        FirebaseToken decodedToken = FirebaseAuth.getInstance().verifyIdToken(token);
        String issuer = decodedToken.getIssuer();
        return issuer != null && issuer.startsWith(FIREBASE_TOKEN_ISSUER);
    }

    private static void authenticateWithFirebase(String token) throws FirebaseAuthException {
        FirebaseToken decodedToken = FirebaseAuth.getInstance().verifyIdToken(token);
        String email = decodedToken.getEmail();

        User user = new User(email, "", Collections.emptyList());
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());

        SecurityContextHolder.getContext().setAuthentication(authentication);
        log.info("Firebase authentication successful for: {}", email);
    }

    private void systemToken(
            String token,
            HttpServletRequest request
    ) {
        final String userEmail = jwtService.extractUsername(token);
        log.debug("Extracted user email from JWT: {}", userEmail);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (userEmail != null && authentication == null) {
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(userEmail);
            log.debug("Loaded user details for email: {}", userEmail);

            if (!jwtService.isTokenValid(token, userDetails)) {
                log.warn("JWT token is invalid for user: {}", userEmail);
                throw new JwtException("JWT token is invalid");
            }

            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                    userDetails,
                    null,
                    userDetails.getAuthorities()
            );

            authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authToken);
            log.debug("Set authentication context for user: {}", userEmail);



        }
    }

}

