package com.alcohol.application.travel.entitiy;


import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.CurrentTimestamp;

import com.alcohol.application.pet.entity.Pet;
import com.alcohol.application.travel.dto.PostCreateRequest;
import com.alcohol.common.files.entity.File;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
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

    private String contentId;

    private String planName;

    // @ManyToMany
    // @JoinTable(
    //     name = "post_files",
    //     joinColumns = @JoinColumn(name = "post_id"),
    //     inverseJoinColumns = @JoinColumn(name = "file_id")
    // )
    private transient List<File> images = new ArrayList<>();
    // private List<File> images = new ArrayList<>();

    @ManyToMany
    @JoinTable(
        name = "post_pets",
        joinColumns = @JoinColumn(name = "post_id"),
        inverseJoinColumns = @JoinColumn(name = "pet_id")
    )
    private List<Pet> pets = new ArrayList<>();

    private int rating;

    private String isOpen;

    @CurrentTimestamp
    private Timestamp createdAt;

    private String isPetYn;

    private Long userId;

    // 삭제 여부
    private String isDelete = "N";

    // 새 Post 생성
    public static Post fromRequest(PostCreateRequest request) {
        Post post = new Post();
        post.setContentId(request.getContentId());
        post.setPlanName(request.getPlanName());
        post.setRating(request.getRating());
        post.setIsOpen(request.getIsOpen());
        post.setIsPetYn(request.getIsPetYn());
        post.setCreatedAt(request.getCreatedAt());
        return post;
    }

    // 기존 Post 수정
    public void updateFromRequest(PostCreateRequest request) {
        this.setContentId(request.getContentId());
        this.setPlanName(request.getPlanName());
        this.setRating(request.getRating());
        this.setIsOpen(request.getIsOpen());
        this.setIsPetYn(request.getIsPetYn());
        this.setCreatedAt(request.getCreatedAt());
        // images, pets는 서비스 계층에서 따로 처리 (ID → 엔티티 변환)
    }
}
