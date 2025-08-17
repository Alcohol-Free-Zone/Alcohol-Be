package com.alcohol.application.travel.service;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.alcohol.application.pet.entity.Pet;
import com.alcohol.application.pet.repository.PetRepository;
import com.alcohol.application.petFollow.repository.PetFollowRepository;
import com.alcohol.application.travel.dto.AroundListResponse;
import com.alcohol.application.travel.dto.AroundProjection;
import com.alcohol.application.travel.dto.FavoriteToggleResult;
import com.alcohol.application.travel.dto.PetAllowResponse;
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

    public FavoriteToggleResult toggleFavorite(String contentId, Long userId) {
        Optional<Favorite> existingFavorite = favoriteRepository.findByContentIdAndUserId(contentId, userId);

        if (existingFavorite.isPresent()) {
            favoriteRepository.delete(existingFavorite.get());
            return new FavoriteToggleResult(null, "삭제되었습니다");
        } else {
            Favorite favorite = new Favorite();
            favorite.setContentId(contentId);
            favorite.setUserId(userId);
            favoriteRepository.save(favorite);
            return new FavoriteToggleResult(favorite.getFavoriteId(), "추가되었습니다");
        }
    }

    public List<String> getFavorites(Long id) {
        List<String> favorites = favoriteRepository.getFavorites(id);
        return favorites;
    }

    public PageResponseDto<ReviewListResponse> getPosts(Long userId, Pageable pageable, List<String> contentIds) {

        // 1. 친구 목록 pet_id 배열 형태로 준비
        List<Long> petIds = petFollowRepository.findPetIdsByFollower_id(userId);

        // 1.1 기본적인 나의 팻 아이디 세팅
        List<Long> myPetIds = travelRepository.findPetIdsByMyPet_id(userId);

        petIds.addAll(myPetIds);

        // 2. 쿼리 실행
        Page<Object[]> page = travelRepository.findAllByIsOpenOrFriendsPrivate(petIds, contentIds, userId, pageable);

        // 3. DTO 변환
        List<ReviewListResponse> content = page.stream()
            .map(row -> new ReviewListResponse(
                ((Number) row[0]).longValue(),
                (String) row[1],
                (String) row[2],
                row[3] != null ? ((Number) row[3]).longValue() : null,
                row[4] != null ? ((Number) row[4]).longValue() : null,
                (String) row[5],
                (String) row[6],
                (String) row[7],
                (Timestamp) row[8],
                ((Number) row[9]).intValue()
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

    public PageResponseDto<AroundListResponse> getArounds(Pageable pageable, List<String> contentIds) {
        
        Page<Object[]> page = travelRepository.getArounds(contentIds, pageable);

        List<AroundListResponse> content = page.stream()
            .map(row -> AroundListResponse.from(row))
            .collect(Collectors.toList());

        return new PageResponseDto<>(
            content,
            page.hasNext(),
            page.getTotalElements(),
            page.getNumber(),
            page.getSize()
        );
    }

    public AroundProjection getAround(String contentId) {
        return travelRepository.getAround(contentId);
    }

    public List<PetAllowResponse> getPetAllowed(List<String> contentIds) {
    List<Object[]> results = travelRepository.findRecentPostsByContentIds(contentIds);

    // DB 결과를 contentId별로 그룹화
    Map<String, List<String>> groupedByContent = results.stream()
        .collect(Collectors.groupingBy(
            row -> (String) row[0],
            Collectors.mapping(row -> (String) row[1], Collectors.toList())
        ));

    // DB에 있는 contentId만 응답
    return groupedByContent.entrySet().stream()
        .map(entry -> {
            String contentId = entry.getKey();
            List<String> top10 = entry.getValue().stream().limit(10).toList();
            long yCount = top10.stream().filter("Y"::equalsIgnoreCase).count();
            String finalYn = top10.isEmpty() ? "N" : (yCount * 2 >= top10.size() ? "Y" : "N");
            return new PetAllowResponse(contentId, finalYn);
        })
        .toList();
}
    
}
