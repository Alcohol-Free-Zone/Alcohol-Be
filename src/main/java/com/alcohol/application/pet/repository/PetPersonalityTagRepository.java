package com.alcohol.application.pet.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.alcohol.application.pet.entity.PetPersonalityTag;

@Repository
public interface PetPersonalityTagRepository  extends JpaRepository<PetPersonalityTag, Long> {
    
}