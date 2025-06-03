package com.alcohol.application.auth.Controller;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.alcohol.application.auth.service.OAuthService;
import com.alcohol.application.auth.dto.OAuthLoginRequestDto;
import com.alcohol.application.auth.dto.TokenResponseDto;

import lombok.RequiredArgsConstructor;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class OAuthController {

    private final OAuthService oAuthService;

    //  카카오 로그인 시작 (OAuthService에서 URL 생성)
    @GetMapping("/kakao/login")
    public void kakaoLogin(HttpServletResponse response) throws IOException {
        String kakaoAuthUrl = oAuthService.getKakaoLoginUrl();
        response.sendRedirect(kakaoAuthUrl);
    }

    //  구글 로그인 시작 (OAuthService에서 URL 생성)
    @GetMapping("/google/login")
    public void googleLogin(HttpServletResponse response) throws IOException {
        String googleAuthUrl = oAuthService.getGoogleLoginUrl();
        response.sendRedirect(googleAuthUrl);
    }

    // 카카오 로그인 (회원가입 자동 처리) 완료
    @PostMapping("/kakao")
    public ResponseEntity<TokenResponseDto> kakaoLogin(@RequestBody OAuthLoginRequestDto request) {
        TokenResponseDto response = oAuthService.processKakaoLogin(request.getCode());
        return ResponseEntity.ok(response);
    }

    // 구글 로그인 (회원가입 자동 처리) 완료
    @PostMapping("/google")
    public ResponseEntity<TokenResponseDto> googleLogin(@RequestBody OAuthLoginRequestDto request) {
        TokenResponseDto response = oAuthService.processGoogleLogin(request.getCode());
        return ResponseEntity.ok(response);
    }


    // 로그아웃
    @PostMapping("/logout")
    public ResponseEntity<String> logout(@RequestHeader("Authorization") String token) {
        oAuthService.logout(token.replace("Bearer ", ""));
        return ResponseEntity.ok("로그아웃 완료");
    }
}