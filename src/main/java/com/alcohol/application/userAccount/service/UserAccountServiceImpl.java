package com.alcohol.application.userAccount.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.alcohol.application.userAccount.dto.UserAccountDto;
import com.alcohol.application.userAccount.entity.UserAccount;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserAccountServiceImpl implements UserAccountService {

    private final PasswordEncoder passwordEncoder;
    private final UserAccountDto UserAccountDto;

    public void signup(UserAccount userAccount) {

//        String encodedPassword = passwordEncoder.encode(userAccount.getPassword());
//        userAccount.setPassword(encodedPassword);

        UserAccountDto.signup(userAccount);
    }
    
}
