package com.alcohol.application.userAccount.Controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.alcohol.application.userAccount.service.UserAccountService;
import com.alcohol.application.userAccount.entity.UserAccount;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class UserAccountController {

    private final UserAccountService signUpService;

    @PostMapping("/signup")
    public ResponseEntity<String> signup(@RequestBody UserAccount userAccount) {
        signUpService.signup(userAccount);
        return ResponseEntity.status(HttpStatus.CREATED).body("회원가입 성공");
}
    
}
