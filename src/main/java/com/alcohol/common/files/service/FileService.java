package com.alcohol.common.files.service;

import com.alcohol.common.files.dto.FileResponseDto;
import com.alcohol.common.files.entity.FileType;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

public interface FileService {

    // ✅ 새로운 업로드 메서드 (사용자 중심)
    FileResponseDto uploadFile(MultipartFile file, Long userId, FileType fileType);

    // 프로필 이미지 업로드 + UserAccount 업데이트
    FileResponseDto uploadProfileImageAndUpdate(MultipartFile file, Long userId);

    // ✅ 새로운 다중 파일 업로드 (사용자 중심)
    List<FileResponseDto> uploadMultipleFiles(List<MultipartFile> files, Long userId, FileType fileType);

    // ✅ 사용자의 파일들 조회 (특정 타입 또는 전체)
    List<FileResponseDto> getUserFiles(Long userId, FileType fileType);

    // ✅ 사용자의 모든 파일 조회
    List<FileResponseDto> getUserAllFiles(Long userId);

    // 파일 ID로 단일 파일 조회
    FileResponseDto getFileById(Long fileId);

    // ✅ 파일 삭제 (기본)
    void deleteFile(Long fileId);

    // ✅ 파일 소유권 확인 후 삭제
    void deleteFileWithPermission(Long fileId, Long userId);

    // ✅ 사용자의 특정 타입 파일들 삭제
    void deleteUserFilesByType(Long userId, FileType fileType);

    // ✅ 파일 다운로드 (기본)
    byte[] downloadFile(Long fileId);

    // ✅ 파일 소유권 확인 후 다운로드
    byte[] downloadFileWithPermission(Long fileId, Long userId);

    // 파일 경로로 다운로드
    byte[] downloadFileByPath(String filePath);

}
