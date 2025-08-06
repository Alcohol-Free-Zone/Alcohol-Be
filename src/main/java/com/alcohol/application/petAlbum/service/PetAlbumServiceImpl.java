package com.alcohol.application.petAlbum.service;

import com.alcohol.application.pet.entity.Pet;
import com.alcohol.application.pet.repository.PetRepository;
import com.alcohol.application.petAlbum.dto.PetAlbumResponseDto;
import com.alcohol.common.files.dto.FileResponseDto;
import com.alcohol.common.files.entity.FileType;
import com.alcohol.common.files.service.FileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class PetAlbumServiceImpl implements PetAlbumService {

    private final FileService fileService;
    private final PetRepository petRepository;

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
}
