package com.alcohol.application.userAccount.entity;

import com.alcohol.common.files.entity.File;
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

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "file_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private File profile;

    @Column(nullable = false)
    private Boolean isActive = true;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role = Role.USER;


    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime lastLoginAt;

    // 소셜 로그인 정보 업데이트
    public UserAccount updateInfo(String nickname, String email, String profileImage) {
        this.nickname = nickname;
        this.email = email;
        //이미지 변경은 일단 url로만 되게 설정 추후 이미지 등록후 경로 가질수있게 변경예정
        this.profileImage = profileImage;
        return this;
    }

    // 계정 활성화 여부
    public UserAccount updateActive(Boolean isActive) {
        this.isActive = isActive;
        return this;
    }

    //마지막 로그인 시간 업데이트
    public UserAccount updateLastLoginAt() {
        this.lastLoginAt = LocalDateTime.now();
        return this;
    }

    //업데이트 시간
    public UserAccount updateAt() {
        this.updatedAt = LocalDateTime.now();
        return this;
    }



}
