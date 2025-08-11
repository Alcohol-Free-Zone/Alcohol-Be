package com.alcohol.application.petAlbum.service;

import com.alcohol.application.pet.entity.Pet;
import com.alcohol.application.pet.repository.PetRepository;
import com.alcohol.application.petAlbum.dto.CustomAlbumCreateRequestDto;
import com.alcohol.application.petAlbum.dto.CustomAlbumResponseDto;
import com.alcohol.application.petAlbum.dto.PetAlbumResponseDto;
import com.alcohol.application.petAlbum.entity.CustomAlbum;
import com.alcohol.application.petAlbum.entity.CustomAlbumPhoto;
import com.alcohol.application.petAlbum.repository.CustomAlbumPhotoRepository;
import com.alcohol.application.petAlbum.repository.CustomAlbumRepository;
import com.alcohol.application.userAccount.entity.UserAccount;
import com.alcohol.common.files.dto.FileResponseDto;
import com.alcohol.common.files.entity.File;
import com.alcohol.common.files.entity.FileType;
import com.alcohol.common.files.repository.FileRepository;
import com.alcohol.common.files.service.FileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class PetAlbumServiceImpl implements PetAlbumService {

    private final FileService fileService;
    private final PetRepository petRepository;
    private final FileRepository fileRepository;
    private final CustomAlbumPhotoRepository customAlbumPhotoRepository;
    private final CustomAlbumRepository customAlbumRepository;

    @Override
    public List<FileResponseDto> uploadPhotos(Long petId, List<MultipartFile> files) {
        // 펫 존재 확인
        Pet pet = petRepository.findById(petId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 펫입니다."));

        // 파일 개수 제한 (한 번에 최대 10개)
        if (files.size() > 10) {
            throw new IllegalArgumentException("한 번에 최대 10개의 사진만 업로드할 수 있습니다.");
        }

        // 파일 업로드
        List<FileResponseDto> uploadedFiles = fileService.uploadMultipleFiles(files, FileType.PET_ALBUM, petId);

        log.info("펫 앨범 사진 업로드 완료: petId={}, count={}", petId, files.size());
        return uploadedFiles;
    }

    @Override
    @Transactional(readOnly = true)
    public PetAlbumResponseDto getPetAlbum(Long petId) {
        // 펫 정보 조회
        Pet pet = petRepository.findById(petId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 펫입니다."));

        // 앨범 사진들 조회
        List<FileResponseDto> photos = fileService.getFilesByTypeAndRelatedId(FileType.PET_ALBUM, petId);

        // 앨범 사진 변환
        List<PetAlbumResponseDto.AlbumPhoto> albumPhotos = photos.stream()
                .map(PetAlbumResponseDto.AlbumPhoto::from)
                .collect(Collectors.toList());

        // 통계 정보
        PetAlbumResponseDto.AlbumStats stats = PetAlbumResponseDto.AlbumStats.builder()
                .totalPhotos(photos.size())
                .totalFileSize(photos.stream().mapToLong(FileResponseDto::getFileSize).sum())
                .build();

        return PetAlbumResponseDto.builder()
                .petId(petId)
                .petName(pet.getPetName())
                .photos(albumPhotos)
                .stats(stats)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public List<FileResponseDto> getAlbumPhotos(Long petId) {
        // 펫 존재 확인
        if (!petRepository.existsById(petId)) {
            throw new IllegalArgumentException("존재하지 않는 펫입니다.");
        }

        return fileService.getFilesByTypeAndRelatedId(FileType.PET_ALBUM, petId);
    }

    @Override
    public void deletePhoto(Long petId, Long fileId) {
        // 펫 존재 확인
        if (!petRepository.existsById(petId)) {
            throw new IllegalArgumentException("존재하지 않는 펫입니다.");
        }

        // 파일 소유권 확인 (해당 펫의 앨범 사진인지 확인)
        FileResponseDto file = fileService.getFileById(fileId);
        if (!file.getRelatedId().equals(petId) || file.getFileType() != FileType.PET_ALBUM) {
            throw new IllegalArgumentException("해당 펫의 앨범 사진이 아닙니다.");
        }

        fileService.deleteFile(fileId);
        log.info("펫 앨범 사진 삭제: petId={}, fileId={}", petId, fileId);
    }

    @Override
    public void deleteAllPhotos(Long petId) {
        // 펫 존재 확인
        if (!petRepository.existsById(petId)) {
            throw new IllegalArgumentException("존재하지 않는 펫입니다.");
        }

        fileService.deleteFilesByTypeAndRelatedId(FileType.PET_ALBUM, petId);
        log.info("펫 앨범 전체 삭제: petId={}", petId);
    }

    @Override
    @Transactional(readOnly = true)
    public PetAlbumResponseDto.AlbumStats getAlbumStats(Long petId) {
        // 펫 존재 확인
        if (!petRepository.existsById(petId)) {
            throw new IllegalArgumentException("존재하지 않는 펫입니다.");
        }

        List<FileResponseDto> photos = fileService.getFilesByTypeAndRelatedId(FileType.PET_ALBUM, petId);

        return PetAlbumResponseDto.AlbumStats.builder()
                .totalPhotos(photos.size())
                .totalFileSize(photos.stream().mapToLong(FileResponseDto::getFileSize).sum())
                .build();
    }


    @Override
    public CustomAlbumResponseDto createCustomAlbum(CustomAlbumCreateRequestDto dto, UserAccount creator) {
        CustomAlbum album = CustomAlbum.builder()
                .name(dto.getName())
                .description(dto.getDescription())
                .creator(creator)
                .build();

        CustomAlbum savedAlbum = customAlbumRepository.save(album);
        log.info("커스텀 앨범 생성: albumId={}, creatorId={}", savedAlbum.getId(), creator.getId());

        return CustomAlbumResponseDto.from(savedAlbum);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CustomAlbumResponseDto> getMyCustomAlbums(UserAccount user) {
        List<CustomAlbum> albums = customAlbumRepository.findByCreatorOrderByCreatedAtDesc(user);
        return albums.stream()
                .map(CustomAlbumResponseDto::from)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public CustomAlbumResponseDto getCustomAlbum(Long albumId) {
        CustomAlbum album = customAlbumRepository.findByIdWithPhotos(albumId)
                .orElseThrow(() -> new IllegalArgumentException("앨범을 찾을 수 없습니다."));

        return CustomAlbumResponseDto.from(album);
    }

    @Override
    public CustomAlbumResponseDto updateCustomAlbum(Long albumId, CustomAlbumCreateRequestDto dto, UserAccount user) {
        return null;
    }

    @Override
    public void deleteCustomAlbum(Long albumId, UserAccount user) {
        // 1. 앨범 존재 및 소유권 확인
        CustomAlbum album = customAlbumRepository.findById(albumId)
                .orElseThrow(() -> new IllegalArgumentException("앨범을 찾을 수 없습니다."));

        if (!album.getCreator().getId().equals(user.getId())) {
            throw new IllegalArgumentException("본인 앨범만 삭제할 수 있습니다.");
        }

        // 2. 앨범에 포함된 모든 사진 매핑 삭제 (원본 파일은 유지)
        List<CustomAlbumPhoto> albumPhotos = customAlbumPhotoRepository.findByCustomAlbum(album);
        if (!albumPhotos.isEmpty()) {
            customAlbumPhotoRepository.deleteAll(albumPhotos);
            log.info("커스텀 앨범 사진 매핑 삭제: albumId={}, photoCount={}", albumId, albumPhotos.size());
        }

        // 3. 앨범 자체 삭제
        customAlbumRepository.delete(album);

        log.info("커스텀 앨범 삭제 완료: albumId={}, creatorId={}", albumId, user.getId());
    }

    @Override
    public List<CustomAlbumResponseDto.CustomAlbumPhotoDto> addPhotosToCustomAlbum(
            Long albumId, List<MultipartFile> files, List<Long> petIds,
            List<String> captions, UserAccount user) {

        // 앨범 존재 및 권한 확인
        CustomAlbum album = customAlbumRepository.findById(albumId)
                .orElseThrow(() -> new IllegalArgumentException("앨범을 찾을 수 없습니다."));

        if (!album.getCreator().getId().equals(user.getId())) {
            throw new IllegalArgumentException("본인 앨범에만 사진을 추가할 수 있습니다.");
        }

        // 파일 개수와 펫 ID 개수 일치 확인
        if (files.size() != petIds.size()) {
            throw new IllegalArgumentException("파일 개수와 펫 ID 개수가 일치하지 않습니다.");
        }

        List<CustomAlbumResponseDto.CustomAlbumPhotoDto> result = new ArrayList<>();

        for (int i = 0; i < files.size(); i++) {
            MultipartFile file = files.get(i);
            Long petId = petIds.get(i);
            String caption = (captions != null && i < captions.size()) ? captions.get(i) : null;

            // 펫 존재 확인
            Pet pet = petRepository.findById(petId)
                    .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 펫입니다: " + petId));

            // 파일 업로드 (기존 FileService 활용)
            FileResponseDto uploadedFile = fileService.uploadFile(file, FileType.CUSTOM_ALBUM, petId);

            // File 엔터티 조회
            File fileEntity = fileRepository.findById(uploadedFile.getId())
                    .orElseThrow(() -> new RuntimeException("업로드된 파일을 찾을 수 없습니다."));

            // CustomAlbumPhoto 생성
            CustomAlbumPhoto albumPhoto = CustomAlbumPhoto.builder()
                    .customAlbum(album)
                    .pet(pet)
                    .file(fileEntity)
                    .caption(caption)
                    .displayOrder(i)
                    .build();

            CustomAlbumPhoto savedPhoto = customAlbumPhotoRepository.save(albumPhoto);

            CustomAlbumResponseDto.CustomAlbumPhotoDto photoDto =
                    CustomAlbumResponseDto.CustomAlbumPhotoDto.builder()
                            .id(savedPhoto.getId())
                            .petId(pet.getPetId())
                            .petName(pet.getPetName())
                            .file(uploadedFile)
                            .caption(caption)
                            .displayOrder(i)
                            .addedAt(savedPhoto.getAddedAt())
                            .build();

            result.add(photoDto);
        }

        log.info("커스텀 앨범 사진 추가: albumId={}, photoCount={}", albumId, files.size());
        return result;
    }

    @Override
    public void removePhotoFromCustomAlbum(Long albumId, Long photoId, UserAccount user) {
        validateCustomAlbumOwner(albumId, user);

        CustomAlbumPhoto photo = customAlbumPhotoRepository.findById(photoId)
                .orElseThrow(() -> new IllegalArgumentException("사진을 찾을 수 없습니다."));

        if (!photo.getCustomAlbum().getId().equals(albumId)) {
            throw new IllegalArgumentException("해당 앨범의 사진이 아닙니다.");
        }

        // 매핑만 삭제 (원본 파일은 유지)
        customAlbumPhotoRepository.delete(photo);
        log.info("커스텀 앨범 사진 제거: albumId={}, photoId={}", albumId, photoId);
    }

    @Override
    public void updatePhotoCaption(Long albumId, Long photoId, String caption, UserAccount user) {

    }

    @Override
    public void updatePhotoOrder(Long albumId, List<Long> photoIds, UserAccount user) {

    }

    @Override
    public void validateCustomAlbumOwner(Long albumId, UserAccount user) {
        if (!customAlbumRepository.existsByIdAndCreator(albumId, user)) {
            throw new IllegalArgumentException("본인 앨범만 관리할 수 있습니다.");
        }
    }
}
