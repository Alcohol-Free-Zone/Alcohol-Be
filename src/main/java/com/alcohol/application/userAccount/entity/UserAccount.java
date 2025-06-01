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

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;



}
