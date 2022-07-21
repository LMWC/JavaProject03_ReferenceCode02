package com.hmall.order.common;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

import java.util.List;

@Configuration
public class WebMvcConfig extends WebMvcConfigurationSupport {
    @Override
    protected void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new CheckLoginInterceptor()).addPathPatterns("/**");
    }
    /**
     * 扩展消息转换器，使用自定义消息转换
     * 将自定义的消息转换器添加到List集合中，添加到第0个元素，优先处理
     * 雪花算法生成的id是19位,而js中只能承受16位会丢失精度,
     * 所以使用雪花算法的id要使用扩展消息转换器,这个是在Springboot中操作的
     */
    @Override
    protected void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
        //1.创建消息转换器对象
        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
        //2.设置转换器属性
        converter.setObjectMapper(new JacksonObjectMapper());
        //3.将这个消息转换器放在第0个位置
        converters.add(0, converter);
    }
}
