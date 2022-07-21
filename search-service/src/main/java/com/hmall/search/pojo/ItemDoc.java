package com.hmall.search.pojo;

import com.hmall.common.pojo.Item;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Data
@NoArgsConstructor
public class ItemDoc {
    private Long id;
    private String name;
    private Long price;
    private Integer sold;
    private String image;
    private String category;
    private String brand;
    private Integer commentCount;
    private Boolean isAD;
    private List<String> suggestion;

    public ItemDoc(Item item) {
        this.id = item.getId();
        this.name = item.getName();
        this.price = item.getPrice();
        this.sold = item.getSold();
        this.image = item.getImage();
        this.category = item.getCategory();
        this.brand = item.getBrand();
        this.commentCount = item.getCommentCount();
        this.isAD = item.getIsAD();
        if(this.category.contains("/")){
            String[] arr = this.category.split("/");
            this.suggestion = new ArrayList<>();
            this.suggestion.add(this.brand);
            Collections.addAll(this.suggestion,arr);
        }else {
            this.suggestion = Arrays.asList(this.brand,this.category);
        }
    }
}
