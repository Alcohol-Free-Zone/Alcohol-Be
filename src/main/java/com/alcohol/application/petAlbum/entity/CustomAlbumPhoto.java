package com.alcohol.application.petAlbum.entity;

import com.alcohol.application.pet.entity.Pet;
import com.alcohol.common.files.entity.File;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "custom_album_photo")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CustomAlbumPhoto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "custom_album_id", nullable = false)
    private CustomAlbum customAlbum;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pet_id", nullable = false)
    private Pet pet;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "file_id", nullable = false)
    private File file;

    @Column(nullable = false)
    private LocalDateTime addedAt;

    @Column
    private String caption; // 사진별 캡션 (선택사항)

    @Column(nullable = false)
    private Integer displayOrder = 0; // 앨범 내 사진 순서

    @PrePersist
    protected void onCreate() {
        addedAt = LocalDateTime.now();
    }
}
