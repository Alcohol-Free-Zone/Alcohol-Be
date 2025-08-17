package com.alcohol.application.Album.dto;

import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
public class PhotoUploadRequestDto {
    // 펫 앨범용
    private Long petId;

    // 커스텀 앨범용
    private Long albumId;
    private List<Long> petIds; // 각 파일에 해당하는 펫 ID들
    private List<String> captions; // 각 파일의 캡션들 (선택사항)

    // 기존 파일 추가용
    private List<Long> fileIds;    // 추가할 기존 파일 ID들
}
