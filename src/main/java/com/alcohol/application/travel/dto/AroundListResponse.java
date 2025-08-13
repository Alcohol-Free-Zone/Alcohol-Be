package com.alcohol.application.travel.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AroundListResponse {
    private String contentId;
    private Float rating;
    private String isPetYn;

    public static AroundListResponse from(Object[] row) {
        return AroundListResponse.builder()
                .contentId((String) row[0])
                .rating(((Number) row[1]).floatValue())
                .isPetYn((String) row[2])
                .build();
    }
}
