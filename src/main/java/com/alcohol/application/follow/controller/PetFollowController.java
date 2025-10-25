package com.alcohol.application.follow.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.alcohol.application.follow.service.PetFollowService;
import com.alcohol.application.pet.dto.PetResponseDto;
import com.alcohol.application.userAccount.entity.UserAccount;
import com.alcohol.util.pagination.PageRequestDto;
import com.alcohol.util.pagination.PageResponseDto;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class PetFollowController {

    private final PetFollowService petFollowService;

    @GetMapping("/pet-follow/my")
    public ResponseEntity<PageResponseDto<PetResponseDto>> getMyFollowers(
        PageRequestDto pageRequestDto,
        @AuthenticationPrincipal UserAccount currentUser
        ) {
        Long userId = currentUser.getId();
        PageResponseDto<PetResponseDto> response = petFollowService.getMyFollowers(userId, pageRequestDto);
        return ResponseEntity.ok(response);
    }
}
