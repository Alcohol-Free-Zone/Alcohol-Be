package com.alcohol.application.plan.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/plan")
public class PlanController {

    // 유저 일정 리스트 조회
    @GetMapping
    public ResponseEntity<?> getPlanListByUser() {
        return ResponseEntity.ok(null);
    }

    // 일정 상세조회
    @GetMapping("/{id}")
    public ResponseEntity<?> getPlanById(@PathVariable Long id) {
        return ResponseEntity.ok(null);
    }

    // 일정 추가, 수정하기
    @PostMapping
    public ResponseEntity<?> createOrUpdatePlan() {
        return ResponseEntity.ok(null);
    }

    // 일정 초대하기
    @PostMapping("/{id}/invite")
    public ResponseEntity<?> inviteUserToPlan(@PathVariable Long id) {
        return ResponseEntity.ok(null);
    }

    // 관심, 일정에 등록하기
    @PostMapping("/{id}/interest")
    public ResponseEntity<?> interestPlan(@PathVariable Long id) {
        return ResponseEntity.ok(null);
    }

    // 일정 삭제하기
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletePlan(@PathVariable Long id) {
        return ResponseEntity.ok(null);
    }
}
