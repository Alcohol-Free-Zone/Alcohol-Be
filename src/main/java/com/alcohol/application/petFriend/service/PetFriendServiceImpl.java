package com.alcohol.application.petFriend.service;

import com.alcohol.application.pet.dto.PetResponseDto;
import com.alcohol.application.pet.entity.Pet;
import com.alcohol.application.pet.repository.PetRepository;
import com.alcohol.application.petFriend.dto.PetFriendResponseDto;
import com.alcohol.application.petFriend.entity.FriendStatus;
import com.alcohol.application.petFriend.entity.PetFriend;
import com.alcohol.application.petFriend.repository.PetFriendRepository;
import com.alcohol.application.userAccount.repository.UserAccountRepository;
import com.alcohol.util.pagination.PageResponseDto;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional          // jakarta.transaction, readOnly 속성 없음
public class PetFriendServiceImpl implements PetFriendService {

    private final PetFriendRepository  petFriendRepo;
    private final PetRepository        petRepo;

    /* ---------------------- 친구 신청 ---------------------- */
    @Override
    public void sendRequest(Long myPetId, Long targetPetId, Long loginUserId) {

        if (myPetId.equals(targetPetId))
            throw new IllegalArgumentException("같은 펫에게는 요청할 수 없습니다.");

        Pet myPet     = petRepo.findById(myPetId)
                .orElseThrow(() -> new IllegalArgumentException("내 펫이 존재하지 않습니다."));
        Pet targetPet = petRepo.findById(targetPetId)
                .orElseThrow(() -> new IllegalArgumentException("대상 펫이 존재하지 않습니다."));

        if (!myPet.getUserAccount().getId().equals(loginUserId))
            throw new IllegalArgumentException("해당 펫의 주인이 아닙니다.");

        petFriendRepo.findRelationAnyStatus(myPetId, targetPetId)
                .ifPresent(r -> { throw new IllegalArgumentException("이미 요청 또는 친구 상태입니다."); });

        petFriendRepo.save(PetFriend.create(myPet, targetPet));
    }

    /* ---------------------- 친구 수락 ---------------------- */
    @Override
    public void acceptRequest(Long requestId, Long loginUserId) {
        PetFriend pf = petFriendRepo.findById(requestId)
                .orElseThrow(() -> new IllegalArgumentException("요청이 존재하지 않습니다."));

        if (!pf.getReceiverUser().getId().equals(loginUserId))
            throw new IllegalArgumentException("수락 권한이 없습니다.");

        if (pf.getStatus() != FriendStatus.PENDING)
            throw new IllegalStateException("이미 처리된 요청입니다.");

        pf.accept();
    }

    /* ---------------------- 친구 거절 ---------------------- */
    @Override
    public void rejectRequest(Long requestId, Long loginUserId) {
        PetFriend pf = petFriendRepo.findById(requestId)
                .orElseThrow(() -> new IllegalArgumentException("요청이 존재하지 않습니다."));

        if (!pf.getReceiverUser().getId().equals(loginUserId))
            throw new IllegalArgumentException("거절 권한이 없습니다.");

        if (pf.getStatus() != FriendStatus.PENDING)
            throw new IllegalStateException("이미 처리된 요청입니다.");

        pf.reject();
        petFriendRepo.delete(pf); //거절직후 해당 친구 신청 삭제
    }

    /* ---------------------- 친구 끊기 ---------------------- */
    @Override
    public void removeFriend(Long myPetId, Long targetPetId, Long loginUserId) {
        PetFriend pf = petFriendRepo.findRelation(myPetId, targetPetId, FriendStatus.ACCEPTED)
                .orElseThrow(() -> new IllegalArgumentException("친구 관계가 없습니다."));

        boolean owner =
                pf.getRequesterUser().getId().equals(loginUserId) ||
                        pf.getReceiverUser().getId().equals(loginUserId);
        if (!owner)
            throw new IllegalArgumentException("삭제 권한이 없습니다.");

        petFriendRepo.delete(pf);
    }

    /* ---------------------- 상태 확인 ---------------------- */
    @Override
    public FriendStatus getFriendStatus(Long myPetId, Long targetPetId) {
        return petFriendRepo.findRelationAnyStatus(myPetId, targetPetId)
                .map(PetFriend::getStatus)
                .orElse(null);   // null = 관계 없음
    }

    /* ---------------------- 받은 요청 목록 ---------------------- */
    @Override
    public PageResponseDto<PetFriendResponseDto> receivedRequests(Long loginUserId,
                                                                  Pageable pageable) {

        var page = petFriendRepo.findByReceiverUserIdAndStatus(
                loginUserId, FriendStatus.PENDING, pageable);

        List<PetFriendResponseDto> dtoList = page.getContent()
                .stream()
                .map(PetFriendResponseDto::from)
                .toList();

        return new PageResponseDto<>(
                dtoList,
                page.hasNext(),
                page.getTotalElements(),
                page.getNumber(),
                page.getSize()
        );
    }

    /* ---------------------- 보낸 요청 목록 ---------------------- */
    @Override
    public PageResponseDto<PetFriendResponseDto> sentRequests(Long loginUserId,
                                                              Pageable pageable) {

        var page = petFriendRepo.findByRequesterUserIdAndStatus(
                loginUserId, FriendStatus.PENDING, pageable);

        List<PetFriendResponseDto> dtoList = page.getContent()
                .stream()
                .map(PetFriendResponseDto::from)
                .toList();

        return new PageResponseDto<>(
                dtoList,
                page.hasNext(),
                page.getTotalElements(),
                page.getNumber(),
                page.getSize()
        );
    }

    /* ---------------------- 특정 펫의 친구 목록 ---------------------- */
    @Override
    public PageResponseDto<PetResponseDto> petFriends(Long petId, Pageable pageable) {

        var page = petFriendRepo.findFriendsOfPet(petId, pageable);

        List<PetResponseDto> dtoList = page.getContent()
                .stream()
                .map(PetResponseDto::from)
                .toList();

        return new PageResponseDto<>(
                dtoList,
                page.hasNext(),
                page.getTotalElements(),
                page.getNumber(),
                page.getSize()
        );
    }
}
