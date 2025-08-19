package com.alcohol.application.planInvite.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.alcohol.application.planInvite.entity.PlanInvite;

public interface PlanInviteRepository extends JpaRepository<PlanInvite, Long> {

    List<PlanInvite> findByReceiverUser_Id(Long userId);

    Optional<PlanInvite> findByPlanInviteIdAndReceiverUser_Id(Long planInviteId, Long userId);
    
}
