package com.alcohol.application.plan.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.alcohol.application.pet.entity.Pet;
import com.alcohol.application.pet.repository.PetRepository;
import com.alcohol.application.plan.dto.PlanCreateUpdateRequest;
import com.alcohol.application.plan.dto.PlanCreateUpdateResponse;
import com.alcohol.application.plan.dto.PlanDto;
import com.alcohol.application.plan.entity.Plan;
import com.alcohol.application.plan.entity.PlanContent;
import com.alcohol.application.plan.entity.PlanPet;
import com.alcohol.application.plan.repository.PlanContentRepository;
import com.alcohol.application.plan.repository.PlanPetRepository;
import com.alcohol.application.plan.repository.PlanRepository;
import com.alcohol.application.userAccount.entity.UserAccount;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class PlanServiceImpl implements PlanService {

    private final PlanRepository planRepository;
    private final PlanPetRepository planPetRepository;
    private final PlanContentRepository planContentRepository;
    private final PetRepository petRepository;

    public PlanCreateUpdateResponse create(PlanCreateUpdateRequest planCreateUpdateRequest, UserAccount createUser) {

        Plan plan = Plan.builder()
            .planTitle(planCreateUpdateRequest.getPlanTitle())
            .createUserId(createUser)
            .build();

        Plan savedPlan = planRepository.save(plan);

        for(Long petId : planCreateUpdateRequest.getPetIdList()){
            PlanPet planPet = PlanPet.builder()
                .plan(savedPlan)
                .petId(petId)
                .user(createUser)
                .build();

            planPetRepository.save(planPet);
       }

       for (String contentId : planCreateUpdateRequest.getContentIdList()) {
            PlanContent planContent = PlanContent.builder()
                .plan(savedPlan)
                .contentId(contentId) // Assuming contentIdList is a list of strings
                .build();

            planContentRepository.save(planContent);
        }
        
        return PlanCreateUpdateResponse.from(savedPlan);
    }

    public PlanCreateUpdateResponse update(PlanCreateUpdateRequest request, UserAccount user) {                        
        Plan plan = planRepository.findById(request.getPlanId())
            .orElseThrow(() -> new EntityNotFoundException("해당 계획이 존재하지 않습니다."));

        if (!plan.getCreateUserId().getId().equals(user.getId())) {
            throw new SecurityException("해당 계획을 수정할 권한이 없습니다.");
        }

        plan.setPlanTitle(request.getPlanTitle());

        planPetRepository.deleteByPlan(plan);
        planContentRepository.deleteByPlan(plan);

        for (Long petId : request.getPetIdList()) {
            PlanPet planPet = PlanPet.builder()
                .plan(plan)
                .petId(petId)
                .user(user)
                .build();

            planPetRepository.save(planPet);
        }
        
        for (String contentId : request.getContentIdList()) {
            PlanContent planContent = PlanContent.builder()
                .plan(plan)
                .contentId(contentId)
                .build();

            planContentRepository.save(planContent);
        }

        return PlanCreateUpdateResponse.from(plan);
    }

    public void deletePlan(Long planId) {
        // 계획 삭제 로직 구현
        log.info("Plan {} deleted", planId);
    }

    public List<PlanDto> getPlansByUser(Long currentUser) {
        List<Plan> planEntities = planRepository.findAllByCreateUserId_Id(currentUser);
        return planEntities.stream()
            .map(PlanDto::from)
            .collect(Collectors.toList());
    }

    // @Override
    // public List<PlanResponseDto> getPlansByUserId(Long userId) {
    //     // 특정 사용자의 계획 조회 로직 구현
    //     return Collections.emptyList(); // 예시로 빈 리스트 반환
    // }

    // @Override
    // public PlanResponseDto getPlanById(Long planId
    
}
