package com.alcohol.application.planInvite.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alcohol.application.planInvite.dto.PlanInviteRequest;
import com.alcohol.application.userAccount.entity.UserAccount;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class PlanInviteServiceImpl implements PlanInviteService {

    public void inviteUserToPlan(PlanInviteRequest planInviteRequest, UserAccount currentUser) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'inviteUserToPlan'");
    }

   
}
