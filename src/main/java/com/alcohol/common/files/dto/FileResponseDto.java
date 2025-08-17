package com.alcohol.common.files.dto;

import com.alcohol.common.files.entity.FileType;
import com.alcohol.common.files.entity.File;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FileResponseDto {
    private Long id;
    private String originalFileName;
    private String fileUrl;
    private Long fileSize;
    private String contentType;
    private FileType fileType;

    public static FileResponseDto from(File file) {
        return FileResponseDto.builder()
                .id(file.getId())
                .originalFileName(file.getOriginalFileName())
                .fileUrl(file.getFileUrl())
                .fileSize(file.getFileSize())
                .contentType(file.getContentType())
                .fileType(file.getFileType())
                .build();
    }
}
