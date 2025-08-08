package com.alcohol.application.petAlbum.dto;

import com.alcohol.common.files.dto.FileResponseDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PetAlbumResponseDto {

    private Long petId;
    private String petName;
    private List<AlbumPhoto> photos;
    private AlbumStats stats;

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class AlbumPhoto {
        private Long fileId;
        private String originalFileName;
        private String thumbnailUrl;
        private String fullUrl;
        private Long fileSize;
        private String contentType;
        private LocalDateTime uploadedAt;

        public static AlbumPhoto from(FileResponseDto file) {
            return AlbumPhoto.builder()
                    .fileId(file.getId())
                    .originalFileName(file.getOriginalFileName())
                    .thumbnailUrl(file.getFileUrl()) // 썸네일 URL (현재는 원본과 동일)
                    .fullUrl(file.getFileUrl())
                    .fileSize(file.getFileSize())
                    .contentType(file.getContentType())
                    .build();
        }
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class AlbumStats {
        private int totalPhotos;
        private long totalFileSize;

        // 파일 크기를 MB로 변환
        public double getTotalFileSizeMB() {
            return totalFileSize / (1024.0 * 1024.0);
        }

        // 파일 크기를 사람이 읽기 쉬운 형태로 변환
        public String getFormattedFileSize() {
            if (totalFileSize < 1024) {
                return totalFileSize + " B";
            } else if (totalFileSize < 1024 * 1024) {
                return String.format("%.1f KB", totalFileSize / 1024.0);
            } else if (totalFileSize < 1024 * 1024 * 1024) {
                return String.format("%.1f MB", totalFileSize / (1024.0 * 1024.0));
            } else {
                return String.format("%.1f GB", totalFileSize / (1024.0 * 1024.0 * 1024.0));
            }
        }
    }
}
