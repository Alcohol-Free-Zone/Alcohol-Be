package com.alcohol.application.petFriend.service;

import com.alcohol.application.pet.dto.PetResponseDto;
import com.alcohol.application.petFriend.dto.PetFriendResponseDto;
import com.alcohol.application.petFriend.entity.FriendStatus;
import com.alcohol.util.pagination.PageResponseDto;
import org.springframework.data.domain.Pageable;

public interface PetFriendService {

    void sendRequest(Long myPetId, Long targetPetId, Long loginUserId);
    void acceptRequest(Long requestId, Long loginUserId);
    void rejectRequest(Long requestId, Long loginUserId);
    void removeFriend(Long petId1, Long petId2, Long loginUserId);

    FriendStatus getFriendStatus(Long petId1, Long petId2);

    PageResponseDto<PetFriendResponseDto> receivedRequests(Long loginUserId, Pageable pageable);
    PageResponseDto<PetFriendResponseDto> sentRequests(Long loginUserId, Pageable pageable);
    PageResponseDto<PetResponseDto>       petFriends(Long petId, Pageable pageable);
}
