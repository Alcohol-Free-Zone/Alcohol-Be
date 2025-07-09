package com.alcohol.application.pet.dto;

import java.util.Date;

import com.alcohol.application.pet.entity.PetAnniversary;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PetAnniversaryDto {
    private Long anniversaryId; // 기념일 ID
    private String title; // 기념일 제목
    private Date date; // 기념일 날짜
    private Long petId;

    
    public static PetAnniversaryDto from(PetAnniversary petAnniversary) {
        return PetAnniversaryDto.builder()
                .anniversaryId(petAnniversary.getAnniversaryId())
                .title(petAnniversary.getTitle())
                .date(petAnniversary.getDate())
                .petId(petAnniversary.getPet().getPetId())
                .build();
    }
}
