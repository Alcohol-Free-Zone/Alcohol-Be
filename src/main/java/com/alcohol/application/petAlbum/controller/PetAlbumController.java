package com.alcohol.application.petAlbum.controller;

import com.alcohol.application.pet.entity.Pet;
import com.alcohol.application.pet.repository.PetRepository;
import com.alcohol.application.petAlbum.dto.PetAlbumResponseDto;
import com.alcohol.application.petAlbum.service.PetAlbumService;
import com.alcohol.application.userAccount.entity.UserAccount;
import com.alcohol.common.files.dto.FileResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/pet-album")
@RequiredArgsConstructor
public class PetAlbumController {

    private final PetAlbumService petAlbumService;
    private final PetRepository petRepository;

    // 펫 앨범 사진 업로드 (다중)
    @PostMapping("/{petId}/upload")
    public ResponseEntity<List<FileResponseDto>> uploadAlbumPhotos(
            @PathVariable Long petId,
            @RequestParam("files") List<MultipartFile> files,
            @AuthenticationPrincipal UserAccount currentUser
    ) {
        validatePetOwner(petId, currentUser);

        List<FileResponseDto> uploadedFiles = petAlbumService.uploadPhotos(petId, files);
        return ResponseEntity.ok(uploadedFiles);
    }

    // 펫 앨범 조회 (그리드용)
    @GetMapping("/{petId}")
    public ResponseEntity<PetAlbumResponseDto> getPetAlbum(
            @PathVariable Long petId
    ) {
        PetAlbumResponseDto albumData = petAlbumService.getPetAlbum(petId);
        return ResponseEntity.ok(albumData);
    }

    // 앨범 사진 목록만 조회 (간단버전)
    @GetMapping("/{petId}/photos")
    public ResponseEntity<List<FileResponseDto>> getPetAlbumPhotos(
            @PathVariable Long petId
    ) {
        List<FileResponseDto> photos = petAlbumService.getAlbumPhotos(petId);
        return ResponseEntity.ok(photos);
    }

    // 앨범 사진 개별 삭제
    @DeleteMapping("/{petId}/photo/{fileId}")
    public ResponseEntity<String> deleteAlbumPhoto(
            @PathVariable Long petId,
            @PathVariable Long fileId,
            @AuthenticationPrincipal UserAccount currentUser
    ) {
        validatePetOwner(petId, currentUser);

        petAlbumService.deletePhoto(petId, fileId);
        return ResponseEntity.ok("앨범 사진이 삭제되었습니다.");
    }

    // 앨범 전체 삭제
    @DeleteMapping("/{petId}/all")
    public ResponseEntity<String> deleteAllAlbumPhotos(
            @PathVariable Long petId,
            @AuthenticationPrincipal UserAccount currentUser
    ) {
        validatePetOwner(petId, currentUser);

        petAlbumService.deleteAllPhotos(petId);
        return ResponseEntity.ok("앨범 전체가 삭제되었습니다.");
    }

    // 앨범 통계 조회
    @GetMapping("/{petId}/stats")
    public ResponseEntity<PetAlbumResponseDto.AlbumStats> getAlbumStats(
            @PathVariable Long petId
    ) {
        PetAlbumResponseDto.AlbumStats stats = petAlbumService.getAlbumStats(petId);
        return ResponseEntity.ok(stats);
    }

    // 펫 소유자 확인
    private void validatePetOwner(Long petId, UserAccount currentUser) {
        Pet pet = petRepository.findById(petId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 펫입니다."));

        if (!pet.getUserAccount().getId().equals(currentUser.getId())) {
            throw new IllegalArgumentException("본인 펫의 앨범만 관리할 수 있습니다.");
        }
    }
}
