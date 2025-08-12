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
            MIN(f.file_path) AS postImage, -- 대표 이미지
            p.is_open AS isOpen
        FROM post p
        LEFT JOIN post_pet pp 
            ON pp.post_id = p.post_id
        LEFT JOIN pet p1 
            ON p1.pet_id = pp.pet_id
        LEFT JOIN post_file pf 
            ON pf.post_id = p.post_id
        LEFT JOIN file f
            ON f.file_id = pf.file_id
        WHERE (p.is_open = 'A' AND p.content_id IN :contentIds)
            OR (p.pet_ids && CAST(:petIds AS bigint[]) AND p.is_open = 'F')
            OR (p.user_id = :userId AND p.is_open = 'M')
        GROUP BY p.post_id
        """,
        countQuery = """
            SELECT COUNT(DISTINCT p.post_id)
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
