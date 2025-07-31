package com.alcohol.application.plan.service;

public interface PlanService {
    /**
     * 계획 생성
     */
    void createPlan(Long userId, String planDetails);

    /**
     * 계획 수정
     */
    void updatePlan(Long planId, String newDetails);

    /**
     * 계획 삭제
     */
    void deletePlan(Long planId);

    /**
     * 특정 사용자의 계획 조회
     */
    // List<PlanResponseDto> getPlansByUserId(Long userId);

    /**
     * 계획 상세 조회
     */
    // PlanResponseDto getPlanById(Long planId);
}
