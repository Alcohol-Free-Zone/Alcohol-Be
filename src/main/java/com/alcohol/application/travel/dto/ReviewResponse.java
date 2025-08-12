package com.alcohol.application.travel.dto;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ReviewResponse {
    private Long postId;
    private LocalDateTime createdAt;
    private Integer rating;
    private Long imgId;
    private String nickname;

    public ReviewResponse(Long postId, LocalDateTime createdAt, Integer rating, Long imgId, String nickname) {
        this.postId = postId;
        this.createdAt = createdAt;
        this.rating = rating;
        this.imgId = imgId;
        this.nickname = nickname;
    }
}
