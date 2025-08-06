package com.alcohol.common.files.service;

import com.alcohol.common.files.dto.FileResponseDto;
import com.alcohol.common.files.entity.FileType;
import com.alcohol.common.files.entity.File;
import com.alcohol.common.files.repository.FileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class FileServiceImpl implements FileService {

    private final FileRepository fileRepository;

    @Value("${file.upload.path:/uploads}")
    private String uploadPath;

    @Value("${file.upload.url:/api/files}")
    private String fileUrlPrefix;

    @Override
    public FileResponseDto uploadFile(MultipartFile file, FileType fileType, Long relatedId) {
        validateFile(file);

        try {
            // 파일 저장
            String storedFileName = generateStoredFileName(file.getOriginalFilename());
            String datePath = generateDatePath();
            String fullPath = uploadPath + "/" + fileType.name().toLowerCase() + "/" + datePath;

            // 디렉토리 생성
            createDirectories(fullPath);

            // 파일 저장
            Path filePath = Paths.get(fullPath, storedFileName);
            Files.copy(file.getInputStream(), filePath);

            // 파일 정보 DB 저장
            File fileEntity = File.builder()
                    .originalFileName(file.getOriginalFilename())
                    .storedFileName(storedFileName)
                    .filePath(filePath.toString())
                    .fileUrl(fileUrlPrefix + "/" + fileType.name().toLowerCase() + "/" + datePath + "/" + storedFileName)
                    .fileSize(file.getSize())
                    .contentType(file.getContentType())
                    .fileType(fileType)
                    .relatedId(relatedId)
                    .build();

            File saved = fileRepository.save(fileEntity);
            log.info("파일 업로드 완료: {}", saved.getOriginalFileName());

            return FileResponseDto.from(saved);

        } catch (IOException e) {
            log.error("파일 업로드 실패: {}", e.getMessage());
            throw new RuntimeException("파일 업로드에 실패했습니다.", e);
        }
    }

    @Override
    public List<FileResponseDto> uploadMultipleFiles(List<MultipartFile> files, FileType fileType, Long relatedId) {
        return files.stream()
                .map(file -> uploadFile(file, fileType, relatedId))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<FileResponseDto> getFilesByTypeAndRelatedId(FileType fileType, Long relatedId) {
        List<File> files = fileRepository.findByFileTypeAndRelatedIdOrderByCreatedAtDesc(fileType, relatedId);
        return files.stream()
                .map(FileResponseDto::from)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public FileResponseDto getFileById(Long fileId) {
        File file = fileRepository.findById(fileId)
                .orElseThrow(() -> new IllegalArgumentException("파일을 찾을 수 없습니다."));
        return FileResponseDto.from(file);
    }

    @Override
    public void deleteFile(Long fileId) {
        File file = fileRepository.findById(fileId)
                .orElseThrow(() -> new IllegalArgumentException("파일을 찾을 수 없습니다."));

        // 실제 파일 삭제
        try {
            Files.deleteIfExists(Paths.get(file.getFilePath()));
        } catch (IOException e) {
            log.error("파일 삭제 실패: {}", e.getMessage());
        }

        // DB에서 삭제
        fileRepository.delete(file);
        log.info("파일 삭제 완료: {}", file.getOriginalFileName());
    }

    @Override
    public void deleteFilesByTypeAndRelatedId(FileType fileType, Long relatedId) {
        List<File> files = fileRepository.findByFileTypeAndRelatedId(fileType, relatedId);
        files.forEach(file -> deleteFile(file.getId()));
    }

    @Override
    @Transactional(readOnly = true)
    public byte[] downloadFile(Long fileId) {
        File file = fileRepository.findById(fileId)
                .orElseThrow(() -> new IllegalArgumentException("파일을 찾을 수 없습니다."));

        return downloadFileByPath(file.getFilePath());
    }

    @Override
    @Transactional(readOnly = true)
    public byte[] downloadFileByPath(String filePath) {
        try {
            Path path = Paths.get(filePath);
            return Files.readAllBytes(path);
        } catch (IOException e) {
            log.error("파일 다운로드 실패: {}", e.getMessage());
            throw new RuntimeException("파일을 읽을 수 없습니다.", e);
        }
    }

    // 유틸리티 메서드들
    private void validateFile(MultipartFile file) {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("빈 파일은 업로드할 수 없습니다.");
        }

        // 이미지 파일만 허용
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new IllegalArgumentException("이미지 파일만 업로드 가능합니다.");
        }

        // 파일 크기 제한 (10MB)
        if (file.getSize() > 10 * 1024 * 1024) {
            throw new IllegalArgumentException("파일 크기는 10MB를 초과할 수 없습니다.");
        }
    }

    private String generateStoredFileName(String originalFilename) {
        String extension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        return UUID.randomUUID().toString() + extension;
    }

    private String generateDatePath() {
        LocalDate now = LocalDate.now();
        return String.format("%d/%02d/%02d", now.getYear(), now.getMonthValue(), now.getDayOfMonth());
    }

    private void createDirectories(String path) throws IOException {
        Path directory = Paths.get(path);
        if (!Files.exists(directory)) {
            Files.createDirectories(directory);
        }
    }
}
