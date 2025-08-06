package com.alcohol.common.files.repository;

import com.alcohol.common.files.entity.File;
import com.alcohol.common.files.entity.FileType;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface FileRepository extends JpaRepository<File, Long> {

    // 특정 사용자의 프로필 이미지 조회
    List<File> findByFileTypeAndRelatedId(FileType fileType, Long relatedId);

    // 특정 펫의 앨범 이미지들 조회
    List<File> findByFileTypeAndRelatedIdOrderByCreatedAtDesc(FileType fileType, Long relatedId);

    // 파일 타입별 조회
    List<File> findByFileType(FileType fileType);
}
