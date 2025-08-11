package com.alcohol.common.files.service;

import com.alcohol.application.userAccount.entity.UserAccount;
import com.alcohol.application.userAccount.repository.UserAccountRepository;
import com.alcohol.common.files.dto.FileResponseDto;
import com.alcohol.common.files.entity.FileType;
import com.alcohol.common.files.entity.File;
import com.alcohol.common.files.repository.FileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class FileServiceImpl implements FileService {

    private final FileRepository fileRepository;
    private final UserAccountRepository userAccountRepository;

    @Value("${files.prefix}")
    private String filePrefix;         // c:/upload

    @Value("${files.imagePath}")
    private String imagePath;          // /images

    @Value("${files.filePath}")
    private String filePath;           // /others

    @Override
    public FileResponseDto uploadFile(MultipartFile file, FileType fileType, Long relatedId) {
        validateFile(file);

        try {
            // 의미있는 파일명 생성 (FileType별로 구분)
            String storedFileName = generateMeaningfulFileName(file.getOriginalFilename(), fileType, relatedId);

            // 단순한 구조: images 또는 others 폴더에 바로 저장
            String subPath = isImageFile(file.getContentType()) ? imagePath : filePath;
            String fullPath = filePrefix + subPath;

            createDirectories(fullPath);

            Path saveFilePath = Paths.get(fullPath, storedFileName);
            Files.copy(file.getInputStream(), saveFilePath);

            String urlPath = subPath.equals(imagePath) ? "/images" : "/others";
            String fileUrl = "/api/files" + urlPath + "/" + storedFileName;

            // 기존 FileType과 relatedId 그대로 저장
            File fileEntity = File.builder()
                    .originalFileName(file.getOriginalFilename())
                    .storedFileName(storedFileName)
                    .filePath(saveFilePath.toString())
                    .fileUrl(fileUrl)
                    .fileSize(file.getSize())
                    .contentType(file.getContentType())
                    .fileType(fileType) // 원래 FileType 사용
                    .relatedId(relatedId) // relatedId도 저장
                    .build();

            File saved = fileRepository.save(fileEntity);
            log.info("파일 업로드 완료: {} -> {}", file.getOriginalFilename(), fullPath);

            return FileResponseDto.from(saved);

        } catch (IOException e) {
            log.error("파일 업로드 실패: {}", e.getMessage());
            throw new RuntimeException("파일 업로드에 실패했습니다.", e);
        }
    }


    @Override
    public FileResponseDto uploadProfileImageAndUpdate(MultipartFile file, Long userId) {
        // 기존 프로필 이미지 삭제
        deleteFilesByTypeAndRelatedId(FileType.PROFILE, userId);

        // 새 프로필 이미지 업로드
        FileResponseDto uploadedFile = uploadFile(file, FileType.PROFILE, userId);

        // UserAccount의 profileImage 필드 업데이트
        UserAccount user = userAccountRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        user.setProfileImage(uploadedFile.getFileUrl()); // 새 파일 URL로 업데이트
        userAccountRepository.save(user);

        log.info("프로필 이미지 업데이트 완료: userId={}, fileUrl={}", userId, uploadedFile.getFileUrl());

        return uploadedFile;
    }

    // 의미있는 파일명 생성 (FileType별로 구분)
    private String generateMeaningfulFileName(String originalFilename, FileType fileType, Long relatedId) {
        String extension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }

        String date = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String time = String.valueOf(System.currentTimeMillis()).substring(8); // 뒷자리만

        switch (fileType) {
            case PROFILE:
                return String.format("profile_user%d_%s_%s%s", relatedId, date, time, extension);
            case PET_ALBUM:
                return String.format("pet_album_pet%d_%s_%s%s", relatedId, date, time, extension);
            case CUSTOM_ALBUM:
                return String.format("custom_album_alb%d_%s_%s%s", relatedId, date, time, extension);
            default:
                return String.format("%s_%d_%s_%s%s", fileType.name().toLowerCase(), relatedId, date, time, extension);
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

        // 파일 크기 제한 (100MB)
        if (file.getSize() > 100 * 1024 * 1024) {
            throw new IllegalArgumentException("파일 크기는 100MB를 초과할 수 없습니다.");
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

    // ✅ 추가: 이미지 파일인지 확인하는 메서드
    private boolean isImageFile(String contentType) {
        return contentType != null && contentType.startsWith("image/");
    }


    // 이미지 파일 서빙 (Controller에서 이동)
    @Override
    public ResponseEntity<Resource> serveImageFile(String fileName) {
        try {
            Path filePath = Paths.get(filePrefix + imagePath, fileName);
            Resource resource = new UrlResource(filePath.toUri());

            if (resource.exists() && resource.isReadable()) {
                return ResponseEntity.ok()
                        .contentType(MediaType.parseMediaType(getContentType(fileName)))
                        .body(resource);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (MalformedURLException e) {
            log.error("이미지 파일 서빙 실패: {}", fileName, e);
            return ResponseEntity.badRequest().build();
        }
    }

    // 기타 파일 서빙 (Controller에서 이동)
    @Override
    public ResponseEntity<Resource> serveOtherFile(String fileName) {
        try {
            Path filePath = Paths.get(filePrefix + this.filePath, fileName);
            Resource resource = new UrlResource(filePath.toUri());

            if (resource.exists() && resource.isReadable()) {
                return ResponseEntity.ok()
                        .contentType(MediaType.APPLICATION_OCTET_STREAM)
                        .header(HttpHeaders.CONTENT_DISPOSITION,
                                "attachment; filename=\"" + fileName + "\"")
                        .body(resource);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (MalformedURLException e) {
            log.error("기타 파일 서빙 실패: {}", fileName, e);
            return ResponseEntity.badRequest().build();
        }
    }

    // 컨텐트 타입 결정 (Controller에서 이동)
    private String getContentType(String fileName) {
        String extension = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
        switch (extension) {
            case "jpg":
            case "jpeg": return "image/jpeg";
            case "png": return "image/png";
            case "gif": return "image/gif";
            case "webp": return "image/webp";
            default: return "application/octet-stream";
        }
    }
}
