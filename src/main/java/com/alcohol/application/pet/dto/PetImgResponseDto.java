package com.alcohol.application.pet.dto;

import java.util.List;

import com.alcohol.application.pet.entity.Pet;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PetImgResponseDto {
    private Long id;
    private String imgUrl;

    public static PetImgResponseDto from(Pet petImgList) {
        return PetImgResponseDto.builder()
                .id(petImgList.getId())
                .imgUrl(petImgList.getImgUrl())
                .build();
    }
}
