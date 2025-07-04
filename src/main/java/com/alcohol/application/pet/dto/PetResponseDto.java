package com.alcohol.application.pet.dto;

import com.alcohol.application.pet.entity.Pet;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PetResponseDto {
    private Long id;
    private String imgUrl;

    public static PetResponseDto from(Pet petList) {
        return PetResponseDto.builder()
                .id(petList.getId())
                .imgUrl(petList.getImgUrl())
                .build();
    }
}
