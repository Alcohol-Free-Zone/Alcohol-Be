package com.alcohol.application.pet.dto;

import java.util.Date;

import com.alcohol.application.pet.entity.Pet;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PetResponseDto {
    private Long petId;
    private String imgUrl;
    private Date birth;
    private String memo;
    private String petName;
    private String breed;
    private int petAge;

    public static PetResponseDto from(Pet petList) {
        return PetResponseDto.builder()
                .petId(petList.getPetId())
                .imgUrl(petList.getImgUrl())
                .birth(petList.getBirth())
                .memo(petList.getMemo())
                .petName(petList.getPetName())
                .breed(petList.getBreed())
                .petAge(petList.getPetAge())
                .build();
    }
}
