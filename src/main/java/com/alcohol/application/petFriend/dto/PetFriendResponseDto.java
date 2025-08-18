package com.alcohol.application.petFriend.dto;

import com.alcohol.application.petFriend.entity.FriendStatus;
import com.alcohol.application.petFriend.entity.PetFriend;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter @Builder
public class PetFriendResponseDto {

    private Long id;                   // PetFriend PK
    private FriendStatus status;       // PENDING / ACCEPTED / REJECTED

    /* 요청 보낸 쪽 */
    private Long   requesterPetId;
    private String requesterPetName;
    private Long   requesterOwnerId;   // 사용자 ID (알람·권한용)

    /* 요청 받은 쪽 */
    private Long   receiverPetId;
    private String receiverPetName;
    private Long   receiverOwnerId;

    private LocalDateTime requestedAt;
    private LocalDateTime respondedAt;

    /* ---------- 엔터티 → DTO 변환 ---------- */
    public static PetFriendResponseDto from(PetFriend pf) {
        return PetFriendResponseDto.builder()
                .id(pf.getId())
                .status(pf.getStatus())
                .requesterPetId(pf.getRequesterPet().getPetId())
                .requesterPetName(pf.getRequesterPet().getPetName())
                .requesterOwnerId(pf.getRequesterUser().getId())
                .receiverPetId(pf.getReceiverPet().getPetId())
                .receiverPetName(pf.getReceiverPet().getPetName())
                .receiverOwnerId(pf.getReceiverUser().getId())
                .requestedAt(pf.getRequestedAt())
                .respondedAt(pf.getRespondedAt())
                .build();
    }
}