package com.alcohol.application.plan.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.alcohol.application.plan.entity.Plan;
import com.alcohol.application.plan.entity.PlanContent;

public interface PlanContentRepository extends JpaRepository<PlanContent, Long> {

    void deleteByPlan(Plan plan);
    // Define methods for PlanContent repository if needed
    
}
