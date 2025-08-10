package com.alcohol.application.travel.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ReviewListResponse {
    private Long postId;
    private String contentId;
    private String visitUser;

    public ReviewListResponse(Long postId, String contentId, String visitUser) {
        this.postId = postId;
        this.contentId = contentId;
        this.visitUser = visitUser + "님이 방문하셨습니다.";
    }
}
