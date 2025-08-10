package com.alcohol.application.petAlbum.dto;

import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
public class PhotoUploadRequestDto {
    private List<Long> petIds; // 각 파일에 해당하는 펫 ID들
    private List<String> captions; // 각 파일의 캡션들 (선택사항)
}
