package com.alcohol.application.Album.entity;

import com.alcohol.application.pet.entity.Pet;
import com.alcohol.common.files.entity.File;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "pet_album_photo")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PetAlbumPhoto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pet_id", nullable = false)
    private Pet pet;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "file_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private File file;

    private String caption;
    private Integer displayOrder;

    // 시간 필드 추가
    @Column(nullable = false)
    private LocalDateTime addedAt;

    @PrePersist
    protected void onCreate() {
        addedAt = LocalDateTime.now();
    }

    @PreUpdate
    private void validateFileExists() {
        if (file == null || file.getId() == null) {
            throw new IllegalStateException("파일이 존재해야 합니다.");
        }
    }
}

