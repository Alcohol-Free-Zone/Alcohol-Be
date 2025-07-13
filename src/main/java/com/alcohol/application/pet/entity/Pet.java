package com.alcohol.application.pet.entity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.alcohol.application.userAccount.entity.UserAccount;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
public class Pet {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "pet_id")
    private Long petId;

    // 반려동물 이미지 URL (최대 1000자)
    @Column(length = 1000)
    private String imgUrl;

    // 반려동물 생일
    private Date birth;

    // 반려동물 메모 (최대 50자)
    @Column(length = 50)
    private String memo;

    // 반려동물 이름 (최대 20자, NOT NULL)
    @Column(length = 20, nullable = false)
    private String petName;

    // 반려동물 종류 (최대 50자)
    @Column(length = 50)
    private String breed;

    // 반려동물 나이 
    @Column(nullable = false)
    private int petAge;

    @OneToMany(mappedBy = "pet", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PetPersonalityTag> personalityTags = new ArrayList<>();

    @OneToMany(mappedBy = "pet", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PetAnniversary> petAnniversaries = new ArrayList<>();
    
    // 일단 주석
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserAccount userAccount;

}
