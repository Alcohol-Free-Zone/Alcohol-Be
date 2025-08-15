package com.alcohol.application.travel.service;

import java.util.List;

import org.springframework.data.domain.Pageable;

import com.alcohol.application.travel.dto.AroundListResponse;
import com.alcohol.application.travel.dto.AroundProjection;
import com.alcohol.application.travel.dto.FavoriteToggleResult;
import com.alcohol.application.travel.dto.PetAllowResponse;
import com.alcohol.application.travel.dto.PostCreateRequest;
import com.alcohol.application.travel.dto.ReviewListResponse;
import com.alcohol.application.travel.dto.ReviewResponse;
import com.alcohol.util.pagination.PageResponseDto;

public interface TravelService {

    void createPost(PostCreateRequest request, Long userId);

    List<String> getFavorites(Long id);

    PageResponseDto<ReviewListResponse> getPosts(Long userId, Pageable pageable, List<String> contentIds);

    List<ReviewResponse> getPost(Long postId);

    void deletePost(Long postId);

    PageResponseDto<AroundListResponse> getArounds(Pageable pageable, List<String> contentIds);

    AroundProjection getAround(String contentId);

    FavoriteToggleResult toggleFavorite(String contentId, Long id);

    List<PetAllowResponse> getPetAllowed(List<String> contentIds);

}
