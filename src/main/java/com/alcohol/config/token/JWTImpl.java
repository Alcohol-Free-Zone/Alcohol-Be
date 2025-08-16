package com.alcohol.config.token;

import java.util.Date;

import javax.crypto.SecretKey;

import io.jsonwebtoken.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class JWTImpl {
    private final SecretKey secretKey;
    private final int expDate;
    private static final long REFRESH_TOKEN_VALIDITY_MS = 14L * 24 * 60 * 60 * 1000;

    public JWTImpl(@Value("${spring.jwt.secret}") String key, @Value("${spring.jwt.token-validity-one-min}") int expDate) {
        this.secretKey = Keys.hmacShaKeyFor(key.getBytes());
        this.expDate = expDate * 30 * 30 * 24 * 60 * 60 * 1000;
    }

    /*
     *
     * 현재 ACCESS 는 1분으로 설정 추후 30일 이후 세팅 필요
     *
    */


    // ACCESS TOKEN 생성
    public String createJwt(String userId, String role, String providerId) {
        return Jwts.builder()
                .setSubject(userId)
                .claim("role", role) // 권한
                .claim("provider", providerId) // 로그인 방식만 (필요시)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 100L * 365 * 24 * 60 * 60 * 1000))
                .signWith(secretKey, SignatureAlgorithm.HS256) 
                .compact();
         
    }

    public String createRefreshToken(String userId) {
        return Jwts.builder()
                .setSubject(userId)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + REFRESH_TOKEN_VALIDITY_MS))
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();
    }


    // 토큰 검증
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(secretKey).build().parseClaimsJws(token);
            return true;
        } catch (ExpiredJwtException e) {
            throw e;  // ExpiredJwtException은 다시 던짐
        } catch (JwtException | IllegalArgumentException e) {
            log.error("Invalid JWT token: {}", e.getMessage());
            return false;
        }
    }

    // JWT에서 사용자 ID 추출
    public String getUserIdFromToken(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();


        return claims.getSubject();

    }

    // JWT에서 역할 추출
    public String getRoleFromToken(String token) {
        JwtParser parser = Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build();

        Claims claims = parser
                .parseClaimsJws(token)
                .getBody();

        return claims.get("role", String.class);
    }

    // JWT에서 프로바이더 추출
    public String getProviderFromToken(String token) {
        JwtParser parser = Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build();

        Claims claims = parser
                .parseClaimsJws(token)
                .getBody();

        return claims.get("provider", String.class);
    }

    // 토큰 만료 확인
    public boolean isTokenExpired(String token) {
        try {
            JwtParser parser = Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build();

            Claims claims = parser
                    .parseClaimsJws(token)
                    .getBody();

            return claims.getExpiration().before(new Date());
        } catch (ExpiredJwtException e) {
            throw e;  // ExpiredJwtException은 다시 던짐
        } catch (JwtException e) {
            return true;
        }
    }




}
