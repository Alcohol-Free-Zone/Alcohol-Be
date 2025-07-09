package com.alcohol.application.Enum;

public enum PersonalityTagType {
    활동("#활동"),
    온순("#온순"),
    호기심("#호기심"),
    충성("#충성"),
    장난꾸러기("#장난꾸러기");

    private String tag;

    PersonalityTagType(String tag) {
        this.tag = tag;
    }

    public String getTag() {
        return tag;
    }
}
