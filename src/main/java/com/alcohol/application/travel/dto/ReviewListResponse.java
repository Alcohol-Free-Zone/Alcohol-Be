package com.alcohol.application.travel.dto;

import java.sql.Timestamp;

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
    private String isOpen;
    private String nickName;
    private String addr;
    private Timestamp createdAt;
    private int rating;

    public ReviewListResponse(Long postId, String contentId, String petName, Long petImg, Long postImg, String isOpen, String nickName, String addr, Timestamp createdAt, int rating) {
        this.postId = postId;
        this.contentId = contentId;
        this.petName = petName;
        this.petImg = petImg;
        this.postImg = postImg;
        this.isOpen = isOpen;
        this.nickName = nickName;
        this.addr = addr;
        this.createdAt = createdAt;
        this.rating = rating;
    }
}
