package com.hmall.order.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hmall.order.pojo.Order;
import com.hmall.order.pojo.RequestArgs;

import java.util.Map;

public interface IOrderService extends IService<Order> {
    /**
     * 下单
     * @param args
     * @return
     */
    Long takeOrder(RequestArgs args);
}
