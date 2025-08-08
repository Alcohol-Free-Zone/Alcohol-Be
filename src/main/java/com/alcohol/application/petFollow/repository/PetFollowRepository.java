package com.alcohol.application.petFollow.repository;

import com.alcohol.application.pet.entity.Pet;
import com.alcohol.application.petFollow.entity.PetFollow;
import com.alcohol.application.userAccount.entity.UserAccount;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PetFollowRepository extends JpaRepository<PetFollow, Long> {

    /* 팔로우  ▸  존재 확인 - 엔터티 객체 사용 */
    boolean existsByFollowerAndPet(UserAccount follower, Pet pet);

    /* 팔로우  ▸  삭제 - 엔터티 객체 사용 */
    void deleteByFollowerAndPet(UserAccount follower, Pet pet);

    /* 내가 팔로우하는 PetFollow 목록 - 엔터티 객체 사용 */
    Page<PetFollow> findAllByFollower(UserAccount follower, Pageable pageable);

    /* 특정 펫의 팔로워 수 - 엔터티 객체 사용 */
    long countByPet(Pet pet);

    /* follower 가 팔로우하는 Pet 엔터티 리스트 직접 조회 */
    @Query("select pf.pet from PetFollow pf where pf.follower = :follower")
    List<Pet> findPetsByFollower(UserAccount follower);

    // 또는 Pet ID만 필요한 경우
    @Query("select pf.pet.petId from PetFollow pf where pf.follower = :follower")
    List<Long> findPetIdsByFollower(UserAccount follower);
}