package com.alcohol.application.plan.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alcohol.application.plan.dto.InterestContentIdRequest;
import com.alcohol.application.plan.dto.PlanCreateUpdateRequest;
import com.alcohol.application.plan.dto.PlanCreateUpdateResponse;
import com.alcohol.application.plan.dto.PlanDetailDto;
import com.alcohol.application.plan.dto.PlanDto;
import com.alcohol.application.plan.service.PlanService;
import com.alcohol.application.userAccount.entity.UserAccount;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/plan")
public class PlanController {
    private final PlanService planService;

    // 유저 일정 리스트 조회
    @GetMapping
    public ResponseEntity<List<PlanDto>> getPlanListByUser(@AuthenticationPrincipal UserAccount currentUser) {
        List<PlanDto> planList = planService.getPlansByUser(currentUser.getId());
        return ResponseEntity.ok(planList);
    }

    // 일정 상세조회
    @GetMapping("/{planId}")
    public ResponseEntity<PlanDetailDto> getPlanById(@PathVariable Long planId) {
        PlanDetailDto planDetail = planService.getPlanDetailById(planId);
        return ResponseEntity.ok(planDetail);
    }

    // 일정 추가, 수정하기
    @PostMapping
    public ResponseEntity<PlanCreateUpdateResponse> createOrUpdatePlan(
        @RequestBody PlanCreateUpdateRequest planCreateUpdateRequest,
        @AuthenticationPrincipal UserAccount createUser) {
        if (planCreateUpdateRequest.getPlanId() == null) {
            // 신규 생성 로직
            PlanCreateUpdateResponse created = planService.create(planCreateUpdateRequest, createUser);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } else {
            // 수정 로직
            PlanCreateUpdateResponse updated = planService.update(planCreateUpdateRequest, createUser);
            return ResponseEntity.ok(updated);
        }
    }

    // 일정 초대하기
    @PostMapping("/{id}/invite")
    public ResponseEntity<?> inviteUserToPlan(@PathVariable Long id) {
        return ResponseEntity.ok(null);
    }

    // 관심, 일정에 등록하기
    @PostMapping("/{planId}/interest-places")
    public ResponseEntity<String> addInterestPlace(
        @PathVariable Long planId,
        @RequestBody InterestContentIdRequest request,
        @AuthenticationPrincipal UserAccount currentUser
    ) {
        planService.addInterestPlace(planId, request.getContentId(), currentUser.getId());
        return ResponseEntity.ok("관심 장소 등록 완료");
    }

    // 관심, 일정에 등록 해제하기
    @DeleteMapping("/{planId}/interest-places")
    public ResponseEntity<String> deleteInterestPlace(
        @PathVariable Long planId,
        @RequestBody InterestContentIdRequest request,
        @AuthenticationPrincipal UserAccount currentUser
    ) {
        planService.deleteInterestPlace(planId, request.getContentId(), currentUser.getId());
        return ResponseEntity.ok("관심 장소 등록 해제 완료");
    }
    
    // 일정 비활성화
    @PatchMapping("/{id}")
    public ResponseEntity<String> deactivatePlan(
        @PathVariable Long id,
        @AuthenticationPrincipal UserAccount currentUser
        ) {
        planService.deactivatePlan(id, currentUser.getId());
        return ResponseEntity.ok("일정 비활성화 완료");
    }
}
