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

import java.util.List;

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

    // ✅ 일반 파일 업로드 (사용자 소유)
    @PostMapping("/upload")
    public ResponseEntity<FileResponseDto> uploadFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam("fileType") FileType fileType,
            @AuthenticationPrincipal UserAccount currentUser
    ) {
        FileResponseDto response = fileService.uploadFile(file, currentUser.getId(), fileType);
        return ResponseEntity.ok(response);
    }


    // ✅ 내 파일 전체 조회
    @GetMapping("/my")
    public ResponseEntity<List<FileResponseDto>> getMyAllFiles(
            @AuthenticationPrincipal UserAccount currentUser
    ) {
        List<FileResponseDto> files = fileService.getUserAllFiles(currentUser.getId());
        return ResponseEntity.ok(files);
    }

    // ✅ 내 파일 타입별 조회
    @GetMapping("/my/{fileType}")
    public ResponseEntity<List<FileResponseDto>> getMyFilesByType(
            @PathVariable FileType fileType,
            @AuthenticationPrincipal UserAccount currentUser
    ) {
        List<FileResponseDto> files = fileService.getUserFiles(currentUser.getId(), fileType);
        return ResponseEntity.ok(files);
    }

    // ✅ 내 특정 타입 파일들 전체 삭제
    @DeleteMapping("/my/{fileType}")
    public ResponseEntity<String> deleteMyFilesByType(
            @PathVariable FileType fileType,
            @AuthenticationPrincipal UserAccount currentUser
    ) {
        fileService.deleteUserFilesByType(currentUser.getId(), fileType);
        return ResponseEntity.ok(fileType + " 타입의 모든 파일이 삭제되었습니다.");
    }

    // 파일 다운로드 (소유권 확인)
    @GetMapping("/download/{fileId}")
    public ResponseEntity<ByteArrayResource> downloadFile(
            @PathVariable Long fileId,
            @AuthenticationPrincipal UserAccount currentUser
    ) {
        FileResponseDto fileInfo = fileService.getFileById(fileId);
        byte[] fileData = fileService.downloadFileWithPermission(fileId, currentUser.getId());

        ByteArrayResource resource = new ByteArrayResource(fileData);

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(fileInfo.getContentType()))
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + fileInfo.getOriginalFileName() + "\"")
                .contentLength(fileData.length)
                .body(resource);
    }

    //파일 프리뷰
    @GetMapping("/preview/{fileId}")
    public ResponseEntity<ByteArrayResource> previewFile(
            @PathVariable Long fileId,
            @AuthenticationPrincipal UserAccount currentUser
    ) {
        FileResponseDto fileInfo = fileService.getFileById(fileId);
        byte[] fileData = fileService.downloadFileWithPermission(fileId, currentUser.getId());

        ByteArrayResource resource = new ByteArrayResource(fileData);

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(fileInfo.getContentType()))
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "inline; filename=\"" + fileInfo.getOriginalFileName() + "\"") // ✅ inline
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
    public ResponseEntity<String> deleteFile(
            @PathVariable Long fileId,
            @AuthenticationPrincipal UserAccount currentUser
    ) {
        fileService.deleteFileWithPermission(fileId, currentUser.getId());
        return ResponseEntity.ok("파일이 삭제되었습니다.");
    }


}
