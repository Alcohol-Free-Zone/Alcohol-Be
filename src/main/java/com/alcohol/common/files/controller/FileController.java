package com.alcohol.common.files.controller;

import com.alcohol.common.files.dto.FileResponseDto;
import com.alcohol.common.files.entity.FileType;
import com.alcohol.common.files.service.FileService;
import com.alcohol.application.userAccount.entity.UserAccount;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
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
        // 기존 프로필 이미지 삭제
        fileService.deleteFilesByTypeAndRelatedId(FileType.PROFILE, currentUser.getId());

        FileResponseDto response = fileService.uploadFile(file, FileType.PROFILE, currentUser.getId());
        return ResponseEntity.ok(response);
    }

    // 파일 다운로드
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

    // ... 나머지 메서드들
}
