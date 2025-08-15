package com.alcohol.application.travel.controller;

import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.alcohol.application.travel.dto.AroundListResponse;
import com.alcohol.application.travel.dto.AroundProjection;
import com.alcohol.application.travel.dto.FavoriteCreateResponse;
import com.alcohol.application.travel.dto.FavoriteToggleResult;
import com.alcohol.application.travel.dto.PetAllowResponse;
import com.alcohol.application.travel.dto.PostCreateRequest;
import com.alcohol.application.travel.dto.ReviewListResponse;
import com.alcohol.application.travel.dto.ReviewResponse;
import com.alcohol.application.travel.service.TravelService;
import com.alcohol.application.userAccount.entity.UserAccount;
import com.alcohol.util.pagination.PageResponseDto;

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

    @PostMapping("/favorite/{contentId}")
    public ResponseEntity<FavoriteCreateResponse> toggleFavorite(
        @PathVariable String contentId,
        @AuthenticationPrincipal UserAccount currentUser
    ) {
        FavoriteToggleResult result = travelService.toggleFavorite(contentId, currentUser.getId());

        FavoriteCreateResponse response = FavoriteCreateResponse.builder()
            .contentId(contentId)
            .userId(currentUser.getId())
            .favoriteId(result.getFavoriteId()) // 삭제 시 null 가능
            .message(result.getMessage())       // "추가되었습니다" or "삭제되었습니다"
            .build();

        return ResponseEntity.ok(response);
    }
    
    // 관심목록 조회
    @GetMapping("/favorite")
    public ResponseEntity<List<String>> getFavorites(@AuthenticationPrincipal UserAccount currentUser) {
        List<String> favorites = travelService.getFavorites(currentUser.getId());
        return ResponseEntity.ok(favorites);
    }

    // 리뷰 리스트 조회
    @GetMapping
    public ResponseEntity<PageResponseDto<ReviewListResponse>> getPosts(
        @AuthenticationPrincipal UserAccount currentUser,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size,
        @RequestParam(required = false) List<String> contentIds) {
            Pageable pageable = PageRequest.of(page, size);
            PageResponseDto<ReviewListResponse> posts = travelService.getPosts(currentUser.getId(), pageable, contentIds);
        return ResponseEntity.ok(posts);
    }

    // 리뷰 단건 리스트 조회
    @GetMapping("/{postId}")
    public ResponseEntity<List<ReviewResponse>> getPost(@PathVariable Long postId) {
        List<ReviewResponse> responses = travelService.getPost(postId);
        return ResponseEntity.ok(responses);
    }

    // 리뷰 삭제 플래그 설정
    @PatchMapping("/{postId}")
    public ResponseEntity<String> deletePost(@PathVariable Long postId) {
        travelService.deletePost(postId);
        return ResponseEntity.ok("게시물 삭제 완료");
    }


    // 주변정보 리스트 정보 제공
    @GetMapping("/around")
    public ResponseEntity<PageResponseDto<AroundListResponse>> getArounds(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size,
        @RequestParam(required = false) List<String> contentIds) {
            Pageable pageable = PageRequest.of(page, size);
            PageResponseDto<AroundListResponse> posts = travelService.getArounds(pageable, contentIds);
        return ResponseEntity.ok(posts);
    }

    // 주변정보 상세 정보 제공
    @GetMapping("/around/{contentId}")
    public ResponseEntity<AroundProjection> getAround(
        @PathVariable String contentId) {
            AroundProjection getAround = travelService.getAround(contentId);
        return ResponseEntity.ok(getAround);
    }

    // 반려동물 동반 여부 확인
    @GetMapping("/pet-allowed")
    public ResponseEntity<List<PetAllowResponse>> getPetAllowed(
        @RequestParam(required = false) List<String> contentIds) {
            List<PetAllowResponse> posts = travelService.getPetAllowed(contentIds);
        return ResponseEntity.ok(posts);
    }


}
