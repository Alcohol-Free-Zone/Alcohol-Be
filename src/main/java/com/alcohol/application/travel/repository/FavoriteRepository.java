package com.alcohol.application.travel.repository;

import java.util.List;
import java.util.Optional;

import org.apache.ibatis.annotations.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.alcohol.application.travel.entitiy.Favorite;

public interface FavoriteRepository extends JpaRepository<Favorite, Long> {
    @Query(value = """
        SELECT content_id
        FROM favorite
        WHERE user_id = :id
        """,
        nativeQuery = true)
    List<String> getFavorites(@Param("id") Long id);

    Optional<Favorite> findByContentIdAndUserId(String contentId, Long userId);
    
}
