package com.hmall.search.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RequestParam {
    private String brand;
    private String category;
    private String key;
    private Integer maxPrice;
    private Integer minPrice;
    private Integer page;
    private Integer size;
    private String sortBy;
}
