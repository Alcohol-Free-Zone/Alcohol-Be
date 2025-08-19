package com.alcohol.application.planInvite.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alcohol.application.pet.entity.Pet;
import com.alcohol.application.pet.repository.PetRepository;
import com.alcohol.application.planInvite.dto.PlanInviteRequest;
import com.alcohol.application.planInvite.dto.PlanInviteResponse;
import com.alcohol.application.planInvite.dto.PlanInviteStatusRequest;
import com.alcohol.application.planInvite.entity.InviteStatus;
import com.alcohol.application.planInvite.entity.PlanInvite;
import com.alcohol.application.planInvite.repository.PlanInviteRepository;
import com.alcohol.application.userAccount.entity.UserAccount;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class PlanInviteServiceImpl implements PlanInviteService {

    private final PlanInviteRepository planInviteRepository;
    private final PetRepository petRepository;

    public void inviteUserToPlan(PlanInviteRequest planInviteRequest, UserAccount currentUser) {
        // petIds 기반으로 반복 처리
        for (Long petId : planInviteRequest.getPetIds()) {
            Pet pet = petRepository.findById(petId)
                    .orElseThrow(() -> new IllegalArgumentException("해당 펫이 존재하지 않습니다. id=" + petId));

            UserAccount receiverUser = pet.getUserAccount(); // Pet 주인 찾기

            // PlanInvite 생성
            PlanInvite invite = new PlanInvite();
            invite.setSendUser(currentUser);
            invite.setReceiverPet(pet);
            invite.setReceiverUser(receiverUser);
            invite.setStatus(InviteStatus.PENDING);

            planInviteRepository.save(invite);
        }
    }

    public List<PlanInviteResponse> getPlanInvites(Long userId) {
        // 특정 유저(userId)가 받은 초대 목록 조회
        List<PlanInvite> invites = planInviteRepository.findByReceiverUser_Id(userId);

        // PlanInvite -> PlanInviteResponse 변환
        return invites.stream()
                .map(invite -> PlanInviteResponse.builder()
                        .planInviteId(invite.getPlanInviteId())
                        .status(invite.getStatus().name()) // Enum일 경우
                        .sendUserId(invite.getSendUser().getId())
                        .receiverPetId(invite.getReceiverPet().getPetId())
                        .createdAt(invite.getCreatedAt())
                        .build()
                )
                .collect(Collectors.toList());
    }

    public void updateInviteStatus(PlanInviteStatusRequest request, Long userId) {
        // receiverUser가 요청한 userId와 일치하는 PlanInvite 조회
        PlanInvite invite = planInviteRepository.findByPlanInviteIdAndReceiverUser_Id(
                            request.getPlanInviteId(), userId)
                    .orElseThrow(() -> new IllegalArgumentException("권한이 없거나 존재하지 않는 초대입니다."));

            // 이미 PENDING 상태가 아니면 권한 없음
            if (invite.getStatus() != InviteStatus.PENDING) {
                throw new IllegalArgumentException("이미 처리된 초대이거나 권한이 없습니다.");
            }

            // 상태 업데이트
            invite.setStatus(request.getStatus());

            // @PreUpdate에서 requestAt / rejectedAt 자동 처리
            planInviteRepository.save(invite);
        }
 
}
