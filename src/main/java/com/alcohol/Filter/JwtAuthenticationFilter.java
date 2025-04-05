package com.alcohol.Filter;

import java.io.IOException;
import java.util.Collections;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import com.alcohol.Util.JWTUtil;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JWTUtil jWTUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {
        //요청에서 JWT 토큰 추출
        String token = getTokenFromRequest(request); 
        
        //JWT 검증 및 사용자 정보 확인
        if (StringUtils.hasText(token) && jWTUtil.validateToken(token)) {
            String userId = jWTUtil.getUserIdFromToken(token);
            UserDetails userDetails = new User(userId, "", Collections.emptyList()); //Spring Security User 객체 생성
            UsernamePasswordAuthenticationToken authentication =new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities()); //인증 객체 생성 및 SecurityContext에 저장
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
