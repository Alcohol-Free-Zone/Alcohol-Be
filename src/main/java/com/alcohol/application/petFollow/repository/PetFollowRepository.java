package com.alcohol.application.petFollow.repository;

import com.alcohol.application.petFollow.entity.PetFollow;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PetFollowRepository extends JpaRepository<PetFollow, Long> {

    /* 팔로우  ▸  존재 확인 */
    boolean existsByFollowerIdAndPetId(Long followerId, Long petId);

    /* 팔로우  ▸  삭제 */
    void deleteByFollowerIdAndPetId(Long followerId, Long petId);

    /* 내가 팔로우하는 PetFollow 목록 */
    Page<PetFollow> findAllByFollowerId(Long followerId, Pageable pageable);

    /* 특정 펫의 팔로워 수 */
    long countByPetId(Long petId);

    /* follower 가 팔로우하는 petId 리스트만 뽑기 (Pet 한꺼번에 읽기용) */
    @Query("select pf.petId from PetFollow pf where pf.followerId = :followerId")
    List<Long> findPetIdsByFollowerId(Long followerId);
}