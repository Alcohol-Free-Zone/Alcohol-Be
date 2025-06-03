package com.alcohol.application.auth.dto;

import com.alcohol.application.userAccount.dto.UserAccountResponseDto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TokenResponseDto {
    private String accessToken;
    private String refreshToken;
    private String tokenType;
    private Long expiresIn;
    private UserAccountResponseDto userAccountResponseDto;
}
