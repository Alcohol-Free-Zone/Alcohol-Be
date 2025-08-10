package com.alcohol.application.petAlbum.service;

import com.alcohol.application.petAlbum.dto.CustomAlbumCreateRequestDto;
import com.alcohol.application.petAlbum.dto.CustomAlbumResponseDto;
import com.alcohol.application.petAlbum.dto.PetAlbumResponseDto;
import com.alcohol.application.userAccount.entity.UserAccount;
import com.alcohol.common.files.dto.FileResponseDto;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface PetAlbumService {

    List<FileResponseDto> uploadPhotos(Long petId, List<MultipartFile> files);

    PetAlbumResponseDto getPetAlbum(Long petId);

    List<FileResponseDto> getAlbumPhotos(Long petId);

    void deletePhoto(Long petId, Long fileId);

    void deleteAllPhotos(Long petId);

    PetAlbumResponseDto.AlbumStats getAlbumStats(Long petId);

    // 커스텀 앨범 관련 메서드들
    CustomAlbumResponseDto createCustomAlbum(CustomAlbumCreateRequestDto dto, UserAccount creator);
    List<CustomAlbumResponseDto> getMyCustomAlbums(UserAccount user);
    CustomAlbumResponseDto getCustomAlbum(Long albumId);
    CustomAlbumResponseDto updateCustomAlbum(Long albumId, CustomAlbumCreateRequestDto dto, UserAccount user);
    void deleteCustomAlbum(Long albumId, UserAccount user);

    // 커스텀 앨범 사진 관리
    List<CustomAlbumResponseDto.CustomAlbumPhotoDto> addPhotosToCustomAlbum(
            Long albumId, List<MultipartFile> files, List<Long> petIds, List<String> captions, UserAccount user);
    void removePhotoFromCustomAlbum(Long albumId, Long photoId, UserAccount user);
    void updatePhotoCaption(Long albumId, Long photoId, String caption, UserAccount user);
    void updatePhotoOrder(Long albumId, List<Long> photoIds, UserAccount user);

    // 유틸리티
    void validateCustomAlbumOwner(Long albumId, UserAccount user);
}
