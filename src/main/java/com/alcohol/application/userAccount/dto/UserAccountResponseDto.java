package com.alcohol.application.userAccount.dto;

import java.time.LocalDateTime;

import com.alcohol.application.userAccount.entity.UserAccount;

import com.alcohol.common.files.dto.FileResponseDto;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserAccountResponseDto {
    private Long id;
    private String email;
    private String nickname;
    private String profileImage;
    private String provider;
    private String providerId;
    private FileResponseDto profile;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Entity에서 DTO로 변환하는 정적 메서드
    public static UserAccountResponseDto from(UserAccount userAccount) {
        return UserAccountResponseDto.builder()
                .id(userAccount.getId())
                .email(userAccount.getEmail())
                .nickname(userAccount.getNickname())
                .profileImage(userAccount.getProfileImage())
                .provider(userAccount.getProvider())
                .providerId(userAccount.getProviderId())
                .profile(userAccount.getProfileSafely())
                .createdAt(userAccount.getCreatedAt())
                .updatedAt(userAccount.getUpdatedAt())
                .build();
    }
}
