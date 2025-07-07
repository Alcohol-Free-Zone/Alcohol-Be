package com.alcohol.config.token;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JWTImpl jwtService;

    public JwtAuthenticationFilter(JWTImpl jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        // 1) 헤더에서 “Bearer {token}” 꺼내기
        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            String token = header.substring(7);

            // 2) 서명·만료 검증
            if (jwtService.validateToken(token)) {
                // 3) 토큰이 정상이라면, Subject(userId) 추출
                String userId = jwtService.getUserIdFromToken(token);

                // 4) 권한(role) 등 추가 클레임 추출(Optional)
                String role = jwtService.getRoleFromToken(token);
                List<GrantedAuthority> authorities =
                        List.of(new SimpleGrantedAuthority(role));

                // 5) 스프링 시큐리티 컨텍스트에 인증 정보 저장
                UsernamePasswordAuthenticationToken auth =
                        new UsernamePasswordAuthenticationToken(userId, null, authorities);
                SecurityContextHolder.getContext().setAuthentication(auth);
            }
        }

        // 다음 필터로 진행
        filterChain.doFilter(request, response);
    }
}

