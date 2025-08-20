package com.alcohol.application.petFriend.repository;

import com.alcohol.application.pet.entity.Pet;
import com.alcohol.application.petFriend.entity.FriendStatus;
import com.alcohol.application.petFriend.entity.PetFriend;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface PetFriendRepository extends JpaRepository<PetFriend, Long> {

    /* 두 펫이 친구인지(순서 무시) */
    @Query("""
           SELECT pf FROM PetFriend pf
           WHERE ((pf.requesterPet.petId = :p1 AND pf.receiverPet.petId = :p2) OR
                  (pf.requesterPet.petId = :p2 AND pf.receiverPet.petId = :p1))
             AND pf.status = :status
           """)
    Optional<PetFriend> findRelation(Long p1, Long p2, FriendStatus status);

    /* 대기중 요청 – 받은 쪽 기준 */
    Page<PetFriend> findByReceiverUserIdAndStatus(Long userId, FriendStatus status, Pageable pageable);

    /* 대기중 요청 – 보낸 쪽 기준 */
    Page<PetFriend> findByRequesterUserIdAndStatus(Long userId, FriendStatus status, Pageable pageable);

    /* 특정 펫의 친구 목록 (양방향) */
    @Query("""
           SELECT CASE WHEN pf.requesterPet.petId = :petId THEN pf.receiverPet
                       ELSE pf.requesterPet END
           FROM   PetFriend pf
           WHERE  (pf.requesterPet.petId = :petId OR pf.receiverPet.petId = :petId)
             AND  pf.status = 'ACCEPTED'
           """)
    Page<Pet> findFriendsOfPet(Long petId, Pageable pageable);

    @Query("""
   SELECT CASE
            WHEN pf.requesterPet.petId = :petId THEN pf.receiverPet.petId
            ELSE pf.requesterPet.petId
          END
   FROM  PetFriend pf
   WHERE (pf.requesterPet.petId = :petId OR pf.receiverPet.petId = :petId)
     AND pf.status = 'ACCEPTED'
""")
    Page<Long> findFriendPetIds(Long petId, Pageable pageable);

    /* 친구 관계 레코드 하나 가져오기 (순서 무시) */
    @Query("""
           SELECT pf FROM PetFriend pf
           WHERE (pf.requesterPet.petId = :p1 AND pf.receiverPet.petId = :p2) OR
                 (pf.requesterPet.petId = :p2 AND pf.receiverPet.petId = :p1)
           """)
    Optional<PetFriend> findRelationAnyStatus(Long p1, Long p2);


    @Query("""
   SELECT DISTINCT CASE
           WHEN pf.requesterPet.userAccount.id = :userId THEN pf.receiverPet.petId
           ELSE pf.requesterPet.petId END
   FROM PetFriend pf
   WHERE (pf.requesterPet.userAccount.id = :userId
       OR pf.receiverPet.userAccount.id = :userId)
     AND pf.status = 'ACCEPTED'
""")
    List<Long> findFriendPetIdsOfUser(Long userId);
}

