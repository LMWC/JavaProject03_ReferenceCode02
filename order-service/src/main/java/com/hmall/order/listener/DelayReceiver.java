package com.hmall.order.listener;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.hmall.common.client.ItemClient;
import com.hmall.order.config.DelayRabbitConfig;
import com.hmall.order.mapper.OrderDetailMapper;
import com.hmall.order.mapper.OrderMapper;
import com.hmall.order.pojo.Order;
import com.hmall.order.pojo.OrderDetail;
import com.hmall.order.service.IOrderService;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 *
 */
@Component
@Slf4j
public class DelayReceiver {
    @Autowired
    private IOrderService orderService;
    @Autowired
    private ItemClient itemClient;
    @Autowired
    private OrderDetailMapper orderDetailMapper;

    @RabbitListener(queues = {DelayRabbitConfig.ORDER_QUEUE_NAME})
    public void orderDelayQueue(Long orderId, Message message, Channel channel) { //监听者接收到到30分钟后的订单id
        Order order = orderService.getById(orderId); //调用order业务层根据订单获取对应的订单
        log.info("【orderDelayQueue 监听的消息】 - 【消费时间】 - [{}]- 【订单内容】 - [{}]", new Date(), order.toString());
        Integer orderStatus = order.getStatus();
        if (orderStatus == 1) { //得到order的状态是否为1  1是未付款且是30分后的话    否则丢弃
            LambdaUpdateWrapper<Order> wrapper = new LambdaUpdateWrapper<>(); //进行修改当前的订单状态为5
            wrapper.set(Order::getStatus,5)
                    .set(Order::getUpdateTime,new Date())
                    .eq(Order::getId,orderId);
            orderService.update(wrapper);
            //根据order订单查询订单明细查到商品明细进行远程调用
            LambdaQueryWrapper<OrderDetail> queryWrapper = new LambdaQueryWrapper<>(); //进行修改库存回滚
            queryWrapper
                    .eq(OrderDetail::getOrderId,orderId)
                    .select(OrderDetail::getItemId,OrderDetail::getNum);
            OrderDetail orderDetail = orderDetailMapper.selectOne(queryWrapper);
            //库存回滚 调用item-service，根据商品id、商品数量恢复库存
            itemClient.rollBackItem(orderDetail.getItemId(),orderDetail.getNum()); //远程过程调用修改库存回滚
        }
    }
}
