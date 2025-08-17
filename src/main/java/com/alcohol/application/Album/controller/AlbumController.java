package com.alcohol.application.Album.controller;

import com.alcohol.application.Album.dto.CustomAlbumCreateRequestDto;
import com.alcohol.application.Album.dto.CustomAlbumResponseDto;
import com.alcohol.application.Album.dto.AlbumResponseDto;
import com.alcohol.application.Album.service.AlbumService;
import com.alcohol.application.userAccount.entity.UserAccount;
import com.alcohol.common.files.dto.FileResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/albums")
@RequiredArgsConstructor
public class AlbumController {

    private final AlbumService albumService;

    // ============ 펫 앨범 관련 ============

    // 펫 앨범에 사진 업로드
    @PostMapping("/pet/{petId}/upload")
    public ResponseEntity<List<FileResponseDto>> uploadPetPhotos(
            @PathVariable Long petId,
            @RequestParam("files") List<MultipartFile> files,
            @AuthenticationPrincipal UserAccount currentUser
    ) {
        List<FileResponseDto> response = albumService.uploadPhotos(petId, files, currentUser);
        return ResponseEntity.ok(response);
    }

    // 펫 앨범 조회
    @GetMapping("/pet/{petId}")
    public ResponseEntity<AlbumResponseDto> getPetAlbum(
            @PathVariable Long petId,
            @AuthenticationPrincipal UserAccount currentUser
    ) {
        AlbumResponseDto response = albumService.getPetAlbum(petId, currentUser);
        return ResponseEntity.ok(response);
    }

    // 펫 앨범 사진들만 조회
    @GetMapping("/pet/{petId}/photos")
    public ResponseEntity<List<FileResponseDto>> getPetAlbumPhotos(
            @PathVariable Long petId,
            @AuthenticationPrincipal UserAccount currentUser
    ) {
        List<FileResponseDto> response = albumService.getAlbumPhotos(petId, currentUser);
        return ResponseEntity.ok(response);
    }

    // 기존 파일을 펫 앨범에 추가
    @PostMapping("/pet/{petId}/add-file/{fileId}")
    public ResponseEntity<String> addExistingFileToPetAlbum(
            @PathVariable Long petId,
            @PathVariable Long fileId,
            @AuthenticationPrincipal UserAccount currentUser
    ) {
        albumService.addExistingFileToPetAlbum(petId, fileId, currentUser);
        return ResponseEntity.ok("파일이 펫 앨범에 추가되었습니다.");
    }

    // 펫 앨범에서 사진 제거
    @DeleteMapping("/pet/{petId}/photo/{fileId}")
    public ResponseEntity<String> deletePetPhoto(
            @PathVariable Long petId,
            @PathVariable Long fileId,
            @AuthenticationPrincipal UserAccount currentUser
    ) {
        albumService.deletePhoto(petId, fileId, currentUser);
        return ResponseEntity.ok("펫 앨범에서 사진이 제거되었습니다.");
    }

    // 펫 앨범 전체 매핑 삭제
    @DeleteMapping("/pet/{petId}/photos")
    public ResponseEntity<String> deleteAllPetPhotos(
            @PathVariable Long petId,
            @AuthenticationPrincipal UserAccount currentUser
    ) {
        albumService.deleteAllPhotos(petId, currentUser);
        return ResponseEntity.ok("펫 앨범의 모든 사진 매핑이 삭제되었습니다.");
    }

    // ============ 커스텀 앨범 관련 ============

    // 커스텀 앨범 생성
    @PostMapping("/custom")
    public ResponseEntity<CustomAlbumResponseDto> createCustomAlbum(
            @RequestBody CustomAlbumCreateRequestDto request,
            @AuthenticationPrincipal UserAccount currentUser
    ) {
        CustomAlbumResponseDto response = albumService.createCustomAlbum(request, currentUser);
        return ResponseEntity.ok(response);
    }

    // 내 커스텀 앨범 목록
    @GetMapping("/custom/my")
    public ResponseEntity<List<CustomAlbumResponseDto>> getMyCustomAlbums(
            @AuthenticationPrincipal UserAccount currentUser
    ) {
        List<CustomAlbumResponseDto> response = albumService.getMyCustomAlbums(currentUser);
        return ResponseEntity.ok(response);
    }

    // 커스텀 앨범 상세 조회
    @GetMapping("/custom/{albumId}")
    public ResponseEntity<CustomAlbumResponseDto> getCustomAlbum(@PathVariable Long albumId) {
        CustomAlbumResponseDto response = albumService.getCustomAlbum(albumId);
        return ResponseEntity.ok(response);
    }

    // 커스텀 앨범 삭제
    @DeleteMapping("/custom/{albumId}")
    public ResponseEntity<String> deleteCustomAlbum(
            @PathVariable Long albumId,
            @AuthenticationPrincipal UserAccount currentUser
    ) {
        albumService.deleteCustomAlbum(albumId, currentUser);
        return ResponseEntity.ok("커스텀 앨범이 삭제되었습니다.");
    }

    // 커스텀 앨범에 사진 업로드
    @PostMapping("/custom/{albumId}/upload")
    public ResponseEntity<List<CustomAlbumResponseDto.CustomAlbumPhotoDto>> addPhotosToCustomAlbum(
            @PathVariable Long albumId,
            @RequestParam("files") List<MultipartFile> files,
            @RequestParam("petIds") List<Long> petIds,
            @RequestParam(value = "captions", required = false) List<String> captions,
            @AuthenticationPrincipal UserAccount currentUser
    ) {
        List<CustomAlbumResponseDto.CustomAlbumPhotoDto> response =
                albumService.addPhotosToCustomAlbum(albumId, files, petIds, captions, currentUser);
        return ResponseEntity.ok(response);
    }

    // 기존 파일을 커스텀 앨범에 추가
    @PostMapping("/custom/{albumId}/add-file")
    public ResponseEntity<CustomAlbumResponseDto.CustomAlbumPhotoDto> addExistingFileToCustomAlbum(
            @PathVariable Long albumId,
            @RequestParam Long fileId,
            @RequestParam Long petId,
            @RequestParam(required = false) String caption,
            @AuthenticationPrincipal UserAccount currentUser
    ) {
        CustomAlbumResponseDto.CustomAlbumPhotoDto response =
                albumService.addExistingFileToCustomAlbum(albumId, fileId, petId, caption, currentUser);
        return ResponseEntity.ok(response);
    }

    // 커스텀 앨범에서 사진 제거
    @DeleteMapping("/custom/{albumId}/photo/{photoId}")
    public ResponseEntity<String> removePhotoFromCustomAlbum(
            @PathVariable Long albumId,
            @PathVariable Long photoId,
            @AuthenticationPrincipal UserAccount currentUser
    ) {
        albumService.removePhotoFromCustomAlbum(albumId, photoId, currentUser);
        return ResponseEntity.ok("커스텀 앨범에서 사진이 제거되었습니다.");
    }

    // 사진 캡션 수정
    @PutMapping("/custom/{albumId}/photo/{photoId}/caption")
    public ResponseEntity<String> updatePhotoCaption(
            @PathVariable Long albumId,
            @PathVariable Long photoId,
            @RequestParam String caption,
            @AuthenticationPrincipal UserAccount currentUser
    ) {
        albumService.updatePhotoCaption(albumId, photoId, caption, currentUser);
        return ResponseEntity.ok("사진 캡션이 수정되었습니다.");
    }

    // 사진 순서 변경
    @PutMapping("/custom/{albumId}/photos/order")
    public ResponseEntity<String> updatePhotoOrder(
            @PathVariable Long albumId,
            @RequestBody List<Long> photoIds,
            @AuthenticationPrincipal UserAccount currentUser
    ) {
        albumService.updatePhotoOrder(albumId, photoIds, currentUser);
        return ResponseEntity.ok("사진 순서가 변경되었습니다.");
    }
}
