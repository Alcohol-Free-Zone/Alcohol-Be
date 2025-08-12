package com.alcohol.application.travel.repository;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.alcohol.application.travel.entitiy.Post;

public interface TravelRepository extends JpaRepository<Post, Long>{

    @Query(value = """
        SELECT 
            p.post_id AS postId,
            p.content_id AS contentId,
            MIN(p1.pet_name) AS petName,
            MIN(p1.img_url) AS petImage,
            p.image_ids[1] AS postImage,
            p.is_open AS isOpen
        FROM post p
        LEFT JOIN pet p1
            ON p1.pet_id = ANY(CAST(:petIds AS bigint[]))
        AND p.pet_ids && CAST(:petIds AS bigint[])
        WHERE (p.is_open = 'A' AND p.content_id IN :contentIds)
        OR (p.pet_ids && CAST(:petIds AS bigint[]) AND p.is_open = 'F')
        OR (p.user_id = :userId AND p.is_open = 'M')
        GROUP BY p.post_id
    """,
    countQuery = """
        SELECT COUNT(*) 
        FROM post p
        WHERE (p.is_open = 'A' AND p.content_id IN :contentIds)
        OR (p.pet_ids && CAST(:petIds AS bigint[]) AND p.is_open = 'F')
        OR (p.user_id = :userId AND p.is_open = 'M')
        """,
    nativeQuery = true)
    Page<Object[]> findAllByIsOpenOrFriendsPrivate(
        @Param("petIds") String petIds,
        @Param("contentIds") List<String> contentIds,
        @Param("userId") Long userId,
        Pageable pageable
    );

}
