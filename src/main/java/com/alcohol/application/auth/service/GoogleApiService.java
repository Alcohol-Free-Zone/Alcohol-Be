package com.alcohol.application.auth.service;

import java.util.Map;

public interface GoogleApiService {
    Map<String, Object> getUserInfo(String code);
    String getGoogleLoginUrl();
}
