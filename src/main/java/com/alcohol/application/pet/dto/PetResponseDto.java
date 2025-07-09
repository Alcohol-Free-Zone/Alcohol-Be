package com.alcohol.application.pet.dto;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

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
    private List<String> tags;
    private List<PetAnniversaryDto> anniversaries;

    public static PetResponseDto from(Pet petList) {
        return PetResponseDto.builder()
                .petId(petList.getPetId())
                .imgUrl(petList.getImgUrl())
                .birth(petList.getBirth())
                .memo(petList.getMemo())
                .petName(petList.getPetName())
                .breed(petList.getBreed())
                .petAge(petList.getPetAge())
                .tags(petList.getPersonalityTags().stream()
                        .map(tag -> tag.getTag().name())
                        .toList())
                .anniversaries(petList.getPetAnniversaries().stream()
                        .map(PetAnniversaryDto::from)
                        .collect(Collectors.toList()))
                .build();
    }
}
