package com.alcohol.application.userAccount.service;

import com.alcohol.application.userAccount.dto.UpdateUserRequestDto;
import com.alcohol.application.userAccount.entity.UserAccount;

import java.util.List;

public interface UserAccountService {

//    void signup(UserAccount userAccount);


    // 사용자 조회
    UserAccount authFindById(UserAccount currentUser, Long targetUserId);

    // 사용자 조회
    UserAccount findById(Long targetUserId);

    // 사용자 존재 여부 확인
    boolean existsById(Long id);


    // 사용자 정보 업데이트
    UserAccount updateUser(UserAccount currentUser, Long targetUserId, UpdateUserRequestDto updateRequest);

    // 사용자 삭제
    void deleteUser(UserAccount currentUser, Long targetUserId);

    // 프로바이더별 사용자 조회
    List<UserAccount> findUsersByProvider(String provider);

    // 전체 사용자 수 조회
    long getUserCount();

}
