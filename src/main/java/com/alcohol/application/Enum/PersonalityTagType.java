package com.alcohol.application.Enum;

public enum PersonalityTagType {
    ACTIVE("#활동"),
    GENTLE("#온순"),
    CURIOUS("#호기심"),
    LOYAL("#충성"),
    PLAYFUL("#장난꾸러기");

    private String tag;

    PersonalityTagType(String tag) {
        this.tag = tag;
    }

    public String getTag() {
        return tag;
    }
}
