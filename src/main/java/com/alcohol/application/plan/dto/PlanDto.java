package com.alcohol.application.plan.dto;

import java.util.List;
import java.util.stream.Collectors;

import com.alcohol.application.plan.entity.Plan;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PlanDto {
    private Long id;
    private String title;
    private List<ContentInfoDto> contentInfo;

    public static PlanDto from(Plan plan) {
        return new PlanDto(plan.getPlanId(), plan.getPlanTitle(), plan.getContents().stream()
            .map(ContentInfoDto::from)
            .collect(Collectors.toList())
            );
    }
}