package com.alcohol.application.planInvite.service;

import java.util.List;

import com.alcohol.application.planInvite.dto.PlanInviteRequest;
import com.alcohol.application.planInvite.dto.PlanInviteResponse;
import com.alcohol.application.userAccount.entity.UserAccount;

public interface PlanInviteService {

    void inviteUserToPlan(PlanInviteRequest planInviteRequest, UserAccount currentUser);

    List<PlanInviteResponse> getPlanInvites(Long planId);
    
}
