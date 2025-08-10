package com.alcohol.application.travel.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.alcohol.application.travel.entitiy.Favorite;

public interface FavoriteRepository extends JpaRepository<Favorite, Long> {

    List<Favorite> findByUserId(Long id);
    
}
