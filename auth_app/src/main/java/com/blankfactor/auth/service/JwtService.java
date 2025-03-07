package com.blankfactor.auth.service;

import com.blankfactor.auth.entity.User;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import java.security.Key;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Log4j2
public class JwtService {

    @Value("${security.jwt.secret-key}")
    private String secretKey;

    @Value("${security.jwt.expiration-time}")
    private long jwtExpiration;

    public String extractUsername(String token) {
        log.debug("Extracting username from token.");
        return extractClaim(token, Claims::getSubject);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        log.debug("Extracting claim from token.");
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    public String generateToken(UserDetails userDetails) {
        log.debug("Generating token for user: {}", userDetails.getUsername());
        Map<String, Object> extraClaims = new HashMap<>();
        List<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());
        extraClaims.put("roles", roles);

        if (userDetails instanceof User) {
            extraClaims.put("userId", ((User) userDetails).getId());
        }

        return buildToken(extraClaims, userDetails.getUsername(), jwtExpiration);
        //return generateToken(extraClaims, userDetails);

    }

    public String generateToken(Map<String, Object> extraClaims, UserDetails userDetails) {
        log.debug("Generating token with extra claims for user: {}", userDetails.getUsername());
        return buildToken(extraClaims, userDetails.getUsername(), jwtExpiration);
    }

    public long getExpirationTime() {
        log.debug("Getting JWT expiration time.");
        return jwtExpiration;
    }

    private String buildToken(
            Map<String, Object> extraClaims,
            String email,
            long expiration
    ) {
        log.debug("Building token for user: {}", email);
        return Jwts
                .builder()
                .setClaims(extraClaims)
                .setSubject(email)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        log.debug("Checking if token is valid for user: {}", userDetails.getUsername());
        final String username = extractUsername(token);

        if (!username.equals(userDetails.getUsername())) {
            log.warn("Token does not belong to the authenticated user.");
            throw new JwtException("Token does not belong to the authenticated user");
        }

        if (isTokenExpired(token)) {
            log.warn("JWT token has expired.");
            throw new ExpiredJwtException(null, null, "JWT token has expired");
        }
        return true;
    }

    private boolean isTokenExpired(String token) {
        log.debug("Checking if token is expired.");
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        log.debug("Extracting expiration date from token.");
        return extractClaim(token, Claims::getExpiration);
    }

    private Claims extractAllClaims(String token) {
        return Jwts
                .parserBuilder()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private Key getSignInKey() {
        log.debug("Generating sign-in key.");
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
