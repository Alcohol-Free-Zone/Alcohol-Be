package com.alcohol.util.pagination;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import lombok.Setter;

@Setter
public class PageRequestDto {
    private int pageNo = 1;        
    private int numOfRows = 20;       

    public Pageable toPageable() {
        return PageRequest.of(pageNo, numOfRows);
    }
    
}
