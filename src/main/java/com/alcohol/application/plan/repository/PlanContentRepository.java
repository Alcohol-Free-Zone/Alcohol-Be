package com.alcohol.application.plan.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.alcohol.application.plan.entity.Plan;
import com.alcohol.application.plan.entity.PlanContent;

public interface PlanContentRepository extends JpaRepository<PlanContent, Long> {

    void deleteByPlan(Plan plan);
    // Define methods for PlanContent repository if needed

    List<PlanContent> findAllByPlan(Plan plan);

    boolean existsByPlanAndContentId(Plan plan, String contentId);

    Optional<PlanContent> findByPlanAndContentId(Plan plan, String contentId);

    
}
