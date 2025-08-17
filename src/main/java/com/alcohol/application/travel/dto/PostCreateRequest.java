package com.alcohol.application.travel.dto;


import java.sql.Timestamp;
import java.util.List;

import lombok.Getter;

@Getter
public class PostCreateRequest {
    private Long postId;
    private String contentId;
    private String planName;
    private List<Long> images;
    private List<Long> pets;
    private int rating;
    private String isOpen;
    private String isPetYn;
    private Timestamp createdAt;
    private String addr;

}
