package com.alcohol.application.userAccount.Controller;

import com.alcohol.application.userAccount.dto.UpdateUserRequestDto;
import com.alcohol.application.userAccount.dto.UserAccountDto;
import com.alcohol.application.userAccount.dto.UserAccountResponseDto;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import com.alcohol.application.userAccount.service.UserAccountService;
import com.alcohol.application.userAccount.entity.UserAccount;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user")
public class UserAccountController {
    private final UserAccountService userAccountService;

    // 사용자 정보 조회
    @GetMapping("/{id}")
    public ResponseEntity<UserAccountResponseDto> getCurrentUser(
            @AuthenticationPrincipal UserAccount currentUser,
            @PathVariable("id") Long targetUserId) {

        UserAccount userAccount = userAccountService.authFindById(currentUser, targetUserId);
        // Entity를 DTO로 변환
        UserAccountResponseDto response = UserAccountResponseDto.from(userAccount);

        return ResponseEntity.ok(response);
    }

    // 사용자 정보 수정
    @PutMapping("/{id}")
    public ResponseEntity<String> updateUser(
            @AuthenticationPrincipal UserAccount currentUser,
            @PathVariable("id") Long targetUserId,
            @RequestBody UpdateUserRequestDto updateRequest) {

        userAccountService.updateUser(currentUser, targetUserId, updateRequest);

        return ResponseEntity.ok("사용자 정보 수정 완료");
    }

    // 회원 탈퇴
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteUser(@AuthenticationPrincipal UserAccount currentUser , @PathVariable("id") Long targetUserId) {

        userAccountService.deleteUser(currentUser, targetUserId);

        return ResponseEntity.ok("계정 비활성화 완료");
    }

}
