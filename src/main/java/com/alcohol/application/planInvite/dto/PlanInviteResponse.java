package com.alcohol.application.planInvite.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PlanInviteResponse {
    private Long planInviteId;
    private String status;
    private Long sendUserId;
    private Long receiverPetId;
}
