package com.alcohol.application.userAccount.service;

import com.alcohol.application.userAccount.dto.UpdateUserRequestDto;
import com.alcohol.application.userAccount.entity.UserAccount;

import java.util.List;

public interface UserAccountService {

//    void signup(UserAccount userAccount);


    // 사용자 조회
    UserAccount findById(Long id);

    // 사용자 존재 여부 확인
    boolean existsById(Long id);

    // 소셜 로그인 사용자 조회 또는 생성
    UserAccount findOrCreateOAuthUser(String provider, String providerId,
                                      String email, String nickname, String profileImage);

    // 사용자 정보 업데이트
    UserAccount updateUser(Long userId, UpdateUserRequestDto updateRequest);

    // 사용자 삭제
    void deleteUser(Long userId);

    // 프로바이더별 사용자 조회
    List<UserAccount> findUsersByProvider(String provider);

    // 전체 사용자 수 조회
    long getUserCount();

}
