package com.alcohol.application.plan.service;

import com.alcohol.application.plan.dto.PlanCreateUpdateResponse;
import com.alcohol.application.plan.dto.PlanDto;

import java.util.List;

import com.alcohol.application.plan.dto.PlanCreateUpdateRequest;
import com.alcohol.application.userAccount.entity.UserAccount;

public interface PlanService {
    /**
     * 계획 생성
     */
    PlanCreateUpdateResponse create(PlanCreateUpdateRequest planCreateUpdateRequest, UserAccount createUser);

    /**
     * 계획 수정
     */
    PlanCreateUpdateResponse update(PlanCreateUpdateRequest planCreateUpdateRequest, UserAccount createUser);

    /**
     * 계획 삭제
     */
    void deletePlan(Long planId);

    /**
     * 계획 조회
     */
    List<PlanDto> getPlansByUser(Long currentUser);

    /**
     * 특정 사용자의 계획 조회
     */
    // List<PlanResponseDto> getPlansByUserId(Long userId);

    /**
     * 계획 상세 조회
     */
    // PlanResponseDto getPlanById(Long planId);
}
