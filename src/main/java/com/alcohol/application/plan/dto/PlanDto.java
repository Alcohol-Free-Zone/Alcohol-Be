package com.alcohol.application.plan.dto;

import com.alcohol.application.plan.entity.Plan;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PlanDto {
    private Long id;
    private String title;

    public static PlanDto from(Plan plan) {
        return new PlanDto(plan.getPlanId(), plan.getPlanTitle());
    }
}