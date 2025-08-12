package com.alcohol.application.travel.service;

import java.sql.Timestamp;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.alcohol.application.pet.entity.Pet;
import com.alcohol.application.pet.repository.PetRepository;
import com.alcohol.application.petFollow.repository.PetFollowRepository;
import com.alcohol.application.travel.dto.FavoriteCreateResponse;
import com.alcohol.application.travel.dto.PostCreateRequest;
import com.alcohol.application.travel.dto.ReviewListResponse;
import com.alcohol.application.travel.dto.ReviewResponse;
import com.alcohol.application.travel.entitiy.Favorite;
import com.alcohol.application.travel.entitiy.Post;
import com.alcohol.application.travel.repository.FavoriteRepository;
import com.alcohol.application.travel.repository.TravelRepository;
import com.alcohol.common.files.entity.File;
import com.alcohol.common.files.repository.FileRepository;
import com.alcohol.util.pagination.PageResponseDto;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class TravelServiceImpl implements TravelService {

    private final TravelRepository travelRepository;
    private final FavoriteRepository favoriteRepository;
    private final PetFollowRepository petFollowRepository;
    private final FileRepository fileRepository;
    private final PetRepository petRepository;

    public void createPost(PostCreateRequest request, Long userId) {
        Post post;

        if (request.getPostId() != null) {
            // ===== 수정 로직 =====
            post = travelRepository.findById(request.getPostId())
                    .orElseThrow(() -> new EntityNotFoundException("해당 게시글이 존재하지 않습니다."));

            if (!post.getUserId().equals(userId)) {
                throw new SecurityException("본인 게시글만 수정할 수 있습니다.");
            }

            // 기본 필드 업데이트
            post.updateFromRequest(request);

            // images 업데이트
            post.getImages().clear();
            if (request.getImages() != null && !request.getImages().isEmpty()) {
                List<File> files = fileRepository.findAllById(request.getImages());
                post.getImages().addAll(files);
            }

            // pets 업데이트
            post.getPets().clear();
            if (request.getPets() != null && !request.getPets().isEmpty()) {
                List<Pet> pets = petRepository.findAllById(request.getPets());
                post.getPets().addAll(pets);
            }

        } else {
            // ===== 생성 로직 =====
            post = Post.fromRequest(request);
            post.setUserId(userId);

            if (request.getImages() != null && !request.getImages().isEmpty()) {
                List<File> files = fileRepository.findAllById(request.getImages());
                post.setImages(files);
            }

            if (request.getPets() != null && !request.getPets().isEmpty()) {
                List<Pet> pets = petRepository.findAllById(request.getPets());
                post.setPets(pets);
            }
        }

        travelRepository.save(post);
    }

    public Long createFavorite(String contentId, Long userId) {
        Favorite favorite = new Favorite();
        favorite.setContentId(contentId);
        favorite.setUserId(userId);
        favoriteRepository.save(favorite);
        return favorite.getFavoriteId();
    }

    public List<FavoriteCreateResponse> getFavorites(Long id) {
        List<Favorite> favorites = favoriteRepository.findByUserId(id);
        return favorites.stream()
                .map(favorite -> FavoriteCreateResponse.builder()
                        .contentId(favorite.getContentId())
                        .userId(favorite.getUserId())
                        .favoriteId(favorite.getFavoriteId())
                        .build())
                .toList();
    }

    public PageResponseDto<ReviewListResponse> getPosts(Long userId, Pageable pageable, List<String> contentIds) {

        // 1. 친구 목록 pet_id 배열 형태로 준비
        List<Long> petIds = petFollowRepository.findPetIdsByFollower_id(userId);

        // 2. 쿼리 실행
        Page<Object[]> page = travelRepository.findAllByIsOpenOrFriendsPrivate(petIds, contentIds, userId, pageable);

        // 3. DTO 변환
        List<ReviewListResponse> content = page.stream()
            .map(row -> new ReviewListResponse(
                ((Number) row[0]).longValue(),
                (String) row[1],
                (String) row[2],
                row[3] != null ? ((Number) row[3]).longValue() : null,
                row[4] != null ? ((Number) row[4]).longValue() : null
            ))
            .collect(Collectors.toList());

        return new PageResponseDto<>(
            content,
            page.hasNext(),
            page.getTotalElements(),
            page.getNumber(),
            page.getSize()
        );
    }

    public List<ReviewResponse> getPost(Long postId) {
        List<Object[]> results = travelRepository.getPostNative(postId);

        return results.stream()
            .map(row -> new ReviewResponse(
                ((Number) row[0]).longValue(),
                row[1] == null ? null : ((Timestamp) row[1]).toLocalDateTime(),
                row[2] == null ? null : ((Number) row[2]).intValue(),
                row[3] == null ? null : ((Number) row[3]).longValue(),
                (String) row[4]
            ))
            .collect(Collectors.toList());
    }

    public void deletePost(Long postId) {
        Post post = travelRepository.findById(postId)
        .orElseThrow(() -> new EntityNotFoundException("게시글이 없습니다"));

        post.setIsDelete("Y");
    }

}
