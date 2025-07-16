package com.alcohol.config.filter;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import com.alcohol.application.userAccount.entity.UserAccount;
import com.alcohol.application.userAccount.repository.UserAccountRepository;
import com.alcohol.application.userAccount.service.UserAccountService;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
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
    private final UserAccountRepository userRepo;    // JPA 리포지토리

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {
        //요청에서 JWT 토큰 추출
        String token = resolveToken(request);
        
        // 1) JWT 검증 및 사용자 정보 확인
        if (StringUtils.hasText(token)) {
            try {

                // 2) 서명·만료 검증
                // 토큰 검증 실패 시 바로 응답 처리
                if (!jwtUtil.validateToken(token)) {
                    log.warn("Invalid JWT token");
                    handleJwtException(response, "유효하지 않은 토큰입니다.");
                    return;
                }

                // 토큰 만료 시 바로 응답 처리
                if (jwtUtil.isTokenExpired(token)) {
                    log.warn("JWT token expired");
                    handleJwtException(response, "토큰이 만료되었습니다. 다시 로그인해주세요.");
                    return;
                }


                // 3) s토큰에서 subject로 저장된 userId(Long)
                String userId = jwtUtil.getUserIdFromToken(token);

                // 4) DB에서 UserAccount 엔티티 로드
                UserAccount userAccount = userRepo.findById(Long.valueOf(userId))
                        .orElseThrow(() -> new UsernameNotFoundException("User not found"));


                // 5) 권한 세팅
                var authorities = List.of(
                        new SimpleGrantedAuthority(userAccount.getRole().name())
                );

                // 6) principal에 UserAccount를 넣고 인증 설정
                UsernamePasswordAuthenticationToken auth =
                        new UsernamePasswordAuthenticationToken(
                                userAccount,   // ← 여기!
                                null,
                                authorities
                        );

                auth.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request)
                );

                // 5) 시큐리티 컨텍스트에 넣기
                SecurityContextHolder.getContext().setAuthentication(auth);


            }catch (ExpiredJwtException ex) {
                log.warn("JWT token expired: {}", ex.getMessage());
                handleJwtException(response, "토큰이 만료되었습니다. 다시 로그인해주세요.");
                return;

            } catch (JwtException ex) {
                log.warn("JWT error: {}", ex.getMessage());
                handleJwtException(response, "유효하지 않은 토큰입니다.");
                return;

            } catch (UsernameNotFoundException ex) {
                log.warn("User not found: {}", ex.getMessage());
                handleJwtException(response, "사용자를 찾을 수 없습니다.");
                return;

            } catch (Exception ex) {
                log.error("Authentication error: {}", ex.getMessage());
                handleJwtException(response, "인증 처리 중 오류가 발생했습니다.");
                return;
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


    // JWT 예외 처리 응답 메서드
    private void handleJwtException(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType("application/json;charset=UTF-8");

        response.getWriter().write(message);
    }


}
