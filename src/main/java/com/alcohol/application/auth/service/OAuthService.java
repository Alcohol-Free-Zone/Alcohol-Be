package com.alcohol.application.auth.service;

import com.alcohol.application.auth.dto.TokenResponseDto;

public interface OAuthService {
    String getKakaoLoginUrl();
    String getGoogleLoginUrl();
    public TokenResponseDto processKakaoLogin(String code);
    public TokenResponseDto processGoogleLogin(String code);
    void logout(String token);


}
