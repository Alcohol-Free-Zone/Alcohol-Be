package com.alcohol.util.pagination;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import lombok.Setter;
import org.springframework.util.StringUtils;

@Setter
public class PageRequestDto {
    private int pageNo = 0;
    private int numOfRows = 10;

    private String searchText = "";  // 검색어 추가

    public Pageable toPageable() {
        return PageRequest.of(pageNo, numOfRows);
    }

    public boolean hasSearchText() {
        return StringUtils.hasText(searchText);
    }

    public String getSearchText() {
        return searchText;
    }
    
}
