package com.alcohol.application.Album.repository;

import com.alcohol.application.Album.entity.PetAlbumPhoto;
import com.alcohol.application.pet.entity.Pet;
import com.alcohol.common.files.entity.File;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PetAlbumPhotoRepository extends JpaRepository<PetAlbumPhoto, Long> {

    // 펫별 사진 조회 (순서대로)
    List<PetAlbumPhoto> findByPetOrderByDisplayOrderAsc(Pet pet);

    // 펫별 모든 사진 조회
    List<PetAlbumPhoto> findByPet(Pet pet);

    // 파일 ID로 사용 현황 조회
    List<PetAlbumPhoto> findByFileId(Long fileId);

    // 특정 펫과 파일의 매핑 조회
    Optional<PetAlbumPhoto> findByPetAndFile(Pet pet, File file);

    // 특정 펫과 파일의 매핑 존재 여부
    boolean existsByPetAndFile(Pet pet, File file);

    // 특정 펫과 파일의 매핑 삭제
    void deleteByPetAndFile(Pet pet, File file);

    // 펫별 사진 개수 조회
    long countByPet(Pet pet);

    // 파일별 사용 개수 조회
    long countByFileId(Long fileId);
}
