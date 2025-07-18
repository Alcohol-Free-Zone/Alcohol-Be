package com.alcohol.application.pet.dto;

import java.util.Date;
import java.util.List;

import com.alcohol.application.Enum.PersonalityTagType;
import com.alcohol.application.pet.entity.PetAnniversary;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PetAddRequest {
    private int petId;
     // 기본 반려동물 정보
    private Date birth;
    
    private String memo;
    
    private String petName;
    
    private String breed;
    
    private int petAge;

    private Long userId;

    // 성격 태그 여러 개
    private List<PersonalityTagType> tags;

    private List<PetAnniversary> anniversaries; // 반려동물 기념일 정보


}
