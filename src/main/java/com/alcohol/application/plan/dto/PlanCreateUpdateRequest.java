package com.alcohol.application.plan.dto;

import java.util.List;

import lombok.Getter;

@Getter
public class PlanCreateUpdateRequest {
    private Long planId;
    private List<Long> petIdList;
    private String planTitle;
    private List<ContentInfoDto> contentList;
}
