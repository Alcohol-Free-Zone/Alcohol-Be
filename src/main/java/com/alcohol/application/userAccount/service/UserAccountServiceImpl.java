package com.alcohol.application.userAccount.service;

import java.util.List;

import com.alcohol.application.auth.service.PermissionService;
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

    private final PermissionService permissionService;
    private final UserAccountRepository userAccountRepository;

    @Override
    public UserAccount authFindById(UserAccount currentUser, Long targetUserId) {

        // 권한 체크
        if (!permissionService.isOwnerOrAdmin(currentUser, targetUserId));

        return userAccountRepository.findById(targetUserId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다: " + targetUserId));
    }

    @Override
    public UserAccount findById(Long targetUserId) {

        return userAccountRepository.findById(targetUserId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다: " + targetUserId));
    }

    @Override
    public boolean existsById(Long id) {
        return userAccountRepository.existsById(id);
    }


    @Override
    @Transactional
    public UserAccount updateUser(UserAccount currentUser, Long targetUserId, UpdateUserRequestDto updateRequest) {

        UserAccount userAccount = authFindById(currentUser, targetUserId);

        userAccount.updateInfo(updateRequest.getNickname(), updateRequest.getEmail(), updateRequest.getProfileImage());

        log.info("사용자 정보 업데이트: {}", targetUserId);
        return userAccountRepository.save(userAccount);
    }

    @Override
    @Transactional
    public void deleteUser(UserAccount currentUser, Long targetUserId) {

        UserAccount userAccount = authFindById(currentUser, targetUserId);

        userAccount.updateActive(false);


        log.info("사용자 비활설화: {}", userAccount);
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
