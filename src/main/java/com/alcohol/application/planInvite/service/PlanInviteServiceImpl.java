package com.alcohol.application.planInvite.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alcohol.application.pet.entity.Pet;
import com.alcohol.application.pet.repository.PetRepository;
import com.alcohol.application.planInvite.dto.PlanInviteRequest;
import com.alcohol.application.planInvite.dto.PlanInviteResponse;
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

        return invites.stream()
        .map(invite -> {
            return PlanInviteResponse.builder()
                    .planInviteId(invite.getPlanInviteId())
                    .status(invite.getStatus().name())
                    .sendUserId(invite.getSendUser().getId())
                    .receiverPetId(invite.getReceiverPet().getId())
                    .build();
        })
        .collect(Collectors.toList());
    }

   
}
