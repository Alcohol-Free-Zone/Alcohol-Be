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

//    @PostMapping("/signup")
//    public ResponseEntity<String> signup(@RequestBody UserAccount userAccount) {
//        signUpService.signup(userAccount);
//        return ResponseEntity.status(HttpStatus.CREATED).body("회원가입 성공");
//}

//    private final UserAccountDto UserAccountDto;
    private final UserAccountService userAccountService;

    // 현재 로그인한 사용자 정보 조회
    @GetMapping("/me")
    public ResponseEntity<UserAccountResponseDto> getCurrentUser(@AuthenticationPrincipal UserDetails userDetails) {
        String userId = userDetails.getUsername();
        UserAccount userAccount = userAccountService.findById(Long.parseLong(userId));

        // Entity를 DTO로 변환
        UserAccountResponseDto response = UserAccountResponseDto.from(userAccount);

        return ResponseEntity.ok(response);
    }

    // 사용자 정보 수정
    @PutMapping("/me")
    public ResponseEntity<String> updateUser(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody UpdateUserRequestDto updateRequest) {

        String userId = userDetails.getUsername();
        userAccountService.updateUser(Long.parseLong(userId), updateRequest);

        return ResponseEntity.ok("사용자 정보 수정 완료");
    }

    // 회원 탈퇴
    @DeleteMapping("/me")
    public ResponseEntity<String> deleteUser(@AuthenticationPrincipal UserDetails userDetails) {
        String userId = userDetails.getUsername();
        userAccountService.deleteUser(Long.parseLong(userId));

        return ResponseEntity.ok("계정 삭제 완료");
    }

}
