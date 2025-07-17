package com.alcohol.application.petFollow.service;

import com.alcohol.util.pagination.PageResponseDto;
import com.alcohol.application.pet.dto.PetResponseDto;
import org.springframework.data.domain.Pageable;

public interface PetFollowService {

    /**
     * 펫 팔로우
     */
    void follow(Long followerId, Long petId);

    /**
     * 펫 언팔로우
     */
    void unfollow(Long followerId, Long petId);

    /**
     * 팔로우 여부 확인
     */
    boolean isFollowing(Long followerId, Long petId);

    /**
     * 내가 팔로우하는 펫 목록
     */
    PageResponseDto<PetResponseDto> getMyFollowingPets(Long followerId, Pageable pageable);

    /**
     * 특정 펫의 팔로워 수
     */
    long getFollowerCount(Long petId);
}