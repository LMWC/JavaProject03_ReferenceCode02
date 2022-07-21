package com.hmall.search;

import com.hmall.common.client.ItemClient;
import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;


@SpringBootApplication
@MapperScan("com.hmall.search.mapper")
@EnableFeignClients(basePackageClasses = {ItemClient.class})
//@Import(value = com.hmall.common.config.FeignConfig.class)
public class SearchApplication {

    public static void main(String[] args) {
        SpringApplication.run(SearchApplication.class,args);
    }


    @Bean
    public RestHighLevelClient client(){
        return new RestHighLevelClient(
                RestClient.builder(HttpHost.create("http://192.168.211.131:9200"))
        );
    }


    @Bean
    public MessageConverter jsonMessageConverter(){
        return new Jackson2JsonMessageConverter();
    }



}
