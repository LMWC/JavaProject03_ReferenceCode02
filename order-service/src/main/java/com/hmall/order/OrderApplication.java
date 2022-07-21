package com.hmall.order;

import com.hmall.common.client.ItemClient;
import com.hmall.common.client.UserClient;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Import;

@MapperScan("com.hmall.order.mapper")
@SpringBootApplication
@EnableFeignClients(clients = {UserClient.class,ItemClient.class})
//@Import(value = com.hmall.common.config.FeignConfig.class)
public class OrderApplication {

    public static void main(String[] args) {
        SpringApplication.run(OrderApplication.class, args);
    }

}