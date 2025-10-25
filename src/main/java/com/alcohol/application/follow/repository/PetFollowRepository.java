package com.alcohol.application.follow.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.alcohol.application.follow.entity.PetFollow;

public interface PetFollowRepository extends JpaRepository<PetFollow, Long> {

    List<PetFollow> findAllByFollowerId(Long followerId);
    
}
