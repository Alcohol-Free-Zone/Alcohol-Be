package com.alcohol.application.travel.repository;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.alcohol.application.travel.entitiy.Post;

public interface TravelRepository extends JpaRepository<Post, Long>{

    @Query("""
        SELECT r
        FROM Post r
        WHERE r.postId IN :userIds
    """)
    Page<Object[]> findReviewsWithLatestVisitUser(@Param("userIds") List<Long> targetUserIds, Pageable pageable);
    
}
