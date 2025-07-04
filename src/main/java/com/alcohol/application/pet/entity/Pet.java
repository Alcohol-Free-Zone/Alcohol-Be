package com.alcohol.application.pet.entity;

import java.util.Date;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Entity
public class Pet {
    @Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
    
    // 반려정보 이미지
    private String imgUrl;

    // 반려동물 이름
    private Date birth;

    // 반려동물 메모
    private String memo;

    // 반려동물 이름
    private String petName;

    // 반려동물 종류
    private String breed;

    // 반려동물 나이
    private int petAge;
}
