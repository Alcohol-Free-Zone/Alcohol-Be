package com.alcohol.application.pet.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alcohol.application.Enum.PersonalityTagType;
import com.alcohol.application.pet.dto.PetAddRequest;
import com.alcohol.application.pet.dto.PetAddResponse;
import com.alcohol.application.pet.dto.PetResponseDto;
import com.alcohol.application.pet.entity.Pet;
import com.alcohol.application.pet.entity.PetAnniversary;
import com.alcohol.application.pet.entity.PetPersonalityTag;
import com.alcohol.application.pet.repository.PetPersonalityTagRepository;
import com.alcohol.application.pet.repository.PetRepository;
import com.alcohol.application.pet.service.PetRepository.PetAnniversaryRepository;
import com.alcohol.application.userAccount.entity.UserAccount;
import com.alcohol.util.pagination.PageResponseDto;

import jakarta.persistence.EntityNotFoundException;
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

    // @Override
    // public Page<Pet> findAll(Pageable pageable) {
    //     return petRepository.findAll(pageable);
    // }

    @Transactional(readOnly = true)
    public PageResponseDto<PetResponseDto> getPetList(Long userId, Pageable pageable) {
        Page<Pet> petPage = petRepository.findAllByUserAccount_Id(userId, pageable);

        List<PetResponseDto> content = petPage.getContent().stream()
                .map(PetResponseDto::from)
                .toList();

        return new PageResponseDto<>(
                content,
                petPage.hasNext(),
                petPage.getTotalElements(),
                petPage.getNumber(),
                petPage.getSize()
        );
    }

    @Override
    public PetAddResponse addPet(PetAddRequest petRequest, UserAccount userAccount) {
        Pet pet;

        

        if (petRequest.getPetId() != 0) { 
            // 수정 로직
            pet = petRepository.findById((long) petRequest.getPetId())
                .orElseThrow(() -> new IllegalArgumentException("해당 Pet이 존재하지 않습니다."));

            if (!pet.getUserAccount().getId().equals(userAccount.getId())) {
                throw new SecurityException("해당 반려동물을 수정할 권한이 없습니다.");
            }

            // 기존 pet 객체에 새 값 덮어쓰기
            updateEntity(pet, petRequest);
        } else {
            // 신규 등록
            pet = toEntity(petRequest, userAccount);
        }

        Pet savedPet = petRepository.save(pet);

        // PersonalityTag 저장
        petPersonalityTagRepository.deleteByPet(pet);
        savePersonalityTags(petRequest.getTags(), savedPet);

        // Anniversary 저장
        petAnniversaryRepository.deleteByPet(pet);
        saveAnniversary(petRequest.getAnniversaries(), savedPet);

        // Entity → Response 변환
        return toResponse(savedPet, petRequest.getTags(), petRequest.getAnniversaries());
    }

    private void updateEntity(Pet pet, PetAddRequest petRequest) {
        pet.setPetName(petRequest.getPetName());
        pet.setBreed(petRequest.getBreed());
        pet.setBirth(petRequest.getBirth());
        pet.setMemo(petRequest.getMemo());
        pet.setPetAge(petRequest.getPetAge());
        
    }

    // DTO → Entity 변환 메서드
    private Pet toEntity(PetAddRequest request, UserAccount userAccount) {
        Pet pet = new Pet();
        pet.setPetName(request.getPetName());
        pet.setBreed(request.getBreed());
        pet.setPetAge(request.getPetAge());
        pet.setBirth(request.getBirth());
        pet.setMemo(request.getMemo());
        pet.setUserAccount(userAccount);
        return pet;
    }

    // 태그 저장 메서드
    private void savePersonalityTags(List<PersonalityTagType> tags, Pet savedPet) {
        if (tags == null || tags.isEmpty()) {
            return;
        }
        for (PersonalityTagType tagType : tags) {
            PetPersonalityTag tagEntity = new PetPersonalityTag();
            tagEntity.setPet(savedPet);
            tagEntity.setTag(tagType);
            petPersonalityTagRepository.save(tagEntity);
        }
    }

    // 기념일 저장 메서드
    private void saveAnniversary(List<PetAnniversary> anniversary, Pet savedPet) {
        if (anniversary == null) {
            return;
        }

        for (PetAnniversary anniversaryUnit : anniversary) {
            PetAnniversary petAnniversary = new PetAnniversary();
            petAnniversary.setPet(savedPet);
            petAnniversary.setDate(anniversaryUnit.getDate());
            petAnniversary.setTitle(anniversaryUnit.getTitle());
            petAnniversaryRepository.save(petAnniversary);
        }
        
    }   
    

    // Entity → DTO 변환 메서드
    private PetAddResponse toResponse(Pet pet, List<PersonalityTagType> tags, List<PetAnniversary> petAnniversary) {
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

    @Override
    @Transactional
    public void deletePet(Long petId, UserAccount userAccount) {
        Pet pet = petRepository.findById(petId)
                .orElseThrow(() -> new EntityNotFoundException("해당 반려동물이 존재하지 않습니다. ID = " + petId));
        petRepository.delete(pet);

        if (!pet.getUserAccount().getId().equals(userAccount.getId())) {
        throw new SecurityException("해당 반려동물을 삭제할 권한이 없습니다.");
        }
    }

}
