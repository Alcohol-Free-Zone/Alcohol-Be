package com.alcohol.application.petFollow.service;

import com.alcohol.application.pet.entity.Pet;
import com.alcohol.application.pet.repository.PetRepository;
import com.alcohol.application.petFollow.entity.PetFollow;
import com.alcohol.application.petFollow.repository.PetFollowRepository;
import com.alcohol.application.userAccount.entity.UserAccount;
import com.alcohol.application.userAccount.repository.UserAccountRepository;
import com.alcohol.util.pagination.PageResponseDto;
import com.alcohol.application.pet.dto.PetResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class PetFollowServiceImpl implements PetFollowService {

    private final PetFollowRepository petFollowRepository;
    private final PetRepository       petRepository;
    private final UserAccountRepository userAccountRepository;

    @Override
    public void follow(Long followerId, Long petId) {
        // 1. 사용자와 펫 엔터티 조회
        UserAccount follower = userAccountRepository.findById(followerId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        Pet pet = petRepository.findById(petId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 펫입니다."));

        // 2. 자기 펫 팔로우 금지
        if (pet.getUserAccount().getId().equals(followerId)) {
            throw new IllegalArgumentException("본인 펫은 팔로우할 수 없습니다.");
        }

        // 3. 이미 팔로우인지 검사 (엔터티 기반 검색으로 변경)
        if (petFollowRepository.existsByFollowerAndPet(follower, pet)) {
            throw new IllegalArgumentException("이미 팔로우 중입니다.");
        }

        // 4. 저장 (엔터티 객체로 생성)
        petFollowRepository.save(PetFollow.of(follower, pet));
        log.info("user {} → pet {} 팔로우", followerId, petId);
    }

    @Override
    public void unfollow(Long followerId, Long petId) {
       // 사용자와 펫 엔터티 조회
        UserAccount follower = userAccountRepository.findById(followerId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        Pet pet = petRepository.findById(petId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 펫입니다."));

        if (!petFollowRepository.existsByFollowerAndPet(follower, pet)) {
            throw new IllegalArgumentException("팔로우 중이지 않습니다.");
        }
        petFollowRepository.deleteByFollowerAndPet(follower, pet);
        log.info("user {} → pet {} 언팔로우", followerId, petId);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isFollowing(Long followerId, Long petId) {
        // 사용자와 펫 엔터티 조회
        UserAccount follower = userAccountRepository.findById(followerId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        Pet pet = petRepository.findById(petId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 펫입니다."));

        return petFollowRepository.existsByFollowerAndPet(follower, pet);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponseDto<PetResponseDto> getMyFollowingPets(Long followerId, Pageable pageable) {
        // 사용자 엔터티 조회
        UserAccount follower = userAccountRepository.findById(followerId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        var followPage = petFollowRepository.findAllByFollower(follower, pageable);

        // Pet 엔터티에서 직접 접근 가능
        List<PetResponseDto> dtoList = followPage.getContent()
                .stream()
                .map(pf -> PetResponseDto.from(pf.getPet())) // getPet()으로 직접 접근
                .toList();

        return new PageResponseDto<>(
                dtoList,
                followPage.hasNext(),
                followPage.getTotalElements(),
                followPage.getNumber(),
                followPage.getSize()
        );
    }

    @Override
    @Transactional(readOnly = true)
    public long getFollowerCount(Long petId) {
        Pet pet = petRepository.findById(petId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 펫입니다."));

        return petFollowRepository.countByPet(pet);
    }
}