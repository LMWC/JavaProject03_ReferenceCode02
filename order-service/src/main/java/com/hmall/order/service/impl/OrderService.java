package com.hmall.order.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hmall.common.client.ItemClient;
import com.hmall.common.client.UserClient;
import com.hmall.common.pojo.Address;
import com.hmall.common.pojo.Item;
import com.hmall.order.common.DelaySender;
import com.hmall.order.mapper.OrderDetailMapper;
import com.hmall.order.mapper.OrderLogisticsMapper;
import com.hmall.order.mapper.OrderMapper;
import com.hmall.order.pojo.Order;
import com.hmall.order.pojo.OrderDetail;
import com.hmall.order.pojo.OrderLogistics;
import com.hmall.order.pojo.RequestArgs;
import com.hmall.order.service.IOrderService;
import com.hmall.order.util.SnowFlakeUtil;
import io.seata.spring.annotation.GlobalTransactional;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class OrderService extends ServiceImpl<OrderMapper, Order> implements IOrderService {

    @Autowired
    private  ItemClient itemClient;

    @Autowired
    private  UserClient userClient;
    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private OrderDetailMapper orderDetailMapper;
    @Autowired
    private OrderLogisticsMapper orderLogisticsMapper;



    @Autowired
    private DelaySender delaySender;
    /**
     * 下单
     * @param args
     * @return
     */
    @Override
    @GlobalTransactional
    public Long takeOrder(RequestArgs args) {
        //1根据雪花算法生成订单id
        final Long orderId = SnowFlakeUtil.getId();
        //1.1远程调用用户服务addressid获取地址对象
        Integer addressId = args.getAddressId();
        Address address = userClient.findAddressById(addressId);
        //2商品微服务提供FeignClient，实现根据id查询商品的接口
        //2.1根据itemId查询商品信息
        //Item item = itemClient.getItemById(100000003145L);
        Long itemId = args.getItemId();
        Item item = itemClient.getItemById(itemId);

        //3基于商品价格、购买数量计算商品总价：totalFee
        long totalFee = item.getPrice()*args.getNum();
        //4封装Order对象，1状态status为未支付
        Order order = new Order();
        order.setId(orderId);
        order.setStatus(1);
        order.setTotalFee(totalFee);
        order.setPaymentType(args.getPaymentType());
        order.setUserId(address.getUserId());
        order.setCreateTime(new Date());
        order.setUpdateTime(new Date());
        //5将Order写入数据库tb_order表中
        orderMapper.insert(order);
        //6将商品信息、orderId信息封装为OrderDetail对象，写入tb_order_detail表
        OrderDetail orderDetail = new OrderDetail();
        orderDetail.setOrderId(orderId);
        orderDetail.setNum(args.getNum());
        orderDetail.setItemId(item.getId());
        BeanUtils.copyProperties(item,orderDetail);
        orderDetail.setId(null);
        orderDetail.setCreateTime(new Date());
        orderDetail.setUpdateTime(new Date());
        orderDetailMapper.insert(orderDetail);
        //7根据addressId查询user-service服务，获取地址信息
        //将地址封装为OrderLogistics对象，写入tb_order_logistics表
        OrderLogistics orderLogistics = new OrderLogistics();
        BeanUtils.copyProperties(address,orderLogistics);
        orderLogistics.setOrderId(orderId);
        orderLogistics.setCreateTime(new Date());
        orderLogistics.setUpdateTime(new Date());
        orderLogisticsMapper.insert(orderLogistics);
        //- 在item-service提供减库存接口，并编写FeignClient
        //- 12）调用item-service的减库存接口
        itemClient.deduct(item.getId(), args.getNum());
        //发送延迟信息
        delaySender.sendDelay(orderId);
        return orderId;
    }
}
