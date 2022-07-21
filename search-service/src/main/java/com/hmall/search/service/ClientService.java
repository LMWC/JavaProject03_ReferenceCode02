package com.hmall.search.service;

import com.hmall.common.dto.PageDTO;
import com.hmall.search.pojo.ItemDoc;
import com.hmall.search.pojo.RequestParam;

import java.util.List;
import java.util.Map;

public interface ClientService {

    /**
     * 搜索
     * @param requestParam
     * @return
     */
    PageDTO<ItemDoc> list(RequestParam requestParam);

    /**
     * 搜索过滤
     * @param requestParam
     * @return
     */
    Map<String, List<String>> filters(RequestParam requestParam);

    /**
     * 自动补全
     * @param prefix
     * @return
     */
    List<String> suggestion(String prefix);
}
