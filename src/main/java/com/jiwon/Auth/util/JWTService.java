package com.cognizant.Auth.util;


import com.cognizant.Auth.model.ResponseDto;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;


public class JWTService {
    private static final SecretKey key = Keys.secretKeyFor(SignatureAlgorithm.HS256);
    private static JWTService instance;

    private JWTService(){

    }

    public static JWTService getInstance() {
        if(JWTService.instance == null) {
            instance = new JWTService();
        }
        return instance;
    }

    public static String generateToken(ResponseDto dto) {
        return Jwts.builder()
                .claim("username", dto.getUsername())
                .claim("userId", dto.getId())
                .claim("permissions", "USER")
                .setIssuedAt(Date.from(Instant.now()))
                .setExpiration(Date.from(Instant.now().plus(10, ChronoUnit.DAYS)))
                .signWith(key)
                .compact();
    }

    public static boolean parseToken(String token) {
        try {
            Jws<Claims> claims = Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);

            return (checkExpiration(claims.getBody()));
        } catch (IllegalArgumentException | SignatureException | ExpiredJwtException e) {
            return false;
        }
    }

    private static boolean checkExpiration(Claims claims) {
        return claims.getExpiration().after(Date.from(Instant.now()));
    }

    public static String setExpiration(String token, Date newDate){
        try {
            Jws<Claims> claims = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token);
            Claims newClaims = claims.getBody().setExpiration(newDate);
            return Jwts.builder()
                    .setClaims(newClaims)
                    .signWith(key)
                    .compact();

        } catch (IllegalArgumentException | SignatureException e) {
            return token;
        }
    }
}
