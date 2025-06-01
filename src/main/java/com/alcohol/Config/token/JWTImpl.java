package com.alcohol.Config.token;

import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class JWTImpl {
    private final SecretKey secretKey;
    private final int expDate;

    public JWTImpl(@Value("${spring.jwt.secret}") String key, @Value("${spring.jwt.token-validity-one-min}") int expDate) {
        this.secretKey = Keys.hmacShaKeyFor(key.getBytes());
        this.expDate = expDate;
    }

    /*
     *
     * 현재 ACCESS 는 1분으로 설정 추후 30일 이후 세팅 필요
     *
    */


    // ACCESS TOKEN 생성
    public String createJwt(String userId, String role) {
        return Jwts.builder()
                .setSubject(userId) 
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expDate))
                .signWith(secretKey, SignatureAlgorithm.HS256) 
                .compact();
         
    }

    // 토큰 검증
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(secretKey).build().parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            log.error("Invalid JWT token: {}", e.getMessage());
            return false;
        }
    }

    // JWT에서 사용자 ID 추출
    public String getUserIdFromToken(String token) {
        JwtParser parser = Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build();

        Claims claims = parser
                .parseClaimsJws(token)
                .getBody();

        return claims.get("userId", String.class);

    }


    
}
