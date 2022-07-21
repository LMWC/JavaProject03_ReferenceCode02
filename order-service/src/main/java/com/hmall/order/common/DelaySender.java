package com.hmall.order.common;

import com.hmall.order.config.DelayRabbitConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
@Component
@Slf4j
public class DelaySender {

    @Autowired
    private RabbitTemplate amqpTemplate;

    public void sendDelay(Long orderId) {
        log.info("【订单生成时间】" + new Date().toString() +"【1分钟后检查订单是否已经支付】" + orderId );
        this.amqpTemplate.convertAndSend(DelayRabbitConfig.ORDER_DELAY_EXCHANGE,
                DelayRabbitConfig.ORDER_DELAY_ROUTING_KEY, orderId , message -> {
            message.getMessageProperties().setExpiration(1 * 1000 * 60 + "");
            return message;
        });
    }
}
