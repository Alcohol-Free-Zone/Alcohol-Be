package com.alcohol.application.Album.service;

import com.alcohol.application.Album.entity.PetAlbumPhoto;
import com.alcohol.application.Album.repository.PetAlbumPhotoRepository;
import com.alcohol.application.pet.entity.Pet;
import com.alcohol.application.pet.repository.PetRepository;
import com.alcohol.application.Album.dto.CustomAlbumCreateRequestDto;
import com.alcohol.application.Album.dto.CustomAlbumResponseDto;
import com.alcohol.application.Album.dto.AlbumResponseDto;
import com.alcohol.application.Album.entity.CustomAlbum;
import com.alcohol.application.Album.entity.CustomAlbumPhoto;
import com.alcohol.application.Album.repository.CustomAlbumPhotoRepository;
import com.alcohol.application.Album.repository.CustomAlbumRepository;
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
public class AlbumServiceImpl implements AlbumService {

    private final FileService fileService;
    private final PetRepository petRepository;
    private final FileRepository fileRepository;
    private final CustomAlbumPhotoRepository customAlbumPhotoRepository;
    private final CustomAlbumRepository customAlbumRepository;
    private final PetAlbumPhotoRepository petAlbumPhotoRepository;


    // ============ 펫 앨범 관련 ============

    @Override
    public List<FileResponseDto> uploadPhotos(Long petId, List<MultipartFile> files, UserAccount user) {
        // 펫 존재 및 소유권 확인
        Pet pet = petRepository.findByPetIdAndUserAccountId(petId, user.getId())
                .orElseThrow(() -> new IllegalArgumentException("본인의 반려동물이 아닙니다."));

        // 파일 개수 제한
        if (files.size() > 10) {
            throw new IllegalArgumentException("한 번에 최대 10개의 사진만 업로드할 수 있습니다.");
        }

        List<FileResponseDto> uploadedFiles = new ArrayList<>();

        for (MultipartFile file : files) {
            // 1. 사용자 소유 파일로 업로드 (새로운 시그니처)
            FileResponseDto uploadedFile = fileService.uploadFile(file, user.getId(), FileType.IMAGE);

            // 2. File 엔터티 조회
            File fileEntity = fileRepository.findById(uploadedFile.getId())
                    .orElseThrow(() -> new RuntimeException("업로드된 파일을 찾을 수 없습니다."));

            // 3. 펫 앨범 매핑 생성
            PetAlbumPhoto petAlbumPhoto = PetAlbumPhoto.builder()
                    .pet(pet)
                    .file(fileEntity)
                    .displayOrder(uploadedFiles.size())
                    .build();

            petAlbumPhotoRepository.save(petAlbumPhoto);
            uploadedFiles.add(uploadedFile);
        }

        log.info("펫 앨범 사진 업로드 완료: petId={}, userId={}, count={}", petId, user.getId(), files.size());
        return uploadedFiles;
    }

    @Override
    @Transactional(readOnly = true)
    public AlbumResponseDto getPetAlbum(Long petId, UserAccount user) {
        // 펫 존재 및 소유권 확인
        Pet pet = petRepository.findByPetIdAndUserAccountId(petId, user.getId())
                .orElseThrow(() -> new IllegalArgumentException("본인의 반려동물이 아닙니다."));

        // ✅ 매핑 테이블 통해 사진들 조회
        List<PetAlbumPhoto> petAlbumPhotos = petAlbumPhotoRepository.findByPetOrderByDisplayOrderAsc(pet);

        List<FileResponseDto> photos = petAlbumPhotos.stream()
                .map(mapping -> FileResponseDto.from(mapping.getFile()))
                .collect(Collectors.toList());

        // 앨범 사진 변환
        List<AlbumResponseDto.AlbumPhoto> albumPhotos = photos.stream()
                .map(AlbumResponseDto.AlbumPhoto::from)
                .collect(Collectors.toList());

        // 통계 정보
        AlbumResponseDto.AlbumStats stats = AlbumResponseDto.AlbumStats.builder()
                .totalPhotos(photos.size())
                .totalFileSize(photos.stream().mapToLong(FileResponseDto::getFileSize).sum())
                .build();

        return AlbumResponseDto.builder()
                .petId(petId)
                .petName(pet.getPetName())
                .photos(albumPhotos)
                .stats(stats)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public List<FileResponseDto> getAlbumPhotos(Long petId, UserAccount user) {
        // 펫 존재 및 소유권 확인
        Pet pet = petRepository.findByPetIdAndUserAccountId(petId, user.getId())
                .orElseThrow(() -> new IllegalArgumentException("본인의 반려동물이 아닙니다."));

        // ✅ 매핑 테이블 통해 조회
        List<PetAlbumPhoto> petAlbumPhotos = petAlbumPhotoRepository.findByPetOrderByDisplayOrderAsc(pet);

        return petAlbumPhotos.stream()
                .map(mapping -> FileResponseDto.from(mapping.getFile()))
                .collect(Collectors.toList());
    }

    @Override
    public void deletePhoto(Long petId, Long fileId, UserAccount user) {
        // 펫 존재 및 소유권 확인
        Pet pet = petRepository.findByPetIdAndUserAccountId(petId, user.getId())
                .orElseThrow(() -> new IllegalArgumentException("본인의 반려동물이 아닙니다."));

        // 파일 소유권 확인
        File file = fileRepository.findByIdAndUserId(fileId, user.getId())
                .orElseThrow(() -> new IllegalArgumentException("본인의 파일이 아닙니다."));

        // ✅ 펫 앨범 매핑 삭제 (원본 파일은 보존)
        PetAlbumPhoto mapping = petAlbumPhotoRepository.findByPetAndFile(pet, file)
                .orElseThrow(() -> new IllegalArgumentException("해당 펫 앨범에 없는 사진입니다."));

        petAlbumPhotoRepository.delete(mapping);
        log.info("펫 앨범 사진 매핑 삭제: petId={}, fileId={}, userId={}", petId, fileId, user.getId());
    }

    @Override
    public void deleteAllPhotos(Long petId, UserAccount user) {
        // 펫 존재 및 소유권 확인
        Pet pet = petRepository.findByPetIdAndUserAccountId(petId, user.getId())
                .orElseThrow(() -> new IllegalArgumentException("본인의 반려동물이 아닙니다."));

        // ✅ 모든 매핑 삭제 (원본 파일은 보존)
        List<PetAlbumPhoto> allMappings = petAlbumPhotoRepository.findByPet(pet);
        petAlbumPhotoRepository.deleteAll(allMappings);

        log.info("펫 앨범 전체 매핑 삭제 (파일 보존): petId={}, count={}, userId={}",
                petId, allMappings.size(), user.getId());
    }

    @Override
    @Transactional(readOnly = true)
    public AlbumResponseDto.AlbumStats getAlbumStats(Long petId, UserAccount user) {
        // 펫 존재 및 소유권 확인
        Pet pet = petRepository.findByPetIdAndUserAccountId(petId, user.getId())
                .orElseThrow(() -> new IllegalArgumentException("본인의 반려동물이 아닙니다."));

        // ✅ 매핑 테이블 통해 통계 조회
        List<PetAlbumPhoto> petAlbumPhotos = petAlbumPhotoRepository.findByPet(pet);
        List<FileResponseDto> photos = petAlbumPhotos.stream()
                .map(mapping -> FileResponseDto.from(mapping.getFile()))
                .collect(Collectors.toList());

        return AlbumResponseDto.AlbumStats.builder()
                .totalPhotos(photos.size())
                .totalFileSize(photos.stream().mapToLong(FileResponseDto::getFileSize).sum())
                .build();
    }

    // ✅ 기존 파일을 펫 앨범에 추가 (새로운 기능)
    @Override
    public void addExistingFileToPetAlbum(Long petId, Long fileId, UserAccount user) {
        Pet pet = petRepository.findByPetIdAndUserAccountId(petId, user.getId())
                .orElseThrow(() -> new IllegalArgumentException("본인의 반려동물이 아닙니다."));

        File file = fileRepository.findByIdAndUserId(fileId, user.getId())
                .orElseThrow(() -> new IllegalArgumentException("본인의 파일이 아닙니다."));

        // 이미 연결되어 있는지 확인
        if (petAlbumPhotoRepository.existsByPetAndFile(pet, file)) {
            throw new IllegalArgumentException("이미 해당 펫 앨범에 추가된 사진입니다.");
        }

        PetAlbumPhoto petAlbumPhoto = PetAlbumPhoto.builder()
                .pet(pet)
                .file(file)
                .build();

        petAlbumPhotoRepository.save(petAlbumPhoto);
        log.info("기존 파일을 펫 앨범에 추가: petId={}, fileId={}, userId={}", petId, fileId, user.getId());
    }

    // ============ 커스텀 앨범 관련 ============

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
        // 앨범 존재 및 소유권 확인
        CustomAlbum album = customAlbumRepository.findById(albumId)
                .orElseThrow(() -> new IllegalArgumentException("앨범을 찾을 수 없습니다."));

        if (!album.getCreator().getId().equals(user.getId())) {
            throw new IllegalArgumentException("본인 앨범만 수정할 수 있습니다.");
        }

        // 앨범 정보 수정
        album.setName(dto.getName());
        album.setDescription(dto.getDescription());

        CustomAlbum updated = customAlbumRepository.save(album);
        log.info("커스텀 앨범 수정: albumId={}, userId={}", albumId, user.getId());

        return CustomAlbumResponseDto.from(updated);
    }

    @Override
    public void deleteCustomAlbum(Long albumId, UserAccount user) {
        CustomAlbum album = customAlbumRepository.findById(albumId)
                .orElseThrow(() -> new IllegalArgumentException("앨범을 찾을 수 없습니다."));

        if (!album.getCreator().getId().equals(user.getId())) {
            throw new IllegalArgumentException("본인 앨범만 삭제할 수 있습니다.");
        }

        // 앨범에 포함된 모든 사진 매핑 삭제 (원본 파일은 유지)
        List<CustomAlbumPhoto> albumPhotos = customAlbumPhotoRepository.findByCustomAlbum(album);
        if (!albumPhotos.isEmpty()) {
            customAlbumPhotoRepository.deleteAll(albumPhotos);
            log.info("커스텀 앨범 사진 매핑 삭제: albumId={}, photoCount={}", albumId, albumPhotos.size());
        }

        customAlbumRepository.delete(album);
        log.info("커스텀 앨범 삭제 완료: albumId={}, creatorId={}", albumId, user.getId());
    }

    @Override
    public List<CustomAlbumResponseDto.CustomAlbumPhotoDto> addPhotosToCustomAlbum(
            Long albumId, List<MultipartFile> files, List<Long> petIds,
            List<String> captions, UserAccount user) {

        CustomAlbum album = customAlbumRepository.findById(albumId)
                .orElseThrow(() -> new IllegalArgumentException("앨범을 찾을 수 없습니다."));

        if (!album.getCreator().getId().equals(user.getId())) {
            throw new IllegalArgumentException("본인 앨범에만 사진을 추가할 수 있습니다.");
        }

        if (files.size() != petIds.size()) {
            throw new IllegalArgumentException("파일 개수와 펫 ID 개수가 일치하지 않습니다.");
        }

        List<CustomAlbumResponseDto.CustomAlbumPhotoDto> result = new ArrayList<>();

        for (int i = 0; i < files.size(); i++) {
            MultipartFile file = files.get(i);
            Long petId = petIds.get(i);
            String caption = (captions != null && i < captions.size()) ? captions.get(i) : null;

            // 펫 존재 및 소유권 확인
            Pet pet = petRepository.findByPetIdAndUserAccountId(petId, user.getId())
                    .orElseThrow(() -> new IllegalArgumentException("본인의 반려동물이 아닙니다: " + petId));

            // ✅ 1. 사용자 소유 파일로 업로드 (새로운 시그니처)
            FileResponseDto uploadedFile = fileService.uploadFile(file, user.getId(), FileType.IMAGE);

            File fileEntity = fileRepository.findById(uploadedFile.getId())
                    .orElseThrow(() -> new RuntimeException("업로드된 파일을 찾을 수 없습니다."));

            // 2. 커스텀 앨범 매핑 생성
            CustomAlbumPhoto albumPhoto = CustomAlbumPhoto.builder()
                    .customAlbum(album)
                    .pet(pet)
                    .file(fileEntity)
                    .caption(caption)
                    .displayOrder(i)
                    .build();

            customAlbumPhotoRepository.save(albumPhoto);

            // ✅ 3. 동시에 펫 앨범에도 추가 (중복 확인 후)
            if (!petAlbumPhotoRepository.existsByPetAndFile(pet, fileEntity)) {
                PetAlbumPhoto petAlbumPhoto = PetAlbumPhoto.builder()
                        .pet(pet)
                        .file(fileEntity)
                        .build();
                petAlbumPhotoRepository.save(petAlbumPhoto);
            }

            CustomAlbumResponseDto.CustomAlbumPhotoDto photoDto =
                    CustomAlbumResponseDto.CustomAlbumPhotoDto.builder()
                            .id(albumPhoto.getId())
                            .petId(pet.getPetId())
                            .petName(pet.getPetName())
                            .file(uploadedFile)
                            .caption(caption)
                            .displayOrder(i)
                            .addedAt(albumPhoto.getAddedAt())
                            .build();

            result.add(photoDto);
        }

        log.info("커스텀 앨범 사진 추가: albumId={}, photoCount={}, userId={}", albumId, files.size(), user.getId());
        return result;
    }

    // ✅ 기존 파일을 커스텀 앨범에 추가 (새로운 기능)
    @Override
    public CustomAlbumResponseDto.CustomAlbumPhotoDto addExistingFileToCustomAlbum(
            Long albumId, Long fileId, Long petId, String caption, UserAccount user) {

        validateCustomAlbumOwner(albumId, user);

        CustomAlbum album = customAlbumRepository.findById(albumId)
                .orElseThrow(() -> new IllegalArgumentException("앨범을 찾을 수 없습니다."));

        Pet pet = petRepository.findByPetIdAndUserAccountId(petId, user.getId())
                .orElseThrow(() -> new IllegalArgumentException("본인의 반려동물이 아닙니다."));

        File file = fileRepository.findByIdAndUserId(fileId, user.getId())
                .orElseThrow(() -> new IllegalArgumentException("본인의 파일이 아닙니다."));

        // 이미 추가된 파일인지 확인
        if (customAlbumPhotoRepository.existsByCustomAlbumAndFile(album, file)) {
            throw new IllegalArgumentException("이미 앨범에 추가된 사진입니다.");
        }

        CustomAlbumPhoto albumPhoto = CustomAlbumPhoto.builder()
                .customAlbum(album)
                .pet(pet)
                .file(file)
                .caption(caption)
                .build();

        CustomAlbumPhoto saved = customAlbumPhotoRepository.save(albumPhoto);

        return CustomAlbumResponseDto.CustomAlbumPhotoDto.builder()
                .id(saved.getId())
                .petId(pet.getPetId())
                .petName(pet.getPetName())
                .file(FileResponseDto.from(file))
                .caption(caption)
                .addedAt(saved.getAddedAt())
                .build();
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
        validateCustomAlbumOwner(albumId, user);

        CustomAlbumPhoto photo = customAlbumPhotoRepository.findById(photoId)
                .orElseThrow(() -> new IllegalArgumentException("사진을 찾을 수 없습니다."));

        if (!photo.getCustomAlbum().getId().equals(albumId)) {
            throw new IllegalArgumentException("해당 앨범의 사진이 아닙니다.");
        }

        photo.setCaption(caption);
        customAlbumPhotoRepository.save(photo);

        log.info("커스텀 앨범 사진 캡션 수정: albumId={}, photoId={}", albumId, photoId);
    }

    @Override
    public void updatePhotoOrder(Long albumId, List<Long> photoIds, UserAccount user) {
        validateCustomAlbumOwner(albumId, user);

        for (int i = 0; i < photoIds.size(); i++) {
            CustomAlbumPhoto photo = customAlbumPhotoRepository.findById(photoIds.get(i))
                    .orElseThrow(() -> new IllegalArgumentException("사진을 찾을 수 없습니다."));

            photo.setDisplayOrder(i);
            customAlbumPhotoRepository.save(photo);
        }

        log.info("커스텀 앨범 사진 순서 변경: albumId={}, photoCount={}", albumId, photoIds.size());
    }

    @Override
    public void validateCustomAlbumOwner(Long albumId, UserAccount user) {
        if (!customAlbumRepository.existsByIdAndCreator(albumId, user)) {
            throw new IllegalArgumentException("본인 앨범만 관리할 수 있습니다.");
        }
    }
}
