package com.alcohol.application.plan.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.alcohol.application.plan.dto.PlanCreateUpdateRequest;
import com.alcohol.application.plan.dto.PlanCreateUpdateResponse;
import com.alcohol.application.plan.dto.PlanDetailDto;
import com.alcohol.application.plan.dto.PlanDto;
import com.alcohol.application.plan.entity.Plan;
import com.alcohol.application.plan.entity.PlanContent;
import com.alcohol.application.plan.entity.PlanPet;
import com.alcohol.application.plan.entity.PlanUser;
import com.alcohol.application.plan.repository.PlanContentRepository;
import com.alcohol.application.plan.repository.PlanPetRepository;
import com.alcohol.application.plan.repository.PlanRepository;
import com.alcohol.application.plan.repository.PlanUserRepository;
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
    private final PlanUserRepository planUserRepository;

    public PlanCreateUpdateResponse create(PlanCreateUpdateRequest planCreateUpdateRequest, UserAccount createUser) {

        Plan plan = Plan.builder()
            .planTitle(planCreateUpdateRequest.getPlanTitle())
            .createUserId(createUser)
            .build();

        Plan savedPlan = planRepository.save(plan);

        PlanUser planUser = PlanUser.builder()
            .user(createUser)
            .plan(savedPlan)
            .build();
            
        planUserRepository.save(planUser);

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
        List<Plan> planEntities = planRepository.findAllByCreateUserId_IdAndIsActive(currentUser, "Y");
        return planEntities.stream()
            .map(PlanDto::from)
            .collect(Collectors.toList());
    }
    
    public PlanDetailDto getPlanDetailById(Long planId) {
        Plan plan = planRepository.findByPlanIdAndIsActive(planId, "Y")
            .orElseThrow(() -> new IllegalArgumentException("해당 일정이 존재하지 않거나 비활성 상태입니다."));

        List<PlanPet> planPets = planPetRepository.findAllByPlan(plan);
        List<PlanContent> planContents = planContentRepository.findAllByPlan(plan);

        return PlanDetailDto.from(plan, planPets, planContents);
    }

    public void addInterestPlace(Long planId, String contentId, Long userId) {
        Plan plan = planRepository.findById(planId)
            .orElseThrow(() -> new EntityNotFoundException("해당 일정이 존재하지 않습니다."));

        boolean isParticipant = planUserRepository.existsByPlanAndUser_Id(plan, userId);
        if (!isParticipant) {
            throw new SecurityException("해당 일정에 참여한 사용자만 등록할 수 있습니다.");
        }

        boolean exists = planContentRepository.existsByPlanAndContentId(plan, contentId);
        if (exists) {
            throw new IllegalStateException("이미 등록된 관심 장소입니다.");
        }

        PlanContent interestPlace = PlanContent.builder()
            .plan(plan)
            .contentId(contentId)
            .build();

        planContentRepository.save(interestPlace);
    }

    public void deleteInterestPlace(Long planId, String contentId, Long userId) {
        Plan plan = planRepository.findById(planId)
            .orElseThrow(() -> new EntityNotFoundException("해당 일정이 존재하지 않습니다."));

        boolean isParticipant = planUserRepository.existsByPlanAndUser_Id(plan, userId);
        if (!isParticipant) {
            throw new SecurityException("해당 일정에 참여한 사용자만 해제 할 수 있습니다.");
        }

        boolean exists = planContentRepository.existsByPlanAndContentId(plan, contentId);
        if (!exists) {
            throw new IllegalStateException("이미 등록된 관심 장소가 아닙니다.");
        }

        PlanContent interestPlace = planContentRepository
            .findByPlanAndContentId(plan, contentId)
            .orElseThrow(() -> new IllegalStateException("이미 등록된 관심 장소가 아닙니다."));

        planContentRepository.delete(interestPlace);
    }

    public void deactivatePlan(Long id, Long userId) {
        Plan plan = planRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("해당 일정이 존재하지 않습니다."));

        boolean isParticipant = planUserRepository.existsByPlanAndUser_Id(plan, userId);
        if (!isParticipant) {
            throw new IllegalArgumentException("해당 일정에 참여한 사용자만 비활성화 할 수 있습니다.");
        }

        plan.setIsActive("N");
        planRepository.save(plan);
    }
}
