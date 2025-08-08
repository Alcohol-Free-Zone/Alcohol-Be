package com.alcohol.common.files.entity;

public enum FileType {
    PROFILE("프로필 이미지"),
    PET_ALBUM("펫 앨범"),
    PET_PROFILE("펫 프로필 이미지"),
    POST_IMAGE("게시물 이미지"),
    DOCUMENT("문서");

    private final String description;

    FileType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
