package com.alcohol.application.travel.repository;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.alcohol.application.travel.dto.ReviewResponse;
import com.alcohol.application.travel.entitiy.Post;

public interface TravelRepository extends JpaRepository<Post, Long>{

    @Query(value = """
        SELECT 
            p.post_id AS postId,
            p.content_id AS contentId,
            MAX(p1.pet_name) AS petName,
            MAX(f.id) as postImage,
            MAX(f2.id) as petImage,
            p.is_open AS isOpen
        FROM post p
        LEFT JOIN post_pets pp 
            ON pp.post_id = p.post_id AND pp.pet_id in (:petIds) 
        LEFT JOIN pet p1 
            ON p1.pet_id = pp.pet_id
        LEFT JOIN post_files pf 
            ON pf.post_id = p.post_id
        LEFT JOIN file f
            ON f.related_id = pf.file_id and f.file_type = 'POST_IMAGE'
        LEFT JOIN file f2
        	ON f2.related_id = pp.pet_id and f2.file_type = 'PET_PROFILE'
        WHERE (p.is_open = 'A' AND p.content_id IN ('A005'))
            OR (pp.pet_id in (:petIds) AND p.is_open = 'F')
            OR (p.user_id = :userId AND p.is_open = 'M')
        GROUP BY p.post_id
        """,
        countQuery = """
            SELECT COUNT(DISTINCT p.post_id)
            FROM post p
            LEFT JOIN post_pets pp 
                ON pp.post_id = p.post_id AND pp.pet_id IN (:petIds)
            WHERE (p.is_open = 'A' AND p.content_id IN (:contentIds))
            OR (pp.pet_id IN (:petIds) AND p.is_open = 'F')
            OR (p.user_id = :userId AND p.is_open = 'M')
            """,
        nativeQuery = true)
    Page<Object[]> findAllByIsOpenOrFriendsPrivate(
        @Param("petIds") List<Long> petIds,
        @Param("contentIds") List<String> contentIds,
        @Param("userId") Long userId,
        Pageable pageable
    );

    @Query(value = """
        SELECT distinct
            p.post_id AS postId,
            p.created_at as createdAt,
            p.rating,
            f.id as imgId,
            ua.nickname
        FROM post p
        left join post_files pf on pf.post_id = p.post_id
        left join file f on f.related_id = pf.post_id
		left join user_account ua on ua.id = p.user_id      
		where 1=1
		and p.post_id = :postId
        """, nativeQuery = true)
    List<Object[]> getPostNative(@Param("postId") Long postId);

}
