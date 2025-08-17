package com.alcohol.application.travel.repository;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.alcohol.application.travel.dto.AroundProjection;
import com.alcohol.application.travel.entitiy.Post;

public interface TravelRepository extends JpaRepository<Post, Long>{

    @Query(value = """
        SELECT 
            p.post_id AS postId,
            p.content_id AS contentId,
            MAX(p1.pet_name) AS petName,
            MAX(f.id) as postImage,
            MAX(f2.id) as petImage,
            p.is_open AS isOpen,
            p.addr,
            MAX(ua.nickname) AS nickName,
            p.created_at AS createdAt,
            p.rating
        FROM post p
        LEFT JOIN post_pets pp 
            ON pp.post_id = p.post_id AND pp.pet_id in (:petIds) 
        LEFT JOIN pet p1 
            ON p1.pet_id = pp.pet_id
        LEFT JOIN post_files pf 
            ON pf.post_id = p.post_id
        LEFT JOIN file f
            ON f.id = pf.file_id and f.file_type = 'POST_IMAGE'
        LEFT JOIN file f2
        	ON f2.id = pp.pet_id and f2.file_type = 'PET_PROFILE'
        LEFT JOIN user_account ua
            ON ua.id = p.user_id
        WHERE (p.is_open = 'A' AND p.is_delete = 'N' AND p.content_id IN (:contentIds))
            OR (pp.pet_id in (:petIds) AND p.is_open = 'F' AND p.is_delete = 'N')
            OR (p.user_id = :userId AND p.is_open = 'M' AND p.is_delete = 'N')
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
        left join file f on f.id = pf.post_id
		left join user_account ua on ua.id = p.user_id      
		where 1=1
		and p.post_id = :postId
        and p.is_delete = 'N'
        """, nativeQuery = true)
    List<Object[]> getPostNative(@Param("postId") Long postId);

    @Query(value = """
        WITH ranked_posts AS (
            SELECT
                p.*,
                ROW_NUMBER() OVER (PARTITION BY p.content_id ORDER BY p.created_at DESC) AS rn
            FROM post p
            WHERE p.is_delete = 'N'
            AND p.content_id IN (:contentIds)
        ),
        filtered_posts AS (
            SELECT *
            FROM ranked_posts
            WHERE rn <= 10
        )
        SELECT
            content_id AS contentId ,
            AVG(rating) AS rating,
            CASE
                WHEN AVG(CASE WHEN is_pet_yn = 'Y' THEN 1.0 ELSE 0 END) >= 0.5 THEN 'Y'
                ELSE 'N'
            END AS isPetYn
        FROM filtered_posts
        GROUP BY content_id;
        """,
        countQuery = """
            WITH ranked_posts AS (
                SELECT
                    p.post_id,
                    p.content_id,
                    ROW_NUMBER() OVER (PARTITION BY p.content_id ORDER BY p.created_at DESC) AS rn
                FROM post p
                LEFT JOIN post_pets pp 
                    ON pp.post_id = p.post_id AND pp.pet_id IN (:petIds)
                WHERE p.is_delete = 'N'
                AND (
                    (p.is_open = 'A' AND p.content_id IN (:contentIds))
                    OR (pp.pet_id IN (:petIds) AND p.is_open = 'F')
                    OR (p.user_id = :userId AND p.is_open = 'M')
                )
            ),
            filtered_posts AS (
                SELECT *
                FROM ranked_posts
                WHERE rn <= 10
            )
            SELECT COUNT(DISTINCT content_id)
            FROM filtered_posts
            """,
        nativeQuery = true)
    Page<Object[]> getArounds(List<String> contentIds, Pageable pageable);


    @Query(value = """
        SELECT
            MAX(p.content_id) AS contentId,
            AVG(p.rating) AS rating,
            count(p.content_id) AS reviewCount 
        FROM post p 
        WHERE 1=1
        AND p.content_id = :contentId
        AND p.is_delete = 'N';
        """, nativeQuery = true)
    AroundProjection getAround(String contentId);

    @Query(value = """
        SELECT p.content_id AS contentId, p.is_pet_yn AS isPetYn, p.created_at AS createdAt
        FROM post p
        WHERE p.content_id IN (:contentIds)
        AND p.is_delete = 'N'
        ORDER BY p.content_id, p.created_at DESC
        """, nativeQuery = true)
    List<Object[]> findRecentPostsByContentIds(@Param("contentIds") List<String> contentIds);

}
