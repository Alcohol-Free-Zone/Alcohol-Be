package com.alcohol.application.pet.repository;

import java.util.List;
import java.util.Optional;

import org.apache.ibatis.annotations.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.alcohol.application.pet.entity.Pet;

@Repository
public interface PetRepository extends JpaRepository<Pet, Long> {

    Optional<Pet> findById(Long petId);    

    Page<Pet> findAllByUserAccount_Id(Long userId, Pageable pageable);

    // 검색 기능 추가된 메서드
    Page<Pet> findAllByUserAccount_IdNotAndPetNameContainingIgnoreCase(
            Long userId, String petName, Pageable pageable);

    Page<Pet> findAllByUserAccount_IdAndPetNameContainingIgnoreCase(Long userId, String petName,
            Pageable pageable);

    //  펫 소유권 확인
    Optional<Pet> findByPetIdAndUserAccountId(Long petId, Long userId);

}
