package com.alcohol.Util;

import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class JWTUtil {
    private final SecretKey secretKey;

    public JWTUtil(@Value("${spring.jwt.secret}") String key, int expDate) {
        this.secretKey = Keys.hmacShaKeyFor(key.getBytes());
        this.expDate = expDate;
    }

    /*
     * 
     * 현재 ACCESS 는 1분으로 설정 추후 30일 이후 세팅 필요
     * 
    */
    @Value("${spring.jwt.token-validity-one-min}")
    private final int expDate;
    
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


    
}
