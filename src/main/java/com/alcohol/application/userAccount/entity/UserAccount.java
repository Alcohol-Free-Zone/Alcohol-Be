package com.alcohol.application.userAccount.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@NoArgsConstructor
@Getter
@Setter
@Entity
@Builder
@AllArgsConstructor
@Table(uniqueConstraints = {
        @UniqueConstraint(columnNames = {"provider", "providerId"})
})
public class UserAccount {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String provider; // "kakao" or "google"
    private String providerId;

    private String email;
    private String nickname;
    private String profileImage;

    @Column(nullable = false)
    private Boolean isActive = true;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role = Role.USER;


    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime lastLoginAt;

    // 소셜 로그인 정보 업데이트
    public UserAccount updateInfo(String nickname, String profileImage) {
        this.nickname = nickname;
        this.profileImage = profileImage;
        return this;
    }

    //마지막 로그인 시간 업데이트
    public UserAccount updateLastLoginAt() {
        this.lastLoginAt = LocalDateTime.now();
        return this;
    }



}
