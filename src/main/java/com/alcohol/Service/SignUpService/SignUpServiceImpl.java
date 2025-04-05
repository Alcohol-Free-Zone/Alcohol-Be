package com.alcohol.Service.SignUpService;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.alcohol.Dao.signup.SignUpDao;
import com.alcohol.entity.UserAccount;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SignUpServiceImpl implements SignUpService {

    private final PasswordEncoder passwordEncoder;
    private final SignUpDao signUpDao;

    public void signup(UserAccount userAccount) {

        String encodedPassword = passwordEncoder.encode(userAccount.getPassword());
        userAccount.setPassword(encodedPassword);

        signUpDao.signup(userAccount);
    }
    
}
