package com.hmall.common.config;


import feign.Logger;
import feign.RequestInterceptor;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

//@EnableFeignClients(basePackages = "com.hmall.common.client")
@Configuration
public class FeignConfig {
    @Bean
    public RequestInterceptor requestInterceptor(){
        return  new MyFeignInterceptor();
    }
    @Bean
    public Logger.Level leve(){
        return Logger.Level.BASIC;
    }

}
