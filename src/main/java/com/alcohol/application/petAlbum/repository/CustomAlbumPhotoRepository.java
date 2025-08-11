package com.alcohol.application.petAlbum.repository;

import com.alcohol.application.petAlbum.entity.CustomAlbumPhoto;
import com.alcohol.application.petAlbum.entity.CustomAlbum;
import com.alcohol.common.files.entity.File;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;

public interface CustomAlbumPhotoRepository extends JpaRepository<CustomAlbumPhoto, Long> {

    // 앨범별 사진 조회 (순서대로)
    List<CustomAlbumPhoto> findByCustomAlbumOrderByDisplayOrderAscAddedAtAsc(CustomAlbum customAlbum);

    // 앨범의 모든 사진 삭제
    void deleteByCustomAlbum(CustomAlbum customAlbum);

    // 특정 파일이 특정 앨범에 있는지 확인
    boolean existsByCustomAlbumAndFile(CustomAlbum customAlbum, File file);

    // 앨범별 사진 개수
    long countByCustomAlbum(CustomAlbum customAlbum);

    // 특정 앨범의 특정 사진 찾기
    Optional<CustomAlbumPhoto> findByCustomAlbumAndFile(CustomAlbum customAlbum, File file);

    // 앨범의 고유 펫 개수 조회
    @Query("SELECT COUNT(DISTINCT cap.pet.petId) FROM CustomAlbumPhoto cap WHERE cap.customAlbum = :album")
    long countUniquePetsByCustomAlbum(@Param("album") CustomAlbum album);

    // 앨범별 모든 사진 조회
    List<CustomAlbumPhoto> findByCustomAlbum(CustomAlbum customAlbum);

}
