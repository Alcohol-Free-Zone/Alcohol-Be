package com.alcohol.config.filter;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import io.jsonwebtoken.JwtException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import com.alcohol.config.token.JWTImpl;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JWTImpl jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {
        //요청에서 JWT 토큰 추출
        String token = resolveToken(request);
        
        // 1) JWT 검증 및 사용자 정보 확인
        if (StringUtils.hasText(token)) {
            try {

                // 2) 서명·만료 검증
                if (jwtUtil.validateToken(token) && !jwtUtil.isTokenExpired(token)) {
                    // 3) subject(userId)·role 꺼내기
                    String userId = jwtUtil.getUserIdFromToken(token);
                    String role = jwtUtil.getRoleFromToken(token);

                    // 4) 권한 세팅
                    var authorities = List.of(new SimpleGrantedAuthority(role));
                    var auth = new UsernamePasswordAuthenticationToken(
                            userId, null, authorities
                    );

                    auth.setDetails(
                            new org.springframework.security.web.authentication
                                    .WebAuthenticationDetailsSource()
                                    .buildDetails(request)
                    );

                    // 5) 시큐리티 컨텍스트에 넣기
                    SecurityContextHolder.getContext().setAuthentication(auth);

                }
            }catch (JwtException ex) {
                // 토큰 파싱 에러 시 컨텍스트 초기화
                logger.error("JWT error: {}",ex);
                SecurityContextHolder.clearContext();
            }
        }

        //다음 필터로 요청 전달
        chain.doFilter(request, response);
    }

    // Authorization 헤더에서 Bearer 제거
    private String resolveToken(HttpServletRequest request) {
        String header = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (StringUtils.hasText(header) && header.startsWith("Bearer ")) {
            return header.substring(7);
        }
        return null;
    }



}
