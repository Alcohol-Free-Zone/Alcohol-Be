package com.alcohol.application.travel.entitiy;


import java.sql.Timestamp;
import java.util.List;

import com.alcohol.application.travel.dto.PostCreateRequest;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    // 게시글 ID
    private Long postId;

    private Long planId;

    private String contentId;

    private String planName;

    private List<Long> imageIds;

    private List<Long> petIds;

    private int rating;

    private String isOpen;

    private Timestamp createdAt;

    private String isPetYn;

    private Long userId;

    public static Post fromRequest(PostCreateRequest request) {
        Post post = new Post();
        post.setPlanId(request.getPlanId());
        post.setContentId(request.getContentId());
        post.setPlanName(request.getPlanName());
        post.setImageIds(request.getImageIds());
        post.setPetIds(request.getPetIds());
        post.setRating(request.getRating());
        post.setIsOpen(request.getIsOpen());
        post.setIsPetYn(request.getIsPetYn());
        post.setCreatedAt(request.getCreatedAt());
        return post;
    }

    public void updateFromRequest(PostCreateRequest request) {
        this.planId = request.getPlanId();
        this.contentId = request.getContentId();
        this.planName = request.getPlanName();
        this.imageIds = request.getImageIds();
        this.petIds = request.getPetIds();
        this.rating = request.getRating();
        this.isOpen = request.getIsOpen();
        this.isPetYn = request.getIsPetYn();
        this.createdAt = request.getCreatedAt();
    }


}
