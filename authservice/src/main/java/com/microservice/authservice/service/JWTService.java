package com.microservice.authservice.service;

import cn.hutool.core.util.ObjectUtil;
import com.microservice.authservice.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
@Service("authJwtService")
public class JWTService {
    private static final String SECRET = "RZsxWR2OkQzHZzNdu+aSsnktWyQ8WkQPR0r4RMJ8WGo="; // Base64-encoded 256-bit key


    private Key getSignKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String generateToken(final User user) {
        final Map<String, Object> claims = new HashMap<>();
        claims.put("name", user.getName());
        claims.put("role", user.getRole());

        return Jwts.builder()
                .claims(claims)
                .subject(user.getUsername())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + 1000 * 60 * 30)) // 30 min
                .signWith(getSignKey())
                .compact();
    }

    public String generateRefreshToken(final User user) {
        return Jwts.builder()
                .subject(user.getUsername())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24)) // 1 day
                .signWith(getSignKey())
                .compact();
    }

    public String extractUserName(final String token) {
        return extractClaim(token, Claims::getSubject);
    }

    private <T> T extractClaim(final String token,final Function<Claims,T> claimResolver) {
        final Claims claims = extractAllClaims(token);
        return claimResolver.apply(claims);
    }

    private Claims extractAllClaims(final String token) {
//        if (token == null || token.isBlank()) {
//            throw new IllegalArgumentException("Token cannot be null or empty");
//        }
        if (ObjectUtil.isEmpty(token)) {
            throw new IllegalArgumentException("Token cannot be null or empty");
        }

        return Jwts.parser()
                .setSigningKey(getSignKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public boolean validateToken(final String token,final UserDetails userDetails) {
        final String userName = extractUserName(token);
        return userName.equals(userDetails.getUsername()) && !isTokenExpired(token);
    }

    private boolean isTokenExpired(final String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(final String token) {
        return extractClaim(token, Claims::getExpiration);
    }
}