package com.alcohol.application.petAlbum.dto;

import lombok.Getter;
import lombok.Setter;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Getter
@Setter
public class CustomAlbumCreateRequestDto {

    @NotBlank(message = "앨범 이름은 필수입니다.")
    @Size(max = 50, message = "앨범 이름은 50자 이내여야 합니다.")
    private String name;

    @Size(max = 500, message = "앨범 설명은 500자 이내여야 합니다.")
    private String description;
}
