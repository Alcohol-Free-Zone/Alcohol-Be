package com.alcohol.application.auth.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OAuthLoginRequestDto {
    private String code; // OAuth 인가 코드
    private String state; // CSRF 방지용 state 파라미터 (선택사항)
}
