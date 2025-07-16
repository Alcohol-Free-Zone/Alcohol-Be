package com.alcohol.application.petFollow.controller;

import com.alcohol.application.petFollow.service.PetFollowService;
import com.alcohol.application.userAccount.entity.UserAccount;
import com.alcohol.util.pagination.PageRequestDto;
import com.alcohol.util.pagination.PageResponseDto;
import com.alcohol.application.pet.dto.PetResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/pet-follow")
@RequiredArgsConstructor
public class PetFollowController {

    private final PetFollowService petFollowService;

    @PostMapping("/{petId}")
    public ResponseEntity<String> follow(
            @PathVariable Long petId,
            @AuthenticationPrincipal UserAccount currentUser
    ) {
        petFollowService.follow(currentUser.getId(), petId);
        return ResponseEntity.ok("팔로우 완료");
    }

    @DeleteMapping("/{petId}")
    public ResponseEntity<String> unfollow(
            @PathVariable Long petId,
            @AuthenticationPrincipal UserAccount currentUser
    ) {
        petFollowService.unfollow(currentUser.getId(), petId);
        return ResponseEntity.ok("언팔로우 완료");
    }

    @GetMapping("/{petId}/status")
    public ResponseEntity<Boolean> followStatus(
            @PathVariable Long petId,
            @AuthenticationPrincipal UserAccount currentUser
    ) {
        boolean isFollowing = petFollowService.isFollowing(currentUser.getId(), petId);
        return ResponseEntity.ok(isFollowing);
    }

    @GetMapping("/my")
    public ResponseEntity<PageResponseDto<PetResponseDto>> myFollowing(
            PageRequestDto pageRequestDto,
            @AuthenticationPrincipal UserAccount currentUser
    ) {
        Pageable pageable = pageRequestDto.toPageable();
        PageResponseDto<PetResponseDto> response =
                petFollowService.getMyFollowingPets(currentUser.getId(), pageable);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{petId}/count")
    public ResponseEntity<Long> followerCount(@PathVariable Long petId) {
        long cnt = petFollowService.getFollowerCount(petId);
        return ResponseEntity.ok(cnt);
    }
}