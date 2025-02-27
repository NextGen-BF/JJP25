package com.blankfactor.auth.config;

import com.blankfactor.auth.entity.User;
import com.blankfactor.auth.service.JwtService;
import com.blankfactor.auth.service.UserService;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@Component
@Log4j2
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final HandlerExceptionResolver handlerExceptionResolver;

    private final JwtService jwtService;

    private final UserDetailsService userDetailsService;

    private final JwtDecoder googleJwtDecoder;

    private final UserService userService;

    public JwtAuthenticationFilter(
            JwtService jwtService,
            UserDetailsService userDetailsService,
            HandlerExceptionResolver handlerExceptionResolver,
            JwtDecoder googleJwtDecoder,
            UserService userService
    ) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
        this.handlerExceptionResolver = handlerExceptionResolver;
        this.googleJwtDecoder = googleJwtDecoder;
        this.userService = userService;
    }

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) {
        final String authHeader = request.getHeader("Authorization");

        try {
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                log.debug("Authorization header is missing or invalid. Continuing without authentication.");
                filterChain.doFilter(request, response);
            } else {
                String token = authHeader.substring(7);

                Jwt jwt = googleJwtDecoder.decode(token);
                Map<String, Object> claims = jwt.getClaims();
                if (claims.containsKey("iss") && claims.get("iss").equals("https://accounts.google.com")) {

                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(claims, null, new ArrayList<>());

                    SecurityContextHolder.getContext().setAuthentication(authentication);
                    Optional<User> userOptional = userService.getUserByEmail((String) claims.get("email"));

                    if (userOptional.isPresent()) {
                        String appToken = jwtService.generateToken(userOptional.get());
                        response.setHeader("X-Auth-Token", appToken);
                    } else {
                        User user = User.builder()
                                .username((String) claims.get("name"))
                                .firstName((String) claims.get("given_name"))
                                .lastName((String) claims.get("family_name"))
                                .email((String) claims.get("email"))
                                .enabled(false)
                                .birthDate(LocalDateTime.now())
                                .roles(Set.of())
                                .build();

                        String appToken = jwtService.generateToken(user);
                        response.setHeader("X-Auth-Token", appToken);
                    }

                } else {
                    final String jwtToken = authHeader.substring(7);
                    final String userEmail = jwtService.extractUsername(jwtToken);
                    log.debug("Extracted user email from JWT: {}", userEmail);

                    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

                    if (userEmail != null && authentication == null) {
                        UserDetails userDetails = this.userDetailsService.loadUserByUsername(userEmail);
                        log.debug("Loaded user details for email: {}", userEmail);
                        if (!jwtService.isTokenValid(jwtToken, userDetails)) {
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

                    filterChain.doFilter(request, response);
                }

            }
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
        } catch (Exception e) {
            log.error("An error occurred during JWT authentication.", e);
            handlerExceptionResolver.resolveException(request, response, null, e);
        }
    }
}

