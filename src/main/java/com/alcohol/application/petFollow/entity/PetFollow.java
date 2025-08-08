package com.alcohol.application.petFollow.entity;


import com.alcohol.application.pet.entity.Pet;
import com.alcohol.application.userAccount.entity.UserAccount;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@NoArgsConstructor
@Getter
@Setter
@Entity
@Builder
@AllArgsConstructor
// 중복 팔로우 방지를 위한 복합 유니크 키
@Table(uniqueConstraints = {
        @UniqueConstraint(columnNames = {"follower_id", "pet_id"})
})
public class PetFollow {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 팔로우 하는 사용자
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "followerId", nullable = false)
    private UserAccount follower;

    // 팔로우 당하는 펫
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "petId", nullable = false)
    private Pet pet;


    // 팔로우 날짜
    @Column(nullable = false)
    private LocalDateTime followedAt;

    public static PetFollow of(UserAccount  follower, Pet pet) {
        return PetFollow.builder()
                .follower(follower)
                .pet(pet)
                .followedAt(LocalDateTime.now())
                .build();
    }

    @PrePersist
    protected void onCreate() {
        if (this.followedAt == null) {
            this.followedAt = LocalDateTime.now();
        }
    }



}
