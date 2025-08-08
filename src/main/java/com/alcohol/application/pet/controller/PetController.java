package com.alcohol.application.pet.controller;

import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.alcohol.application.pet.dto.PetAddRequest;
import com.alcohol.application.pet.dto.PetAddResponse;
import com.alcohol.application.pet.dto.PetResponseDto;
import com.alcohol.application.pet.service.PetService;
import com.alcohol.application.userAccount.entity.UserAccount;
import com.alcohol.util.pagination.PageRequestDto;
import com.alcohol.util.pagination.PageResponseDto;

import lombok.RequiredArgsConstructor;



@RestController
@RequiredArgsConstructor
@RequestMapping("/api/pet")
public class PetController {

    private final PetService petService;

    @GetMapping
    public ResponseEntity<PageResponseDto<PetResponseDto>> getPetList(
        PageRequestDto pageRequestDto,
        @AuthenticationPrincipal UserAccount currentUser,
        @RequestParam(required = false) String petName
        ) {
        Long userId = currentUser.getId();
        PageResponseDto<PetResponseDto> response = petService.getPetList(userId, pageRequestDto, petName);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{petId}")
    public ResponseEntity<PetResponseDto> getPet(
        @PathVariable Long petId
        ) {
        PetResponseDto response = petService.getPet(petId);
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<PetAddResponse> addPet(
        @RequestBody PetAddRequest petRequest,
        @AuthenticationPrincipal UserAccount currentUser
    ) {
        PetAddResponse response = petService.addPet(petRequest, currentUser);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @DeleteMapping("/{petId}")
    public ResponseEntity<Void> deletePet(@PathVariable Long petId,
        @AuthenticationPrincipal UserAccount currentUser) {
        petService.deletePet(petId, currentUser);
        return ResponseEntity.noContent().build();
    }
    
}
