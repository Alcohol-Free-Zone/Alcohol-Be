package com.alcohol.application.plan.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.alcohol.application.plan.entity.Plan;

public interface PlanRepository extends JpaRepository<Plan, Long> {

    List<Plan> findAllByCreateUserId_Id(Long id);

    List<Plan> findAllByCreateUserId_IdAndIsActive(Long currentUser, String string);

    Optional<Plan> findByPlanIdAndIsActive(Long planId, String string);
  
}
