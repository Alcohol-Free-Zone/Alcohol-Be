package com.alcohol.Controller.SignUp;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.alcohol.Service.SignUpService.SignUpService;
import com.alcohol.entity.UserAccount;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class SignUpController {

    private final SignUpService signUpService;

    @PostMapping("/signup")
    public ResponseEntity<String> signup(@RequestBody UserAccount userAccount) {
        signUpService.signup(userAccount);
        return ResponseEntity.status(HttpStatus.CREATED).body("회원가입 성공");
}
    
}
