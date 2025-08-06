package com.alcohol.application.petAlbum.service;

import com.alcohol.application.petAlbum.dto.PetAlbumResponseDto;
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
}
