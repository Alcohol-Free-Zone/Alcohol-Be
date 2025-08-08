package com.alcohol.application.plan.service;

import com.alcohol.application.plan.dto.PlanCreateUpdateResponse;
import com.alcohol.application.plan.dto.PlanDetailDto;
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
     * 계획 조회
     */
    List<PlanDto> getPlansByUser(Long currentUser);

    /**
     * 계획 상세 조회
     */
    PlanDetailDto getPlanDetailById(Long planId);

    /**
     * 현재 일정에 관심 장소 추가
     */
    void addInterestPlace(Long planId, String contentId, Long userId);

    /**
     * 현재 일정에 관심 장소 삭제
     */    
    void deleteInterestPlace(Long planId, String contentId, Long id);

    /**
     * 계획 비활성화
     * @param long1 
     */
    void deactivatePlan(Long id, Long userId);
}
