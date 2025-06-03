package com.alcohol.application.auth.service;

import java.util.Map;

public interface KakaoApiService {
    Map<String, Object> getUserInfo(String code);
    String getKakaoLoginUrl();
}
