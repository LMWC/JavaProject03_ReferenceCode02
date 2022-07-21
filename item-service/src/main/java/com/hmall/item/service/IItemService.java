package com.hmall.item.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hmall.item.pojo.Item;

public interface IItemService extends IService<Item> {
    /**
     * 扣减库存
     * @param itemId
     * @param num
     */
    void deduct(Long itemId, Integer num);

    /**
     * 增加库存
     * @param itemId
     * @param num
     */
    void rollBackItem(Long itemId, Integer num);
}
