package com.alcohol.application.auth.service;

import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.alcohol.application.auth.service.GoogleApiService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class GoogleApiServiceImpl implements GoogleApiService {

    private final RestTemplate restTemplate;

    @Value("${oauth.google.client-id}")
    private String googleClientId;

    @Value("${oauth.google.client-secret}")
    private String googleClientSecret;

    @Value("${oauth.google.redirect-uri}")
    private String googleRedirectUri;

    private static final String TOKEN_URL = "https://oauth2.googleapis.com/token";
    private static final String USER_INFO_URL = "https://www.googleapis.com/oauth2/v2/userinfo";
    private static final String GOOGLE_AUTH_URL = "https://accounts.google.com/o/oauth2/auth";

    //  구글 로그인 URL 생성 메서드 추가
    @Override
    public String getGoogleLoginUrl() {
        return String.format(
                "%s?response_type=code&client_id=%s&redirect_uri=%s&scope=profile email",
                GOOGLE_AUTH_URL,
                googleClientId,
                googleRedirectUri
        );
    }

    @Override
    public Map<String, Object> getUserInfo(String code) {
        try {
            String accessToken = getAccessToken(code);
            return getUserInfoWithToken(accessToken);
        } catch (Exception e) {
            log.error("구글 사용자 정보 조회 실패: {}", e.getMessage());
            throw new RuntimeException("구글 로그인 처리 중 오류가 발생했습니다.", e);
        }
    }

    private String getAccessToken(String code) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "authorization_code");
        body.add("client_id", googleClientId);
        body.add("client_secret", googleClientSecret);
        body.add("redirect_uri", googleRedirectUri);
        body.add("code", code);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);
        ResponseEntity<Map> response = restTemplate.postForEntity(TOKEN_URL, request, Map.class);

        if (response.getBody() == null || !response.getBody().containsKey("access_token")) {
            throw new RuntimeException("구글 액세스 토큰 발급 실패");
        }

        return (String) response.getBody().get("access_token");
    }

    private Map<String, Object> getUserInfoWithToken(String accessToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);

        HttpEntity<String> request = new HttpEntity<>(headers);
        ResponseEntity<Map> response = restTemplate.exchange(
                USER_INFO_URL, HttpMethod.GET, request, Map.class);

        if (response.getBody() == null) {
            throw new RuntimeException("구글 사용자 정보 조회 실패");
        }

        return response.getBody();
    }
}
