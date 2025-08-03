package com.alcohol.application.plan.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.alcohol.application.plan.entity.Plan;
import com.alcohol.application.plan.entity.PlanUser;

public interface PlanUserRepository extends JpaRepository<PlanUser, Long> {

    boolean existsByPlanAndUser_Id(Plan plan, Long userId);

}
