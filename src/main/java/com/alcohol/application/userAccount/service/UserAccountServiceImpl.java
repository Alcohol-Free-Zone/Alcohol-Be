package com.alcohol.application.userAccount.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alcohol.application.userAccount.entity.UserAccount;
import com.alcohol.application.userAccount.entity.Role;
import com.alcohol.application.userAccount.repository.UserAccountRepository;
import com.alcohol.application.userAccount.dto.UpdateUserRequestDto;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserAccountServiceImpl implements UserAccountService {

    private final UserAccountRepository userAccountRepository;

    @Override
    public UserAccount findById(Long id) {
        return userAccountRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다: " + id));
    }

    @Override
    public boolean existsById(Long id) {
        return userAccountRepository.existsById(id);
    }

    @Override
    @Transactional
    public UserAccount findOrCreateOAuthUser(String provider, String providerId,
                                             String email, String nickname, String profileImage) {

        return userAccountRepository
                .findByProviderAndProviderId(provider, providerId)
                .map(existingUser -> {
                    // 기존 사용자 정보 업데이트
                    existingUser.updateInfo(nickname, profileImage);
                    log.info("기존 {} 사용자 정보 업데이트: {}", provider, email);
                    return userAccountRepository.save(existingUser);
                })
                .orElseGet(() -> {
                    // 신규 사용자 생성
                    UserAccount newUser = UserAccount.builder()
                            .provider(provider)
                            .providerId(providerId)
                            .email(email)
                            .nickname(nickname)
                            .profileImage(profileImage)
                            .role(Role.USER)
                            .build();

                    log.info("새로운 {} 사용자 생성: {}", provider, email);
                    return userAccountRepository.save(newUser);
                });
    }

    @Override
    @Transactional
    public UserAccount updateUser(Long userId, UpdateUserRequestDto updateRequest) {
        UserAccount userAccount = findById(userId);

        userAccount.updateInfo(updateRequest.getNickname(), updateRequest.getProfileImage());

        log.info("사용자 정보 업데이트: {}", userId);
        return userAccountRepository.save(userAccount);
    }

    @Override
    @Transactional
    public void deleteUser(Long userId) {
        UserAccount userAccount = findById(userId);
        userAccountRepository.delete(userAccount);

        log.info("사용자 삭제: {}", userId);
    }

    @Override
    public List<UserAccount> findUsersByProvider(String provider) {
        return userAccountRepository.findByProvider(provider);
    }

    @Override
    public long getUserCount() {
        return userAccountRepository.count();
    }
}
