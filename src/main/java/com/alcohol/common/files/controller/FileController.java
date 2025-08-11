package com.alcohol.common.files.controller;

import com.alcohol.common.files.dto.FileResponseDto;
import com.alcohol.common.files.entity.FileType;
import com.alcohol.common.files.service.FileService;
import com.alcohol.application.userAccount.entity.UserAccount;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/files")
@RequiredArgsConstructor
public class FileController {

    private final FileService fileService;

    // 프로필 이미지 업로드
    @PostMapping("/profile")
    public ResponseEntity<FileResponseDto> uploadProfileImage(
            @RequestParam("file") MultipartFile file,
            @AuthenticationPrincipal UserAccount currentUser
    ) {
        FileResponseDto response = fileService.uploadProfileImageAndUpdate(file, currentUser.getId());
        return ResponseEntity.ok(response);
    }

    // 펫 앨범 사진 업로드 (추가)
    @PostMapping("/pet-album/{petId}")
    public ResponseEntity<FileResponseDto> uploadPetAlbumPhoto(
            @PathVariable Long petId,
            @RequestParam("file") MultipartFile file,
            @AuthenticationPrincipal UserAccount currentUser
    ) {
        FileResponseDto response = fileService.uploadFile(file, FileType.PET_ALBUM, petId);
        return ResponseEntity.ok(response);
    }

    // 커스텀 앨범 사진 업로드 (추가)
    @PostMapping("/custom-album/{albumId}")
    public ResponseEntity<FileResponseDto> uploadCustomAlbumPhoto(
            @PathVariable Long albumId,
            @RequestParam("file") MultipartFile file,
            @AuthenticationPrincipal UserAccount currentUser
    ) {
        FileResponseDto response = fileService.uploadFile(file, FileType.CUSTOM_ALBUM, albumId);
        return ResponseEntity.ok(response);
    }

    // 파일 서빙도 Service에 위임
    @GetMapping("/images/{fileName}")
    public ResponseEntity<Resource> serveImageFile(@PathVariable String fileName) {
        return fileService.serveImageFile(fileName);
    }

    @GetMapping("/others/{fileName}")
    public ResponseEntity<Resource> serveOtherFile(@PathVariable String fileName) {
        return fileService.serveOtherFile(fileName);
    }

    // 파일 다운로드 (기존 유지)
    @GetMapping("/download/{fileId}")
    public ResponseEntity<ByteArrayResource> downloadFile(@PathVariable Long fileId) {
        FileResponseDto fileInfo = fileService.getFileById(fileId);
        byte[] fileData = fileService.downloadFile(fileId);

        ByteArrayResource resource = new ByteArrayResource(fileData);

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(fileInfo.getContentType()))
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + fileInfo.getOriginalFileName() + "\"")
                .contentLength(fileData.length)
                .body(resource);
    }

    // 파일 정보 조회
    @GetMapping("/{fileId}")
    public ResponseEntity<FileResponseDto> getFileInfo(@PathVariable Long fileId) {
        FileResponseDto fileInfo = fileService.getFileById(fileId);
        return ResponseEntity.ok(fileInfo);
    }

    // 파일 삭제
    @DeleteMapping("/{fileId}")
    public ResponseEntity<String> deleteFile(@PathVariable Long fileId) {
        fileService.deleteFile(fileId);
        return ResponseEntity.ok("파일이 삭제되었습니다.");
    }


}
