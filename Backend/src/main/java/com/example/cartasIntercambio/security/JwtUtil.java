package com.example.cartasIntercambio.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;

import java.util.Date;
public class JwtUtil {

    private static final SecretKey  SECRET_KEY = Keys.secretKeyFor(SignatureAlgorithm.HS256);

    public static String generateToken(String username) {
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60))  // Expira en 1 hora
                .signWith(SECRET_KEY)
                .compact();
    }

    public static boolean isTokenExpired(String token) {
        Claims claims = getClaims(token);
        Date expirationDate = claims.getExpiration();
        return expirationDate.before(new Date());
    }

    public static Claims getClaims(String token) {
        return Jwts.parser()
                .setSigningKey(SECRET_KEY)
                .parseClaimsJws(token)
                .getBody();
    }

    public static String extractUsername(String token) {
        return getClaims(token).getSubject();
    }

    public static boolean isValidToken(String token, String username) {
        //veo si el nombre obtenido y la cookie concuerdan y además si no expiró
        return (username.equals(extractUsername(token)) && !isTokenExpired(token));
    }
}