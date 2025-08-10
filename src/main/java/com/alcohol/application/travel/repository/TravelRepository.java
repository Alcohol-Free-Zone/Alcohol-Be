package com.alcohol.application.travel.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.alcohol.application.travel.entitiy.Post;

public interface TravelRepository extends JpaRepository<Post, Long>{
    
}
