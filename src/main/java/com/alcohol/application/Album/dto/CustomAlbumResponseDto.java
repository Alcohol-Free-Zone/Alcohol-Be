package com.alcohol.application.Album.dto;

import com.alcohol.application.Album.entity.CustomAlbum;
import com.alcohol.common.files.dto.FileResponseDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CustomAlbumResponseDto {

    private Long id;
    private String name;
    private String description;
    private Long creatorId;
    private String creatorName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<CustomAlbumPhotoDto> photos;
    private AlbumStats stats;

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class CustomAlbumPhotoDto {
        private Long id;
        private Long petId;
        private String petName;
        private FileResponseDto file;
        private String caption;
        private Integer displayOrder;
        private LocalDateTime addedAt;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class AlbumStats {
        private int totalPhotos;
        private int uniquePets;
        private long totalFileSize;

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

    public static CustomAlbumResponseDto from(CustomAlbum album) {
        List<CustomAlbumPhotoDto> photoDtos = new ArrayList<>();
        if (album.getPhotos() != null) {
            photoDtos = album.getPhotos().stream()
                    .map(photo -> CustomAlbumPhotoDto.builder()
                            .id(photo.getId())
                            .petId(photo.getPet().getPetId())
                            .petName(photo.getPet().getPetName())
                            .file(FileResponseDto.from(photo.getFile()))
                            .caption(photo.getCaption())
                            .displayOrder(photo.getDisplayOrder())
                            .addedAt(photo.getAddedAt())
                            .build())
                    .collect(Collectors.toList());
        }

        AlbumStats stats = AlbumStats.builder()
                .totalPhotos(photoDtos.size())
                .uniquePets((int) photoDtos.stream().mapToLong(CustomAlbumPhotoDto::getPetId).distinct().count())
                .totalFileSize(photoDtos.stream().mapToLong(photo -> photo.getFile().getFileSize()).sum())
                .build();

        return CustomAlbumResponseDto.builder()
                .id(album.getId())
                .name(album.getName())
                .description(album.getDescription())
                .creatorId(album.getCreator().getId())
                .creatorName(album.getCreator().getNickname())
                .createdAt(album.getCreatedAt())
                .updatedAt(album.getUpdatedAt())
                .photos(photoDtos)
                .stats(stats)
                .build();
    }
}
