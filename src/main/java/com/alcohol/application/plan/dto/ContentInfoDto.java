package com.alcohol.application.plan.dto;

import com.alcohol.application.plan.entity.PlanContent;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ContentInfoDto {
    private String contentId;
    private String addr;
    private String areaCode;
    private String areaCodeNm;
    private String sigunguCode;
    private String sigunguCodeNm;

    public static ContentInfoDto from(PlanContent planContent) {
        return new ContentInfoDto(
            planContent.getContentId(),
            planContent.getAddr(),
            planContent.getAreaCode(),
            planContent.getAreaCodeNm(),
            planContent.getSigunguCode(),
            planContent.getSigunguCodeNm()
        );
    }
}
