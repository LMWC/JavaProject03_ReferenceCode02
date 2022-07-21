package com.hmall.item.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hmall.item.mapper.ItemMapper;
import com.hmall.item.pojo.Item;
import com.hmall.item.service.IItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ItemService extends ServiceImpl<ItemMapper, Item> implements IItemService {
    @Autowired
    private ItemMapper itemMapper;
    /**
     * 扣减库存
     *
     * @param itemId 商品id
     * @param num  提交的库存个数
     */
    @Override
    public void deduct(Long itemId, Integer num) {
        itemMapper.deduct(itemId,num);
    }

    /**
     * 增加库存
     *
     * @param itemId
     * @param num
     */
    @Override
    public void rollBackItem(Long itemId, Integer num) {
        itemMapper.rollBackItem(itemId,num);
    }
}
