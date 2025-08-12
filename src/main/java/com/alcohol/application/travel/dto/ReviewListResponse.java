package com.alcohol.application.travel.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ReviewListResponse {
    private Long postId;
    private String contentId;
    private String petName;
    private Long petImg;
    private Long postImg;

    public ReviewListResponse(Long postId, String contentId, String petName, Long petImg, Long postImg) {
        this.postId = postId;
        this.contentId = contentId;
        this.petName = petName;
        this.petImg = petImg;
        this.postImg = postImg;
    }
}
