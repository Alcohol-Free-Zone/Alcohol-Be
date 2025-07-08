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
        Pet pet;

        if (petRequest.getPetId() != 0) { 
            // 수정 로직
            pet = petRepository.findById((long) petRequest.getPetId())
                .orElseThrow(() -> new IllegalArgumentException("해당 Pet이 존재하지 않습니다."));

            // 기존 pet 객체에 새 값 덮어쓰기
            updateEntity(pet, petRequest);
        } else {
            // 신규 등록
            pet = toEntity(petRequest);
        }

        Pet savedPet = petRepository.save(pet);

        // PersonalityTag 저장
        savePersonalityTags(petRequest.getTags(), savedPet);

        // Anniversary 저장
        saveAnniversary(petRequest.getAnniversary(), savedPet);

        // Entity → Response 변환
        return toResponse(savedPet, petRequest.getTags(), petRequest.getAnniversary());
    }

    private void updateEntity(Pet pet, PetAddRequest petRequest) {
        pet.setPetName(petRequest.getPetName());
        pet.setBreed(petRequest.getBreed());
        pet.setBirth(petRequest.getBirth());
        pet.setMemo(petRequest.getMemo());
        pet.setPetAge(petRequest.getPetAge());
        
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
