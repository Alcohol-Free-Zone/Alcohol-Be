package com.alcohol.application.travel.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class FavoriteToggleResult {
    private Long favoriteId;
    private String message;
}
