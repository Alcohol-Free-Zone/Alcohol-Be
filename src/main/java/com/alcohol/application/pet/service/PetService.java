package com.alcohol.application.pet.service;

import com.alcohol.application.pet.dto.PetAddRequest;
import com.alcohol.application.pet.dto.PetAddResponse;
import com.alcohol.application.pet.dto.PetResponseDto;
import com.alcohol.application.userAccount.entity.UserAccount;
import com.alcohol.util.pagination.PageRequestDto;
import com.alcohol.util.pagination.PageResponseDto;

public interface PetService {
    // Page<Pet> findAll(Pageable pageable);

    PetAddResponse addPet(PetAddRequest petRequest, UserAccount currentUser);

    PageResponseDto<PetResponseDto> getPetList(Long userId, PageRequestDto pageRequestDto, String petName);

    void deletePet(Long petId, UserAccount currentUser);

    PetResponseDto getPet(Long petId);
}
