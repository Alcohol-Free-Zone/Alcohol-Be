package com.alcohol.application.pet.dto;

import java.util.Date;
import java.util.List;

import com.alcohol.application.Enum.PersonalityTagType;
import com.alcohol.application.pet.entity.PetAnniversary;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PetAddResponse {
    private Long petId;

    private String petName;

    private String breed;

    private int petAge;

    private Date birth;

    private String memo;

    private String imgUrl;

    private List<PersonalityTagType> tags;

    private List<PetAnniversary> anniversary;

}
