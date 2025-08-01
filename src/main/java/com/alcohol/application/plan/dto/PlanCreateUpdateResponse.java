package com.alcohol.application.plan.dto;

import com.alcohol.application.plan.entity.Plan;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class PlanCreateUpdateResponse {
    private Long planId;

    public static PlanCreateUpdateResponse from(Plan plan) {
        return new PlanCreateUpdateResponse(plan.getPlanId());
    }
}
