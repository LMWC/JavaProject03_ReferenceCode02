package com.hmall.search.controller;


import com.hmall.common.dto.PageDTO;
import com.hmall.search.pojo.ItemDoc;
import com.hmall.search.pojo.RequestParam;
import com.hmall.search.service.ClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/search")
public class SearchController {

    @Autowired
    private ClientService clientService;

    /**
     * 搜索
     */
    @PostMapping("/list")
    public PageDTO<ItemDoc> list(@RequestBody RequestParam requestParam){
        return clientService.list(requestParam);
    }

    /**
     * 条件过滤
     */
    @PostMapping("/filters")
    public Map<String, List<String>> filters(@RequestBody RequestParam requestParam){
        return clientService.filters(requestParam);
    }

    /**
     * 自动补全
     */
    @GetMapping("/suggestion")
    public List<String> suggestion(@org.springframework.web.bind.annotation.RequestParam("key") String prefix){
        return clientService.suggestion(prefix);
    }

}
