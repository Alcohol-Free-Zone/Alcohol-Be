package com.alcohol.application.travel.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.alcohol.application.travel.entitiy.Favorite;

public interface FavoriteRepository extends JpaRepository<Favorite, Long> {
    
}
