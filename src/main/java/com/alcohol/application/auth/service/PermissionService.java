package com.alcohol.application.auth.service;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;
import com.alcohol.application.userAccount.entity.UserAccount;

@Service
public class PermissionService {
    public boolean isOwnerOrAdmin(UserAccount currentUser, Long targetUserId) {
        // 1) 본인 및 관리자 체크
        if (!currentUser.getId().equals(targetUserId) && !"ADMIN".equals(currentUser.getRole().name())) {
            throw new AccessDeniedException("권한이 없습니다.");
        }

        return true;
    }

}