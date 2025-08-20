package com.alcohol.application.petFriend.dto;

import com.alcohol.application.petFriend.entity.FriendStatus;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PetFriendStatusResponse {

    private FriendStatus status;
    private String message;

    public PetFriendStatusResponse(FriendStatus status, String message) {
        this.status = status;
        this.message = message;
    }


}
