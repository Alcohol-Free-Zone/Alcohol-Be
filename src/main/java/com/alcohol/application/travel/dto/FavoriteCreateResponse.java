package com.alcohol.application.travel.dto;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class FavoriteCreateResponse {
    private Long favoriteId;
    private Long userId;
    private String contentId;
    private String message;

    public FavoriteCreateResponse(Long favoriteId, Long userId, String contentId, String message) {
        this.favoriteId = favoriteId;
        this.userId = userId;
        this.contentId = contentId;
        this.message = message;
    }
}
