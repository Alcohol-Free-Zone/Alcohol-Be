package com.alcohol.application.pet.entity;

import com.alcohol.application.Enum.PersonalityTagType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PetPersonalityTag {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "personality_tag_id")
    private Long personalityTagId;

    private PersonalityTagType tag;  // 예: "#활동", "#온순" 등

    @ManyToOne
    @JoinColumn(name = "pet_id")
    private Pet pet;
}