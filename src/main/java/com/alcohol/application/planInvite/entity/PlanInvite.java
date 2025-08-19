package com.alcohol.application.planInvite.entity;

import java.time.LocalDateTime;

import com.alcohol.application.pet.entity.Pet;
import com.alcohol.application.userAccount.entity.UserAccount;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PlanInvite {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long planInviteId;

    // 초대한 유저 (보낸 사람)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "send_user_id", nullable = false)
    private UserAccount sendUser;

    // 초대받은 반려동물
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receiver_pet_id", nullable = false)
    private Pet receiverPet;

    // 초대받은 유저 (Pet 주인)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receiver_user_id", nullable = false)
    private UserAccount receiverUser;

    // 보낸 날짜
    private LocalDateTime createdAt;

    // 상태 (Enum)
    @Enumerated(EnumType.STRING)
    private InviteStatus status;

    // 수락 날짜
    private LocalDateTime requestAt;

    // 거절 날짜
    private LocalDateTime rejectedAt;

    @PrePersist
    public void onCreate() {
        this.createdAt = LocalDateTime.now();
        if (this.status == null) {
            this.status = InviteStatus.PENDING;
        }
    }

    @PreUpdate
    public void onUpdate() {
        if (this.status == InviteStatus.ACCEPTED && this.requestAt == null) {
            this.requestAt = LocalDateTime.now();
        }
        if (this.status == InviteStatus.REJECTED && this.rejectedAt == null) {
            this.rejectedAt = LocalDateTime.now();
        }
    }
}
