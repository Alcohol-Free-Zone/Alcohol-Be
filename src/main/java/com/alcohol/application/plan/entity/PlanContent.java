package com.alcohol.application.plan.entity;

import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PlanContent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long planContentId;

    private String contentId;

    // 지역코드
    private String areaCode;

    // 지역코드명
    private String areaCodeNm;

    // 시군구코드
    private String sigunguCode;

    // 시군구코드명
    private String sigunguCodeNm;

    // 주소
    private String addr;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plan_id", nullable = false)
    private Plan plan;

}