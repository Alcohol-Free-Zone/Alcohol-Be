package com.alcohol.application.travel.service;

import java.util.List;

import org.springframework.data.domain.Pageable;

import com.alcohol.application.travel.dto.FavoriteCreateResponse;
import com.alcohol.application.travel.dto.PostCreateRequest;
import com.alcohol.application.travel.dto.ReviewListResponse;
import com.alcohol.application.travel.dto.ReviewResponse;
import com.alcohol.util.pagination.PageResponseDto;

public interface TravelService {

    void createPost(PostCreateRequest request, Long userId);

    Long createFavorite(String contentId, Long id);

    List<FavoriteCreateResponse> getFavorites(Long id);

    PageResponseDto<ReviewListResponse> getPosts(Long userId, Pageable pageable, List<String> contentIds);

    List<ReviewResponse> getPost(Long postId);

}
