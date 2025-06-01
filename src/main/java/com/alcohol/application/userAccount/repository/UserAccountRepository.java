package com.alcohol.application.userAccount.repository;

import com.alcohol.application.userAccount.entity.UserAccount;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserAccountRepository extends JpaRepository<UserAccount, Long> {
}
