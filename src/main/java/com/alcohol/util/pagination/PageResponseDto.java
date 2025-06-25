package com.alcohol.util.pagination;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class PageResponseDto<T> {
    private List<T> content;
    private boolean hasNext;
}
