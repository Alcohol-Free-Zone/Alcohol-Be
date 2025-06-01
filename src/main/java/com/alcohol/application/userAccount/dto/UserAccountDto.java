package com.alcohol.application.userAccount.dto;

import org.apache.ibatis.annotations.Mapper;

import com.alcohol.application.userAccount.entity.UserAccount;

@Mapper
public interface UserAccountDto {

    void signup(UserAccount userAccount);
    
}
