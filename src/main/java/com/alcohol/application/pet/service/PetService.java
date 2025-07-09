package com.alcohol.application.pet.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.alcohol.application.pet.dto.PetAddRequest;
import com.alcohol.application.pet.dto.PetAddResponse;
import com.alcohol.application.pet.dto.PetResponseDto;
import com.alcohol.application.pet.entity.Pet;
import com.alcohol.util.pagination.PageResponseDto;

public interface PetService {
    // Page<Pet> findAll(Pageable pageable);

    PetAddResponse addPet(PetAddRequest petRequest);

    PageResponseDto<PetResponseDto> getPetList(Pageable pageable);

    void deletePet(Long petId);
}
