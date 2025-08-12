package com.alcohol.application.Enum;

public enum PersonalityTagType {
    온순("#온순"),
    활발("#활발"),
    장난기많음("#장난기많음"),
    애교많음("#애교많음"),
    독립적임("#독립적임"),
    호기심많음("#호기심많음"),
    겁많음("#겁많음"),
    충성심강함("#충성심강함"),
    사교적임("#사교적임"),
    느긋함("#느긋함"),
    영리함("#영리함"),
    조심성많음("#조심성많음"),
    집요함("#집요함"),
    민첩함("#민첩함"),
    탐험심강함("#탐험심강함");

    private final String tag;

    PersonalityTagType(String tag) {
        this.tag = tag;
    }

    public String getTag() {
        return tag;
    }
}