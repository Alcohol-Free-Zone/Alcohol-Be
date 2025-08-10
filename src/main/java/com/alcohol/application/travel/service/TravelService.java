package com.alcohol.application.travel.service;

import java.util.List;

import com.alcohol.application.travel.dto.FavoriteCreateResponse;
import com.alcohol.application.travel.dto.PostCreateRequest;

public interface TravelService {

    void createPost(PostCreateRequest request, Long userId);

    Long createFavorite(String contentId, Long id);

    List<FavoriteCreateResponse> getFavorites(Long id);

}
