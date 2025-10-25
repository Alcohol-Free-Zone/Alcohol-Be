package com.alcohol.application.follow.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alcohol.application.follow.entity.PetFollow;
import com.alcohol.application.follow.repository.PetFollowRepository;
import com.alcohol.application.pet.dto.PetResponseDto;
import com.alcohol.application.pet.entity.Pet;
import com.alcohol.application.pet.repository.PetRepository;
import com.alcohol.util.pagination.PageRequestDto;
import com.alcohol.util.pagination.PageResponseDto;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class PetFollowServiceImpl implements PetFollowService {

    private final PetRepository petRepository;
    private final PetFollowRepository petFollowRepository;

    @Transactional(readOnly = true)
    public PageResponseDto<PetResponseDto> getMyFollowers(Long userId, PageRequestDto pageRequestDto) {
        Pageable pageable = pageRequestDto.toPageable();

        // followerId로 PetFollow 조회
        List<PetFollow> follows = petFollowRepository.findAllByFollowerId(userId);

        // petId 리스트 추출
        List<Long> petIds = follows.stream()
                .map(PetFollow::getPetId)
                .toList();

        // petId로 Pet 엔티티 조회 (페이징 적용)
        Page<Pet> petPage = petRepository.findAllByPetIdIn(petIds, pageable);

        // Pet → DTO 변환
        List<PetResponseDto> content = petPage.getContent().stream()
                .map(PetResponseDto::from)
                .toList();

        // 응답 반환
        return new PageResponseDto<>(
                content,
                petPage.hasNext(),
                petPage.getTotalElements(),
                petPage.getNumber(),
                petPage.getSize()
        );
    }

    
}
