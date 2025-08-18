package com.alcohol.application.planInvite.service;

import com.alcohol.application.planInvite.dto.PlanInviteRequest;
import com.alcohol.application.userAccount.entity.UserAccount;

public interface PlanInviteService {

    void inviteUserToPlan(PlanInviteRequest planInviteRequest, UserAccount currentUser);
    
}
