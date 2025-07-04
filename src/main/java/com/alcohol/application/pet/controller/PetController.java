package com.alcohol.application.pet.controller;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alcohol.application.pet.dto.PetResponseDto;
import com.alcohol.application.pet.entity.Pet;
import com.alcohol.application.pet.service.PerService;
import com.alcohol.util.pagination.PageRequestDto;
import com.alcohol.util.pagination.PageResponseDto;

import lombok.RequiredArgsConstructor;



@RestController
@RequiredArgsConstructor
@RequestMapping("/api/pet")
public class PetController {

    private final PerService perService;

    @GetMapping
    public ResponseEntity<PageResponseDto<PetResponseDto>> getPetList(
        PageRequestDto pageRequestDto
        ) {

        Pageable pageable = pageRequestDto.toPageable();
        Page<Pet> petPage = perService.findAll(pageable);

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
    
}
