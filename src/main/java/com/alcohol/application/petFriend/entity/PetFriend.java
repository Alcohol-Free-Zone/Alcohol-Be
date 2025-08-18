package com.alcohol.application.petFriend.entity;

import com.alcohol.application.pet.entity.Pet;
import com.alcohol.application.userAccount.entity.UserAccount;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(uniqueConstraints = {
        @UniqueConstraint(columnNames = {"requester_pet_id", "receiver_pet_id"})
})
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PetFriend {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)      // 친구 신청 보낸 펫
    @JoinColumn(name = "requester_pet_id", nullable = false)
    private Pet requesterPet;

    @ManyToOne(fetch = FetchType.LAZY)      // 친구 신청 받은 펫
    @JoinColumn(name = "receiver_pet_id", nullable = false)
    private Pet receiverPet;

    // 알람·권한 확인용
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "requester_user_id", nullable = false)
    private UserAccount requesterUser;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receiver_user_id", nullable = false)
    private UserAccount receiverUser;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FriendStatus status;

    private LocalDateTime requestedAt;
    private LocalDateTime respondedAt;

    @PrePersist
    protected void onCreate() {
        if (requestedAt == null) requestedAt = LocalDateTime.now();
    }

    /* ---------- 팩터리 & 상태 전환 ---------- */
    public static PetFriend create(Pet from, Pet to) {
        return PetFriend.builder()
                .requesterPet(from)
                .receiverPet(to)
                .requesterUser(from.getUserAccount())
                .receiverUser(to.getUserAccount())
                .status(FriendStatus.PENDING)
                .requestedAt(LocalDateTime.now())
                .build();
    }
    public void accept() {
        this.status = FriendStatus.ACCEPTED;
        this.respondedAt = LocalDateTime.now();
    }
    public void reject() {
        this.status = FriendStatus.REJECTED;
        this.respondedAt = LocalDateTime.now();
    }
}
