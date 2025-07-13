package com.alcohol.application.pet.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.alcohol.application.pet.entity.Pet;

@Repository
public interface PetRepository extends JpaRepository<Pet, Long> {

    Optional<Pet> findById(Long petId);    

    Page<Pet> findAllByUserAccount_Id(Long userId, Pageable pageable);
    
}
