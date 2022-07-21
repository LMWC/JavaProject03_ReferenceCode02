package com.hmall.common.client;

import com.hmall.common.config.FeignConfig;
import com.hmall.common.pojo.Address;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 *用户微服务远程过程调用
 */
@FeignClient(value ="userservice",configuration = FeignConfig.class)
public interface UserClient {
    /**
     * 根据地址id获取用户
     * @param id
     * @return
     */
    @GetMapping("/address/{id}")
    public Address findAddressById(@PathVariable("id") Integer id);

}
