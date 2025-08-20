package com.alcohol.application.petFriend.controller;

import com.alcohol.application.pet.dto.PetResponseDto;
import com.alcohol.application.petFriend.dto.PetFriendResponseDto;
import com.alcohol.application.petFriend.dto.PetFriendStatusResponse;
import com.alcohol.application.petFriend.entity.FriendStatus;
import com.alcohol.application.petFriend.service.PetFriendService;
import com.alcohol.application.userAccount.entity.UserAccount;
import com.alcohol.util.pagination.PageRequestDto;
import com.alcohol.util.pagination.PageResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/pet-friends")
@RequiredArgsConstructor
public class PetFriendController {

    private final PetFriendService petFriendService;

    /* 친구 신청 */
    @PostMapping("/request")
    public ResponseEntity<String> request(
            @RequestParam Long myPetId,
            @RequestParam Long targetPetId,
            @AuthenticationPrincipal UserAccount currentUser) {

        petFriendService.sendRequest(myPetId, targetPetId, currentUser.getId());
        return ResponseEntity.ok("친구 신청이 완료되었습니다.");
    }

    /* 수락 */
    @PostMapping("/accept/{requestId}")
    public ResponseEntity<String> accept(
            @PathVariable Long requestId,
            @AuthenticationPrincipal UserAccount currentUser) {

        petFriendService.acceptRequest(requestId, currentUser.getId());
        return ResponseEntity.ok("친구 신청을 수락하였습니다.");
    }

    /* 거절 */
    @PostMapping("/reject/{requestId}")
    public ResponseEntity<String> reject(
            @PathVariable Long requestId,
            @AuthenticationPrincipal UserAccount currentUser) {

        petFriendService.rejectRequest(requestId, currentUser.getId());
        return ResponseEntity.ok("친구 신청을 거절하였습니다.");
    }

    /* 친구 끊기 */
    @DeleteMapping
    public ResponseEntity<String> unfriend(
            @RequestParam Long myPetId,
            @RequestParam Long targetPetId,
            @AuthenticationPrincipal UserAccount currentUser) {

        petFriendService.removeFriend(myPetId, targetPetId, currentUser.getId());
        return ResponseEntity.ok("친구관계가 해제 되었습니다.");
    }

    /* 상태 조회 */
    @GetMapping("/status")
    public ResponseEntity<PetFriendStatusResponse> status(
            @RequestParam Long myPetId,
            @RequestParam Long targetPetId) {

        return ResponseEntity.ok(petFriendService.getFriendStatus(myPetId, targetPetId));
    }

    /* 받은 요청 목록 */
    @GetMapping("/requests/received")
    public ResponseEntity<PageResponseDto<PetFriendResponseDto>> received(
            PageRequestDto pageDto,
            @AuthenticationPrincipal UserAccount currentUser) {

        Pageable pageable = pageDto.toPageable();
        return ResponseEntity.ok(petFriendService.receivedRequests(currentUser.getId(), pageable));
    }

    /* 보낸 요청 목록 */
    @GetMapping("/requests/sent")
    public ResponseEntity<PageResponseDto<PetFriendResponseDto>> sent(
            PageRequestDto pageDto,
            @AuthenticationPrincipal UserAccount currentUser) {

        Pageable pageable = pageDto.toPageable();
        return ResponseEntity.ok(petFriendService.sentRequests(currentUser.getId(), pageable));
    }

    /* 특정 펫의 친구 목록 */
    @GetMapping("/pets/{petId}/friends")
    public ResponseEntity<PageResponseDto<PetResponseDto>> friends(
            @PathVariable Long petId,
            PageRequestDto pageDto) {

        Pageable pageable = pageDto.toPageable();
        return ResponseEntity.ok(petFriendService.petFriends(petId, pageable));
    }
}
