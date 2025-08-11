package com.alcohol.common.files.service;

import com.alcohol.common.files.dto.FileResponseDto;
import com.alcohol.common.files.entity.FileType;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

public interface FileService {

    FileResponseDto uploadFile(MultipartFile file, FileType fileType, Long relatedId);

    FileResponseDto uploadProfileImageAndUpdate(MultipartFile file, Long userId);

    List<FileResponseDto> uploadMultipleFiles(List<MultipartFile> files, FileType fileType, Long relatedId);

    List<FileResponseDto> getFilesByTypeAndRelatedId(FileType fileType, Long relatedId);

    FileResponseDto getFileById(Long fileId);

    void deleteFile(Long fileId);

    void deleteFilesByTypeAndRelatedId(FileType fileType, Long relatedId);

    // 파일 다운로드용 메서드 추가
    byte[] downloadFile(Long fileId);

    byte[] downloadFileByPath(String filePath);

    ResponseEntity<Resource> serveImageFile(String fileName);

    ResponseEntity<Resource> serveOtherFile(String fileName);
}
