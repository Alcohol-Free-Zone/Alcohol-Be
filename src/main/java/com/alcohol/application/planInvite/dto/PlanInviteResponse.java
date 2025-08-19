package com.alcohol.application.planInvite.dto;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PlanInviteResponse {
    private Long planInviteId;
    private String status;
    private Long sendUserId;
    private Long receiverPetId;
    private LocalDateTime createdAt;
}
