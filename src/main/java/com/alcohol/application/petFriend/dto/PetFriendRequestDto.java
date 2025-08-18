package com.alcohol.application.petFriend.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @NoArgsConstructor
public class PetFriendRequestDto {

    /* 내 펫 ID */
    @NotNull
    private Long myPetId;

    /* 친구를 요청할 상대 펫 ID */
    @NotNull
    private Long targetPetId;
}