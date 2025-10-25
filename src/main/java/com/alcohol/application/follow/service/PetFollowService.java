package com.alcohol.application.follow.service;

import com.alcohol.application.pet.dto.PetResponseDto;
import com.alcohol.util.pagination.PageRequestDto;
import com.alcohol.util.pagination.PageResponseDto;

public interface PetFollowService {

    PageResponseDto<PetResponseDto> getMyFollowers(Long userId, PageRequestDto pageRequestDto);
    
}
