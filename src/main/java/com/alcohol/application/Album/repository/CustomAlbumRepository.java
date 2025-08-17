package com.alcohol.application.Album.repository;

import com.alcohol.application.Album.entity.CustomAlbum;
import com.alcohol.application.userAccount.entity.UserAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;

public interface CustomAlbumRepository extends JpaRepository<CustomAlbum, Long> {

    // 사용자별 앨범 조회 (최신순)
    List<CustomAlbum> findByCreatorOrderByCreatedAtDesc(UserAccount creator);

    // 앨범 소유자 확인
    boolean existsByIdAndCreator(Long id, UserAccount creator);

    // 앨범과 사진들 함께 조회 (N+1 방지)
    @Query("SELECT ca FROM CustomAlbum ca " +
            "LEFT JOIN FETCH ca.photos cap " +
            "LEFT JOIN FETCH cap.pet " +
            "LEFT JOIN FETCH cap.file " +
            "WHERE ca.id = :albumId")
    Optional<CustomAlbum> findByIdWithPhotos(@Param("albumId") Long albumId);
}
