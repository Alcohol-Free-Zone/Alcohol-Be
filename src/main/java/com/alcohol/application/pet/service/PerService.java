package com.alcohol.application.pet.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.alcohol.application.pet.entity.Pet;

public interface PerService {
    Page<Pet> findAll(Pageable pageable);
}
