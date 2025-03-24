package com.alcohol.Controller;

import com.alcohol.entity.UserAccount;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TestRepository extends JpaRepository<UserAccount, Long> {



}
