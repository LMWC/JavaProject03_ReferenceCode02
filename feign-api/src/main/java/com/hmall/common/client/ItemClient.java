package com.hmall.common.client;

import com.hmall.common.config.FeignConfig;
import com.hmall.common.dto.PageDTO;
import com.hmall.common.pojo.Item;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

/**
 *商品微服务远程过程调用
 */
@FeignClient(value ="itemservice",configuration = FeignConfig.class)
public interface ItemClient {
    /**
     * 根据id获取商品
     * @param id
     * @return
     */
    @GetMapping("item/{id}")
    Item getItemById(@PathVariable("id") Long id);

    /**
     * 根据id减对应的库存
     */
    @GetMapping("/item/stock/{itemId}/{num}")
     void deduct(@PathVariable("itemId") Long itemId, @PathVariable("num") Integer num) ;

    /**
     * 根据id加对应的库存
     * @param itemId
     * @param num
     */
    @GetMapping("/item/stockRollBack/{itemId}/{num}")
    void rollBackItem(@PathVariable("itemId")Long itemId,@PathVariable("num") Integer num);

    /**
     * 分页查询
     * @param page
     * @param size
     * @return
     */
    @GetMapping("/item/list")
    PageDTO<Item> findPage(@RequestParam("page") Integer page,@RequestParam("size") Integer size);


    /**
     * 新增商品
     */
    @PostMapping("/item")
    void insertItem(@RequestBody Item item);

    /**
     * 修改商品
     */
    @PutMapping("/item")
    void updateItem(@RequestBody Item item);

    /**
     * 删除商品
     */
    @DeleteMapping("/item/{id}")
    void deleteItem(@RequestParam("id") Long id);
}
