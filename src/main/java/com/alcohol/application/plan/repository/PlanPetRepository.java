package com.alcohol.application.plan.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.alcohol.application.plan.entity.Plan;
import com.alcohol.application.plan.entity.PlanPet;

public interface PlanPetRepository extends JpaRepository<PlanPet, Long> {

    void deleteByPlan(Plan plan);
    // Define methods for PlanPet repository if needed
    
}
