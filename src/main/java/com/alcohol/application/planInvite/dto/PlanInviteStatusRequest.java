package com.alcohol.application.planInvite.dto;

import com.alcohol.application.planInvite.entity.InviteStatus;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PlanInviteStatusRequest {
    private Long planInviteId;
    private InviteStatus status; // ACCEPTED 또는 REJECTED
}
