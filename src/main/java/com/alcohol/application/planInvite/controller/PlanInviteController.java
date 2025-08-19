package com.alcohol.application.planInvite.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alcohol.application.planInvite.dto.PlanInviteRequest;
import com.alcohol.application.planInvite.dto.PlanInviteResponse;
import com.alcohol.application.planInvite.service.PlanInviteService;
import com.alcohol.application.userAccount.entity.UserAccount;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class PlanInviteController {
    
    private final PlanInviteService planInviteService;

    // 일정 초대하기
    @PostMapping("/plan-invite")
    public ResponseEntity<String> invitePlan(
        @RequestBody PlanInviteRequest planInviteRequest,
        @AuthenticationPrincipal UserAccount currentUser) {

        // 초대 로직 구현
        // 예시로 단순히 응답을 반환
        planInviteService.inviteUserToPlan(planInviteRequest, currentUser);
        return ResponseEntity.status(HttpStatus.CREATED).body("일정 초대 요청이 성공적으로 처리되었습니다.");
        
    }

    // 일정 상세조회
    @GetMapping("/plan-invite")
    public ResponseEntity<List<PlanInviteResponse>> getPlans(@AuthenticationPrincipal UserAccount currentUser) {
        List<PlanInviteResponse> invites = planInviteService.getPlanInvites(currentUser.getId());
        return ResponseEntity.ok(invites);
    }
}
