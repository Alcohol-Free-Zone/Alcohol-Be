package com.alcohol.application.planInvite.dto;

import java.util.List;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PlanInviteRequest {
    private List<Long> petIds;
}
