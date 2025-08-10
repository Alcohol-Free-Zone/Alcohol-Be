package com.alcohol.application.travel.dto;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class FavoriteCreateResponse {
    private Long favoriteId;
    private Long userId;
    private String contentId;

    public FavoriteCreateResponse(Long favoriteId, Long userId, String contentId) {
        this.favoriteId = favoriteId;
        this.userId = userId;
        this.contentId = contentId;
    }
}
