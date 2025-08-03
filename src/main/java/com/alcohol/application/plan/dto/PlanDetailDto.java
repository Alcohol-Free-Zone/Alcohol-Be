package com.alcohol.application.plan.dto;

import java.util.List;
import java.util.stream.Collectors;

import com.alcohol.application.plan.entity.Plan;
import com.alcohol.application.plan.entity.PlanContent;
import com.alcohol.application.plan.entity.PlanPet;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PlanDetailDto {
    private Long planId;
    private String planTitle;
    private String planStartDate;
    private String planEndDate;
    private List<Long> petIdList;
    private List<String> contentIdList;

    public static PlanDetailDto from(Plan plan, List<PlanPet> planPets, List<PlanContent> planContents) {
        List<Long> petIds = planPets.stream()
            .map(PlanPet::getPetId)
            .collect(Collectors.toList());

        List<String> contentIds = planContents.stream()
            .map(PlanContent::getContentId)
            .collect(Collectors.toList());

        return new PlanDetailDto(
            plan.getPlanId(),
            plan.getPlanTitle(),
            plan.getPlanStartDate(),
            plan.getPlanEndDate(),
            petIds,
            contentIds
        );
    }
}
