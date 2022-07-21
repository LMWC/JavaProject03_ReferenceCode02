package com.hmall.item.web;

import com.hmall.item.common.UserId;
import com.hmall.item.contans.ItemMqConstants;
import com.hmall.item.mapper.ItemMapper;
import com.hmall.item.pojo.Item;
import com.hmall.item.common.UserId;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hmall.common.dto.PageDTO;
import com.hmall.item.pojo.Item;
import com.hmall.item.service.IItemService;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
@RestController
@RequestMapping("item")
public class ItemController {

    @Autowired
    private IItemService itemService;

    @Autowired
    private ItemMapper itemMapper;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    /**
     * 分页查询
     * @param page 页数
     * @param size 总数
     *
     */
    @GetMapping("list")
    public PageDTO<Item> findPage(@RequestParam("page") Integer page, @RequestParam("size") Integer size) {
        Page<Item> pageList = new Page<>(page, size);
        itemService.page(pageList);
        PageDTO<Item> pageDTO = new PageDTO<>();
        pageDTO.setTotal(pageList.getTotal());
        pageDTO.setList(pageList.getRecords());
        return pageDTO;
    }

/**
     * 根据id减对应的库存
     */
    @GetMapping("stock/{itemId}/{num}")
    public void deduct(@PathVariable("itemId") Long itemId, @PathVariable("num") Integer num) {
        if (itemId == null) {
            throw new RuntimeException("id不能为null");
        }
        itemService.deduct(itemId, num);
    }
    /**
     * 根据id添加对应的库存
     */
    @GetMapping("stockRollBack/{itemId}/{num}")
    public void rollBackItem(@PathVariable("itemId")Long itemId,@PathVariable("num") Integer num){
        if (itemId == null) {
            throw new RuntimeException("id不能为null");
        }
        itemService.rollBackItem(itemId, num);
    }

    /**
     * 商品上架下架
     * @param id 需要修改的商品id
     * @param status 修改的状态码
     */
    @PutMapping("status/{id}/{status}")
    public void updateStatus(@PathVariable Long id,@PathVariable Integer status){
        Item item = itemService.getById(id);
        Item newItem = new Item();
        newItem.setId(item.getId());
        newItem.setStatus(status);
        itemService.updateById(newItem);
    }

    /**
     * 新增商品
     * @param item 需要新增的商品对象
     */
    @PostMapping()
    public void insertItem(@RequestBody Item item){
        itemService.save(item);
        rabbitTemplate.convertAndSend(ItemMqConstants.EXCHANGE_NAME,
                ItemMqConstants.INSERT_KEY, item.getId());
    }

    /**
     * 修改商品
     * @param item 需要修改的商品对象
     */
    @PutMapping()
    public void updateItem(@RequestBody Item item){
        itemService.updateById(item);
        rabbitTemplate.convertAndSend(ItemMqConstants.EXCHANGE_NAME,
                ItemMqConstants.INSERT_KEY, item.getId());
    }

    /**
     * 删除商品
     * @param id 删除的商品id
     */
    @DeleteMapping("{id}")
    public void deleteItem(@PathVariable Long id){
        itemService.removeById(id);
        rabbitTemplate.convertAndSend(ItemMqConstants.EXCHANGE_NAME,
                ItemMqConstants.DELETE_KEY, id);
    }

    /**
     * 根据id查询商品
     * @param id
     * @return
     */
    @GetMapping("{id}")
    public Item getItemById(@PathVariable("id") Long id) {
        if (id == null) {
            throw new RuntimeException("id不能为null");
        }
        return itemService.getById(id);
    }

}
