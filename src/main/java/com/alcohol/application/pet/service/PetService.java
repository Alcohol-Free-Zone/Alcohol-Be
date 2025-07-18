package com.alcohol.application.pet.service;

import com.alcohol.util.pagination.PageRequestDto;
import org.springframework.data.domain.Pageable;

import com.alcohol.application.pet.dto.PetAddRequest;
import com.alcohol.application.pet.dto.PetAddResponse;
import com.alcohol.application.pet.dto.PetResponseDto;
import com.alcohol.application.userAccount.entity.UserAccount;
import com.alcohol.util.pagination.PageResponseDto;

public interface PetService {
    // Page<Pet> findAll(Pageable pageable);

    PetAddResponse addPet(PetAddRequest petRequest, UserAccount currentUser);

    PageResponseDto<PetResponseDto> getPetList(Long userId, PageRequestDto pageRequestDto);

    void deletePet(Long petId, UserAccount currentUser);
}
