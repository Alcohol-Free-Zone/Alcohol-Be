package com.alcohol.application.plan.entity;

import com.alcohol.application.pet.entity.Pet;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
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
public class PlanPet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long planPetId;

    private Long planId;

    private Long petId;

    private Long userId;  // userId 는 별도 추가
}