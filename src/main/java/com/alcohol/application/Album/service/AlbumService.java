package com.alcohol.application.Album.service;

import com.alcohol.application.Album.dto.CustomAlbumCreateRequestDto;
import com.alcohol.application.Album.dto.CustomAlbumResponseDto;
import com.alcohol.application.Album.dto.AlbumResponseDto;
import com.alcohol.application.userAccount.entity.UserAccount;
import com.alcohol.common.files.dto.FileResponseDto;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface AlbumService {

    // ============ 펫 앨범 관련 ============

    // 펫 앨범에 사진 업로드
    List<FileResponseDto> uploadPhotos(Long petId, List<MultipartFile> files, UserAccount user);

    // 펫 앨범 전체 조회
    AlbumResponseDto getPetAlbum(Long petId, UserAccount user);

    // 펫 앨범 사진들만 조회
    List<FileResponseDto> getAlbumPhotos(Long petId, UserAccount user);

    // 펫 앨범에서 사진 제거 (매핑 삭제)
    void deletePhoto(Long petId, Long fileId, UserAccount user);

    // 펫 앨범 전체 매핑 삭제
    void deleteAllPhotos(Long petId, UserAccount user);

    // 펫 앨범 통계 조회
    AlbumResponseDto.AlbumStats getAlbumStats(Long petId, UserAccount user);

    // 기존 파일을 펫 앨범에 추가
    void addExistingFileToPetAlbum(Long petId, Long fileId, UserAccount user);

    // ============ 커스텀 앨범 관련 ============

    // 커스텀 앨범 생성
    CustomAlbumResponseDto createCustomAlbum(CustomAlbumCreateRequestDto dto, UserAccount creator);

    // 내 커스텀 앨범 목록 조회
    List<CustomAlbumResponseDto> getMyCustomAlbums(UserAccount user);

    // 커스텀 앨범 상세 조회
    CustomAlbumResponseDto getCustomAlbum(Long albumId);

    // 커스텀 앨범 수정
    CustomAlbumResponseDto updateCustomAlbum(Long albumId, CustomAlbumCreateRequestDto dto, UserAccount user);

    // 커스텀 앨범 삭제
    void deleteCustomAlbum(Long albumId, UserAccount user);

    // 커스텀 앨범에 사진 업로드
    List<CustomAlbumResponseDto.CustomAlbumPhotoDto> addPhotosToCustomAlbum(
            Long albumId, List<MultipartFile> files, List<Long> petIds,
            List<String> captions, UserAccount user);

    // 기존 파일을 커스텀 앨범에 추가
    CustomAlbumResponseDto.CustomAlbumPhotoDto addExistingFileToCustomAlbum(
            Long albumId, Long fileId, Long petId, String caption, UserAccount user);

    // 커스텀 앨범에서 사진 제거
    void removePhotoFromCustomAlbum(Long albumId, Long photoId, UserAccount user);

    // 사진 캡션 수정
    void updatePhotoCaption(Long albumId, Long photoId, String caption, UserAccount user);

    // 사진 순서 변경
    void updatePhotoOrder(Long albumId, List<Long> photoIds, UserAccount user);

    // 커스텀 앨범 소유권 검증
    void validateCustomAlbumOwner(Long albumId, UserAccount user);
}
