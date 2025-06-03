package com.alcohol.application.userAccount.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.alcohol.application.userAccount.entity.UserAccount;

@Repository
public interface UserAccountRepository extends JpaRepository<UserAccount, Long> {

    // 소셜 로그인 사용자 조회 (provider + providerId 조합으로 유니크)
    Optional<UserAccount> findByProviderAndProviderId(String provider, String providerId);

    // 이메일로 사용자 조회
    Optional<UserAccount> findByEmail(String email);

    // 프로바이더별 사용자 조회 (추가 필요)
    List<UserAccount> findByProvider(String provider);

    // 활성 사용자만 조회
    Optional<UserAccount> findByIdAndIsActiveTrue(Long id);

    // 프로바이더별 활성 사용자 조회
    @Query("SELECT u FROM UserAccount u WHERE u.provider = :provider AND u.isActive = true")
    List<UserAccount> findActiveUsersByProvider(@Param("provider") String provider);

    // 사용자 존재 여부 확인 (provider + providerId)
    boolean existsByProviderAndProviderId(String provider, String providerId);

    // 이메일 중복 확인
    boolean existsByEmail(String email);

    // 활성 사용자 수 조회
    @Query("SELECT COUNT(u) FROM UserAccount u WHERE u.isActive = true")
    long countActiveUsers();

    // 프로바이더별 사용자 수 조회
    @Query("SELECT COUNT(u) FROM UserAccount u WHERE u.provider = :provider AND u.isActive = true")
    long countByProvider(@Param("provider") String provider);

    // 최근 가입한 사용자들 조회 (관리자용)
    @Query("SELECT u FROM UserAccount u WHERE u.isActive = true ORDER BY u.createdAt DESC")
    List<UserAccount> findRecentUsers(Pageable pageable);

    // 닉네임으로 검색 (부분 일치)
    @Query("SELECT u FROM UserAccount u WHERE u.nickname LIKE %:nickname% AND u.isActive = true")
    List<UserAccount> findByNicknameContaining(@Param("nickname") String nickname);
}
