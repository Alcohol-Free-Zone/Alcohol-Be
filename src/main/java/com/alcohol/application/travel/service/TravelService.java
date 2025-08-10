package com.alcohol.application.travel.service;

import com.alcohol.application.travel.dto.PostCreateRequest;

public interface TravelService {

    void createPost(PostCreateRequest request, Long userId);

}
