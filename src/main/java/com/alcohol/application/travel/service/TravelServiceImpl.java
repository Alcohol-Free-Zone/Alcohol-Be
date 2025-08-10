package com.alcohol.application.travel.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.alcohol.application.travel.dto.FavoriteCreateResponse;
import com.alcohol.application.travel.dto.PostCreateRequest;
import com.alcohol.application.travel.entitiy.Favorite;
import com.alcohol.application.travel.entitiy.Post;
import com.alcohol.application.travel.repository.FavoriteRepository;
import com.alcohol.application.travel.repository.TravelRepository;

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

    
}
