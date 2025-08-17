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
    public FileResponseDto uploadFile(MultipartFile file, Long userId, FileType fileType) {
        validateFile(file);

        try {
            // 사용자 기반 파일명 생성
            String storedFileName = generateUserBasedFileName(file.getOriginalFilename(), userId, fileType);

            // 단순한 구조: images 또는 others 폴더에 바로 저장
            String subPath = isImageFile(file.getContentType()) ? imagePath : filePath;
            String fullPath = filePrefix + subPath;

            createDirectories(fullPath);

            Path saveFilePath = Paths.get(fullPath, storedFileName);
            Files.copy(file.getInputStream(), saveFilePath);


            // 사용자 소유 파일로 저장
            File fileEntity = File.builder()
                    .originalFileName(file.getOriginalFilename())
                    .storedFileName(storedFileName)
                    .filePath(saveFilePath.toString())
                    .fileUrl("")
                    .fileSize(file.getSize())
                    .contentType(file.getContentType())
                    .fileType(fileType)
                    .userId(userId) // 사용자 ID로 소유권 관리
                    .build();

            File saved = fileRepository.save(fileEntity);

            // ✅ ID 기반 URL 생성
            String fileUrl = "/api/files/preview/" + saved.getId();
            saved.setFileUrl(fileUrl);
            saved = fileRepository.save(saved);

            log.info("사용자 파일 업로드 완료: userId={}, fileId={}, fileName={}",
                    userId, saved.getId(), file.getOriginalFilename());

            return FileResponseDto.from(saved);

        } catch (IOException e) {
            log.error("파일 업로드 실패: {}", e.getMessage());
            throw new RuntimeException("파일 업로드에 실패했습니다.", e);
        }
    }


    @Override
    public FileResponseDto uploadProfileImageAndUpdate(MultipartFile file, Long userId) {
        // 기존 프로필 이미지 삭제 (사용자 소유 파일 중에서)
        deleteUserProfileImages(userId);

        // 새 프로필 이미지 업로드
        FileResponseDto uploadedFile = uploadFile(file, userId, FileType.IMAGE);

        // UserAccount의 profileImage 필드 업데이트
        UserAccount user = userAccountRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        // ✅ File 엔터티 조회
        File fileEntity = fileRepository.findById(uploadedFile.getId())
                .orElseThrow(() -> new RuntimeException("업로드된 파일을 찾을 수 없습니다."));

        user.setProfile(fileEntity);
        user.updateAt();
        userAccountRepository.save(user);

        log.info("프로필 이미지 업데이트 완료: userId={}, fileUrl={}", userId, uploadedFile.getFileUrl());

        return uploadedFile;
    }

    // 사용자 기반 파일명 생성
    private String generateUserBasedFileName(String originalFilename, Long userId, FileType fileType) {
        String extension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }

        String date = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String time = String.valueOf(System.currentTimeMillis()).substring(8);

        return String.format("user_%d_%s_%s_%s%s",
                userId, fileType.name().toLowerCase(), date, time, extension);
    }

    // 사용자의 프로필 이미지들 삭제
    private void deleteUserProfileImages(Long userId) {
        // UserAccount에서 현재 profile 확인
        UserAccount user = userAccountRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        // 기존 프로필 파일이 있으면 삭제
        if (user.getProfile()!= null) {
            deleteFile(user.getProfile().getId());
        }
    }

    // 외부 URL인지 확인
    private boolean isExternalUrl(String url) {
        return url != null && (url.startsWith("http://") || url.startsWith("https://"));
    }

    @Override
    public List<FileResponseDto> uploadMultipleFiles(List<MultipartFile> files, Long userId, FileType fileType) {
        return files.stream()
                .map(file -> uploadFile(file, userId, fileType))
                .collect(Collectors.toList());
    }

    // ✅ 사용자의 파일들 조회
    @Override
    @Transactional(readOnly = true)
    public List<FileResponseDto> getUserFiles(Long userId, FileType fileType) {
        List<File> files;
        if (fileType != null) {
            files = fileRepository.findByUserIdAndFileTypeOrderByCreatedAtDesc(userId, fileType);
        } else {
            files = fileRepository.findByUserIdOrderByCreatedAtDesc(userId);
        }
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

    // ✅ 사용자의 특정 타입 파일들 삭제
    @Override
    public void deleteUserFilesByType(Long userId, FileType fileType) {
        List<File> files = fileRepository.findByUserIdAndFileType(userId, fileType);
        files.forEach(file -> deleteFile(file.getId()));

        log.info("사용자 파일 타입별 삭제: userId={}, fileType={}, count={}",
                userId, fileType, files.size());
    }

    // ✅ 사용자의 모든 파일 조회
    @Override
    @Transactional(readOnly = true)
    public List<FileResponseDto> getUserAllFiles(Long userId) {
        return getUserFiles(userId, null);
    }

    // ✅ 파일 소유권 확인 후 삭제
    @Override
    public void deleteFileWithPermission(Long fileId, Long userId) {
        File file = fileRepository.findByIdAndUserId(fileId, userId)
                .orElseThrow(() -> new IllegalArgumentException("본인의 파일이 아니거나 파일을 찾을 수 없습니다."));

        deleteFile(fileId);
    }

    @Override
    @Transactional(readOnly = true)
    public byte[] downloadFile(Long fileId) {
        File file = fileRepository.findById(fileId)
                .orElseThrow(() -> new IllegalArgumentException("파일을 찾을 수 없습니다."));

        return downloadFileByPath(file.getFilePath());
    }

    // ✅ 파일 소유권 확인 후 다운로드
    @Override
    @Transactional(readOnly = true)
    public byte[] downloadFileWithPermission(Long fileId, Long userId) {
        File file = fileRepository.findByIdAndUserId(fileId, userId)
                .orElseThrow(() -> new IllegalArgumentException("본인의 파일이 아니거나 파일을 찾을 수 없습니다."));

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
        if (contentType == null) {
            return false;
        }

        // 로그로 확인
        log.debug("Content-Type 확인: {}", contentType);

        return contentType.startsWith("image/") ||
                contentType.equals("application/octet-stream"); // 일부 브라우저에서 이미지를 이렇게 보냄
    }

    // 또는 파일 확장자도 함께 확인
    private boolean isImageFile(String contentType, String fileName) {
        // Content-Type 확인
        if (contentType != null && contentType.startsWith("image/")) {
            return true;
        }

        // 확장자 확인 (fallback)
        if (fileName != null) {
            String extension = fileName.toLowerCase();
            return extension.endsWith(".jpg") || extension.endsWith(".jpeg") ||
                    extension.endsWith(".png") || extension.endsWith(".gif") ||
                    extension.endsWith(".webp") || extension.endsWith(".jfif");
        }

        return false;
    }


}
