package com.alcohol.application.auth.service;

import java.util.Map;

import com.alcohol.application.auth.dto.TokenResponseDto;
import com.alcohol.application.userAccount.entity.Role;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alcohol.Config.token.JWTImpl;
import com.alcohol.application.userAccount.entity.UserAccount;
import com.alcohol.application.userAccount.repository.UserAccountRepository;
import com.alcohol.application.userAccount.dto.UserAccountResponseDto;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class OAuthServiceImpl implements OAuthService {

    private final UserAccountRepository userAccountRepository;
    private final JWTImpl jwtImpl;
    private final KakaoApiService kakaoApiService;
    private final GoogleApiService googleApiService;

    //  카카오 로그인 URL 생성
    @Override
    public String getKakaoLoginUrl() {
        return kakaoApiService.getKakaoLoginUrl();
    }

    //  구글 로그인 URL 생성
    @Override
    public String getGoogleLoginUrl() {
        return googleApiService.getGoogleLoginUrl();
    }

    public TokenResponseDto processKakaoLogin(String code) {
        // 카카오 API로 사용자 정보 가져오기
        Map<String, Object> kakaoUserInfo = kakaoApiService.getUserInfo(code);

        // 사용자 정보 추출
        String providerId = kakaoUserInfo.get("id").toString();
        Map<String, Object> kakaoAccount = (Map<String, Object>) kakaoUserInfo.get("kakao_account");
        Map<String, Object> profile = (Map<String, Object>) kakaoAccount.get("profile");

        String email = (String) kakaoAccount.get("email");
        String nickname = (String) profile.get("nickname");
        String profileImage = (String) profile.get("profile_image_url");

        // 기존 사용자 조회 또는 신규 생성 (자동 회원가입)
        UserAccount userAccount = findOrCreateUser("kakao", providerId, email, nickname, profileImage);

        // JWT 토큰 생성
        String accessToken = jwtImpl.createJwt(
                userAccount.getId().toString(),
                userAccount.getRole().name(),
                "kakao"
        );

        // 리프레시 토큰 생성
        String refreshToken = jwtImpl.createRefreshToken(userAccount.getId().toString());

        return TokenResponseDto.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .userAccountResponseDto(UserAccountResponseDto.from(userAccount))
                .build();
    }

    public TokenResponseDto processGoogleLogin(String code) {
        // 구글 API로 사용자 정보 가져오기
        Map<String, Object> googleUserInfo = googleApiService.getUserInfo(code);

        // 사용자 정보 추출
        String providerId = (String) googleUserInfo.get("id");
        String email = (String) googleUserInfo.get("email");
        String nickname = (String) googleUserInfo.get("name");
        String profileImage = (String) googleUserInfo.get("picture");

        // 기존 사용자 조회 또는 신규 생성 (자동 회원가입)
        UserAccount userAccount = findOrCreateUser("google", providerId, email, nickname, profileImage);

        // JWT 토큰 생성
        String accessToken = jwtImpl.createJwt(
                userAccount.getId().toString(),
                userAccount.getRole().name(),
                "google"
        );

        // 리프레시 토큰 생성
        String refreshToken = jwtImpl.createRefreshToken(userAccount.getId().toString());

        return TokenResponseDto.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .userAccountResponseDto(UserAccountResponseDto.from(userAccount))
                .build();
    }

    private UserAccount findOrCreateUser(String provider, String providerId,
                                         String email, String nickname, String profileImage) {

        return userAccountRepository
                .findByProviderAndProviderId(provider, providerId)
                .map(existingUser -> {
                    // 기존 사용자 정보 업데이트
                    existingUser.updateInfo(nickname, profileImage);
                    return userAccountRepository.save(existingUser);
                })
                .orElseGet(() -> {
                    // 신규 사용자 자동 생성
                    UserAccount newUser = UserAccount.builder()
                            .provider(provider)
                            .providerId(providerId)
                            .email(email)
                            .nickname(nickname)
                            .profileImage(profileImage)
                            .role(Role.USER)
                            .isActive(true)
                            .build();

                    log.info("새로운 {} 사용자 자동 생성: {}", provider, email);
                    return userAccountRepository.save(newUser);
                });
    }

    public void logout(String token) {
        // 토큰 블랙리스트 처리 (선택사항)
        log.info("사용자 로그아웃 처리");
    }
}
