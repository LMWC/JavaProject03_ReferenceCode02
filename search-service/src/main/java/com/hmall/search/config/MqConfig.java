package com.hmall.search.config;

import com.hmall.search.contans.ItemMqConstants;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MqConfig {

    @Bean
    public TopicExchange topicExchange(){
        return new TopicExchange(ItemMqConstants.EXCHANGE_NAME,true,false);
    }

    @Bean
    public Queue insertQueue(){
        return new Queue(ItemMqConstants.INSERT_QUEUE_NAME,true);
    }

    @Bean
    public Queue deleteQueue(){
        return new Queue(ItemMqConstants.DELETE_QUEUE_NAME,true);
    }

    @Bean
    public Binding insertBindingQueue(){
        return BindingBuilder.bind(insertQueue()).to(topicExchange()).with(ItemMqConstants.INSERT_KEY);
    }

    @Bean
    public Binding deleteBindingQueue(){
        return BindingBuilder.bind(deleteQueue()).to(topicExchange()).with(ItemMqConstants.DELETE_KEY);
    }
}
