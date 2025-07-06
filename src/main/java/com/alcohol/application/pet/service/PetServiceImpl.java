package com.alcohol.application.pet.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alcohol.application.Enum.PersonalityTagType;
import com.alcohol.application.pet.dto.PetAddRequest;
import com.alcohol.application.pet.dto.PetAddResponse;
import com.alcohol.application.pet.entity.Pet;
import com.alcohol.application.pet.entity.PetAnniversary;
import com.alcohol.application.pet.entity.PetPersonalityTag;
import com.alcohol.application.pet.repository.PetPersonalityTagRepository;
import com.alcohol.application.pet.repository.PetRepository;
import com.alcohol.application.pet.service.PetRepository.PetAnniversaryRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class PetServiceImpl implements PetService {
    
    private final PetRepository petRepository;
    private final PetPersonalityTagRepository petPersonalityTagRepository;
    private final PetAnniversaryRepository petAnniversaryRepository;

    @Override
    public Page<Pet> findAll(Pageable pageable) {
        return petRepository.findAll(pageable);
    }

    @Override
    public PetAddResponse addPet(PetAddRequest petRequest) {
        // Pet 엔티티 생성
        Pet pet = toEntity(petRequest);
        Pet savedPet = petRepository.save(pet);

        // PersonalityTag 저장
        savePersonalityTags(petRequest.getTags(), savedPet);

        // Anniversary 저장
        saveAnniversary(petRequest.getAnniversary(), savedPet);

        // Entity → Response 변환
        return toResponse(savedPet, petRequest.getTags(), petRequest.getAnniversary());
    }

    // DTO → Entity 변환 메서드
    private Pet toEntity(PetAddRequest request) {
        Pet pet = new Pet();
        pet.setPetName(request.getPetName());
        pet.setBreed(request.getBreed());
        pet.setPetAge(request.getPetAge());
        pet.setBirth(request.getBirth());
        pet.setMemo(request.getMemo());
        return pet;
    }

    // 태그 저장 메서드
    private void savePersonalityTags(List<PersonalityTagType> tags, Pet pet) {
        if (tags == null || tags.isEmpty()) {
            return;
        }
        for (PersonalityTagType tagType : tags) {
            PetPersonalityTag tagEntity = new PetPersonalityTag();
            tagEntity.setPet(pet);
            tagEntity.setTag(tagType);
            petPersonalityTagRepository.save(tagEntity);
        }
    }

    // 기념일 저장 메서드
    private void saveAnniversary(PetAnniversary anniversary, Pet savedPet) {
        if (anniversary == null) {
            return;
        }

        PetAnniversary petAnniversary = new PetAnniversary();
        petAnniversary.setDate(anniversary.getDate());
        petAnniversary.setTitle(anniversary.getTitle());
        petAnniversaryRepository.save(petAnniversary);
        
    }   
    

    // Entity → DTO 변환 메서드
    private PetAddResponse toResponse(Pet pet, List<PersonalityTagType> tags, PetAnniversary petAnniversary) {
        return PetAddResponse.builder()
                .petId(pet.getPetId())
                .petName(pet.getPetName())
                .breed(pet.getBreed())
                .petAge(pet.getPetAge())
                .birth(pet.getBirth())
                .memo(pet.getMemo())
                .tags(tags)
                .anniversary(petAnniversary)
                .build();
    }

}
