package com.makemytrip.makemytrip.config;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.security.auth.message.AuthException;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.stereotype.Component;
import com.makemytrip.makemytrip.models.Users;

import java.security.Key;
import java.util.Date;

@Component
public class Jwtutils {

    private final String SECRET = "jhbjbsjbckjdbskjckjdsbkcjbdsjbcjbdsjcdsbchjjdsjchdschjvdhcvhwdhdj";

    private Key getSignKey() {
        return Keys.hmacShaKeyFor(SECRET.getBytes());
    }

    public String generateToken(Users user) {
        return Jwts.builder()
                .setSubject(user.getEmail())
                .claim("email", user.getEmail())
                .claim("userId", user.getId())
                .claim("role", user.getRole())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 5184000000L))
                .signWith(getSignKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSignKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public String extractUsername(String token) throws AuthException {
        try {
            Claims claims = extractAllClaims(token);
            return claims.get("email", String.class);
        } catch (Exception e) {
            throw new AuthException("don not send wrong token please.......");
        }
    }

    public String extractUserId(String token) throws AuthException {
        try {
            Claims claims = extractAllClaims(token);
            System.out.println("CLAIMS: " + claims);
            return claims.get("userId", String.class);
        } catch (Exception e) {
            e.printStackTrace();
            throw new AuthException("do not send wrong token");
        }
    }

    public String extractRole(String token) {
        try {
            Claims claims = extractAllClaims(token);
            return claims.get("role", String.class);
        } catch (Exception e) {
            throw new AuthenticationCredentialsNotFoundException("not able to fetch role");
        }
    }

}