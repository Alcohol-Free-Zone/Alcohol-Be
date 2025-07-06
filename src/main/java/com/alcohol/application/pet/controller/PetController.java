package com.alcohol.application.pet.controller;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alcohol.application.pet.dto.PetAddRequest;
import com.alcohol.application.pet.dto.PetAddResponse;
import com.alcohol.application.pet.dto.PetResponseDto;
import com.alcohol.application.pet.entity.Pet;
import com.alcohol.application.pet.service.PetService;
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
        PageRequestDto pageRequestDto
        ) {

        Pageable pageable = pageRequestDto.toPageable();
        Page<Pet> petPage = petService.findAll(pageable);

        List<PetResponseDto> content = petPage.getContent().stream()
                .map(PetResponseDto::from)
                .toList();

        PageResponseDto<PetResponseDto> response = new PageResponseDto<>(
                content,
                petPage.hasNext(),
                petPage.getTotalElements(),
                petPage.getNumber(),
                petPage.getSize()
        );

        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<PetAddResponse> addPet(
        @RequestBody PetAddRequest petRequest
    ) {
        PetAddResponse response = petService.addPet(petRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
}
