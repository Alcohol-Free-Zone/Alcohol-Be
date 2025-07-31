package com.alcohol.application.plan.service;

import org.springframework.stereotype.Service;

import com.alcohol.application.plan.repository.PlanRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class PlanServiceImpl implements PlanService {

    private final PlanRepository planRepository;

    @Override
    public void createPlan(Long userId, String planDetails) {
        // 계획 생성 로직 구현
        log.info("Plan created for user {}: {}", userId, planDetails);
    }

    @Override
    public void updatePlan(Long planId, String newDetails) {
        // 계획 수정 로직 구현
        log.info("Plan {} updated with details: {}", planId, newDetails);
    }

    @Override
    public void deletePlan(Long planId) {
        // 계획 삭제 로직 구현
        log.info("Plan {} deleted", planId);
    }

    // @Override
    // public List<PlanResponseDto> getPlansByUserId(Long userId) {
    //     // 특정 사용자의 계획 조회 로직 구현
    //     return Collections.emptyList(); // 예시로 빈 리스트 반환
    // }

    // @Override
    // public PlanResponseDto getPlanById(Long planId
    
}
