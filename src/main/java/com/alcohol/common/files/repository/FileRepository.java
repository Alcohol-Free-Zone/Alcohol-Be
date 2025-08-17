package com.alcohol.common.files.repository;

import com.alcohol.common.files.entity.File;
import com.alcohol.common.files.entity.FileType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FileRepository extends JpaRepository<File, Long> {
// ============ 사용자별 파일 관리 ============

    // ✅ 사용자의 모든 파일 조회 (최신순)
    List<File> findByUserIdOrderByCreatedAtDesc(Long userId);

    // ✅ 사용자의 파일 타입별 조회 (최신순)
    List<File> findByUserIdAndFileTypeOrderByCreatedAtDesc(Long userId, FileType fileType);

    // ✅ 사용자의 파일 타입별 조회 (정렬 없음)
    List<File> findByUserIdAndFileType(Long userId, FileType fileType);

    // ✅ 파일 소유권 확인
    Optional<File> findByIdAndUserId(Long fileId, Long userId);

}
