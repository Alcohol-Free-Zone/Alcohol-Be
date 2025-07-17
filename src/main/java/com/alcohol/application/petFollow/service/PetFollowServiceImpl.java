package com.alcohol.application.petFollow.service;

import com.alcohol.application.pet.entity.Pet;
import com.alcohol.application.pet.repository.PetRepository;
import com.alcohol.application.petFollow.entity.PetFollow;
import com.alcohol.application.petFollow.repository.PetFollowRepository;
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

    @Override
    public void follow(Long followerId, Long petId) {
        // 1. 펫 존재 확인
        Pet pet = petRepository.findById(petId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 펫입니다."));

        // 2. 자기 펫 팔로우 금지
        if (pet.getUserAccount().getId().equals(followerId)) {
            throw new IllegalArgumentException("본인 펫은 팔로우할 수 없습니다.");
        }

        // 3. 이미 팔로우인지 검사
        if (petFollowRepository.existsByFollowerIdAndPetId(followerId, petId)) {
            throw new IllegalArgumentException("이미 팔로우 중입니다.");
        }

        // 4. 저장
        petFollowRepository.save(PetFollow.of(followerId, petId));
        log.info("user {} → pet {} 팔로우", followerId, petId);
    }

    @Override
    public void unfollow(Long followerId, Long petId) {
        if (!petFollowRepository.existsByFollowerIdAndPetId(followerId, petId)) {
            throw new IllegalArgumentException("팔로우 중이지 않습니다.");
        }
        petFollowRepository.deleteByFollowerIdAndPetId(followerId, petId);
        log.info("user {} → pet {} 언팔로우", followerId, petId);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isFollowing(Long followerId, Long petId) {
        return petFollowRepository.existsByFollowerIdAndPetId(followerId, petId);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponseDto<PetResponseDto> getMyFollowingPets(Long followerId, Pageable pageable) {
        var followPage = petFollowRepository.findAllByFollowerId(followerId, pageable);

        // 1) petId 모아서 한 번에 Pet 조회
        List<Long> petIds = followPage.getContent()
                .stream()
                .map(PetFollow::getPetId)
                .toList();

        Map<Long, Pet> petMap = petRepository.findAllById(petIds)
                .stream()
                .collect(Collectors.toMap(Pet::getPetId, p -> p));

        // 2) DTO 변환
        List<PetResponseDto> dtoList = followPage.getContent()
                .stream()
                .map(pf -> PetResponseDto.from(petMap.get(pf.getPetId())))
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
        return petFollowRepository.countByPetId(petId);
    }
}