package com.alcohol.application.travel.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alcohol.application.travel.dto.PostCreateRequest;
import com.alcohol.application.travel.entitiy.Post;
import com.alcohol.application.travel.service.TravelService;
import com.alcohol.application.userAccount.entity.UserAccount;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/travel")
public class TravelController {

    private final TravelService travelService;

    @PostMapping("/post")
    // 게시글(리뷰) 생성/수정 임시 폼
    public ResponseEntity<String> createPost(
        @RequestBody PostCreateRequest request,
        @AuthenticationPrincipal UserAccount currentUser
    ) {
        // 게시글 생성 로직 구현
        travelService.createPost(request, currentUser.getId());
        return ResponseEntity.ok("게시물 생성/변경 완료");
    }
    

    
    
}
