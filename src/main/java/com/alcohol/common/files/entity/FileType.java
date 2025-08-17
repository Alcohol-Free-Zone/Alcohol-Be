package com.alcohol.common.files.entity;

public enum FileType {
    IMAGE("이미지"),
    DOCUMENT("문서"),
    VIDEO("동영상"),
    AUDIO("오디오");

    private final String description;

    FileType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
