package com.alcohol.util.pagination;

import java.util.List;

import org.springframework.data.domain.Page;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class PageResponseDto<T> {
    private List<T> content;     // 실제 데이터 리스트
    private boolean hasNext;     // 다음 페이지 존재 여부
    private long totalCount;     // 전체 건수
    private int pageNo;          // 현재 페이지 번호
    private int numOfRows;       // 페이지 크기

    public static <T> PageResponseDto<T> from(Page<T> page) {
        return new PageResponseDto<>(
                page.getContent(),
                page.hasNext(),
                page.getTotalElements(),
                page.getNumber(),
                page.getSize()
        );
    }
}
