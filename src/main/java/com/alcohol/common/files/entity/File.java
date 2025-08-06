package com.alcohol.common.files.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "file")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class File {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String originalFileName;  // 원본 파일명

    @Column(nullable = false)
    private String storedFileName;    // 저장된 파일명 (UUID)

    @Column(nullable = false)
    private String filePath;          // 파일 저장 경로

    @Column(nullable = false)
    private String fileUrl;           // 접근 가능한 URL

    @Column(nullable = false)
    private Long fileSize;            // 파일 크기

    @Column(nullable = false)
    private String contentType;       // MIME 타입

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FileType fileType;        // 파일 용도 (PROFILE, PET_ALBUM)

    @Column
    private Long relatedId;           // 연관된 엔터티 ID (userId, petId 등)

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}