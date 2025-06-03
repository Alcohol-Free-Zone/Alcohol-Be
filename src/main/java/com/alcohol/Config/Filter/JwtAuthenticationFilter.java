package com.alcohol.Config.Filter;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import com.alcohol.Config.token.JWTImpl;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JWTImpl jWTUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {
        //요청에서 JWT 토큰 추출
        String token = getTokenFromRequest(request); 
        
        //JWT 검증 및 사용자 정보 확인
        if (StringUtils.hasText(token) && jWTUtil.validateToken(token) && !jWTUtil.isTokenExpired(token)) {
            // 토큰에서 사용자 정보 추출
            String userId = jWTUtil.getUserIdFromToken(token);
            String role = jWTUtil.getRoleFromToken(token);
            String provider = jWTUtil.getProviderFromToken(token);
            // 권한 객체 생성
            Collection<GrantedAuthority> authorities = Collections.singletonList(
                    new SimpleGrantedAuthority(role)
            );
            // Spring Security 기본 User 객체 사용
            UserDetails userDetails = User.builder()
                    .username(userId)
                    .password("") // OAuth 사용자는 패스워드 없음
                    .authorities(authorities)
                    .build();
            UsernamePasswordAuthenticationToken authentication =new UsernamePasswordAuthenticationToken(userDetails, null, authorities); //인증 객체 생성 및 SecurityContext에 저장
            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        //다음 필터로 요청 전달
        chain.doFilter(request, response);
    }

    // 요청 헤더에서 JWT 추출
    private String getTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }


    
}
