package com.alcohol.Dao.signup;

import org.apache.ibatis.annotations.Mapper;

import com.alcohol.entity.UserAccount;

@Mapper
public interface SignUpDao {

    void signup(UserAccount userAccount);
    
}
