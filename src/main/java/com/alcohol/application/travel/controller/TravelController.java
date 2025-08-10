package com.alcohol.application.travel.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alcohol.application.travel.dto.FavoriteCreateResponse;
import com.alcohol.application.travel.dto.PostCreateRequest;
import com.alcohol.application.travel.service.TravelService;
import com.alcohol.application.userAccount.entity.UserAccount;

import lombok.RequiredArgsConstructor;



@RestController
@RequiredArgsConstructor
@RequestMapping("/api/travel")
public class TravelController {

    private final TravelService travelService;

    // 게시글(리뷰) 생성/수정 임시 폼
    @PostMapping("/post")
    public ResponseEntity<String> createPost(
        @RequestBody PostCreateRequest request,
        @AuthenticationPrincipal UserAccount currentUser
    ) {
        // 게시글 생성 로직 구현
        travelService.createPost(request, currentUser.getId());
        return ResponseEntity.ok("게시물 생성/변경 완료");
    }

    // 관심목록 추가
    @PostMapping("/favorite/{contentId}")
    public ResponseEntity<FavoriteCreateResponse> createFavorite(
        @PathVariable String contentId,
        @AuthenticationPrincipal UserAccount currentUser
    ) {
        Long favoriteId = travelService.createFavorite(contentId, currentUser.getId());

        FavoriteCreateResponse response = FavoriteCreateResponse.builder()
            .contentId(contentId)
            .userId(currentUser.getId())
            .favoriteId(favoriteId)
            .build();

        return ResponseEntity.ok(response);
    }
    
    // 관심목록 조회
    @GetMapping("/favorite")
    public ResponseEntity<List<FavoriteCreateResponse>> getFavorites(@AuthenticationPrincipal UserAccount currentUser) {
        List<FavoriteCreateResponse> favorites = travelService.getFavorites(currentUser.getId());
        return ResponseEntity.ok(favorites);
    }
    
    
}
