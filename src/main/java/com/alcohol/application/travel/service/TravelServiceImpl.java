package com.alcohol.application.travel.service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.alcohol.application.travel.dto.FavoriteCreateResponse;
import com.alcohol.application.travel.dto.PostCreateRequest;
import com.alcohol.application.travel.dto.ReviewListResponse;
import com.alcohol.application.travel.entitiy.Favorite;
import com.alcohol.application.travel.entitiy.Post;
import com.alcohol.application.travel.repository.FavoriteRepository;
import com.alcohol.application.travel.repository.TravelRepository;
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

    public void createPost(PostCreateRequest request, Long userId) {
        Post post;

        if (request.getPostId() != null) {
            // 수정 로직
            post = travelRepository.findById(request.getPostId())
                    .orElseThrow(() -> new EntityNotFoundException("해당 게시글이 존재하지 않습니다."));

            // 작성자 본인인지 검증
            if (!post.getUserId().equals(userId)) {
                throw new SecurityException("본인 게시글만 수정할 수 있습니다.");
            }

            // 수정할 필드 업데이트
            post.updateFromRequest(request);

        } else {
            // 생성 로직
            post = Post.fromRequest(request);
            post.setUserId(userId);
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

    public PageResponseDto<ReviewListResponse> getPosts(Long userId, Pageable pageable) {

        // 1. 로그인 유저의 친구 ID 리스트 조회
        List<Long> friendIds = List.of(1L,2L,3L);

        // 2. 본인 포함 친구 전체 리스트
        List<Long> targetUserIds = new ArrayList<>(friendIds);
        targetUserIds.add(userId);

        // 3. 해당 사용자들(본인 + 친구들)의 리뷰 목록 페이징 조회
        Page<Object[]> page = travelRepository.findReviewsWithLatestVisitUser(targetUserIds, pageable);

        // 4. 페이지 결과를 DTO로 변환
        List<ReviewListResponse> content = page.stream().map(record -> {
            Post review = (Post) record[0];
            String visitUserName = (String) record[1];
            return new ReviewListResponse(review.getPostId(), review.getContentId(), visitUserName);
        }).collect(Collectors.toList());

        return new PageResponseDto<>(content, page.hasNext(), page.getTotalElements(), page.getNumber(), page.getSize());
    }

    
}
