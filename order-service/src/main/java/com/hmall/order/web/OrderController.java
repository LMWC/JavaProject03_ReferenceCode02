package com.hmall.order.web;

import com.hmall.order.common.UserId;
import com.hmall.order.pojo.Order;
import com.hmall.order.pojo.RequestArgs;
import com.hmall.order.service.IOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("order")
public class OrderController {

   @Autowired
   private IOrderService orderService;

   @GetMapping("{id}")
   public Order queryOrderById(@PathVariable("id") Long orderId) {
      return orderService.getById(orderId);
   }
   /**
    * 下单
    * @param args
    * @return
    */
   @PostMapping
   public Long takeOrder(@RequestBody RequestArgs args){
      Long orderId = orderService.takeOrder(args);
      return orderId;
   }
}
