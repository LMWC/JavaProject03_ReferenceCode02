package com.hmall.search.listener;

import com.alibaba.fastjson.JSON;
import com.hmall.common.client.ItemClient;
import com.hmall.common.pojo.Item;
import com.hmall.search.contans.ItemMqConstants;
import com.hmall.search.pojo.ItemDoc;
import com.hmall.search.service.ClientService;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class ItemListener {

    @Autowired
    private ItemClient itemClient;

    @Autowired
    private RestHighLevelClient client;


    /**
     * 监听创建和修改队列
     */
    @RabbitListener(queues = ItemMqConstants.INSERT_QUEUE_NAME)
    public void listenerInsertOrUpdate(Long id){
        try {
            Item item = itemClient.getItemById(id);
            ItemDoc itemDoc = new ItemDoc(item);
            IndexRequest request = new IndexRequest("test02_index");
            request.source(JSON.toJSONString(itemDoc), XContentType.JSON);
            request.id(item.getId().toString());
            client.index(request, RequestOptions.DEFAULT);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 监听删除队列
     */
    @RabbitListener(queues = ItemMqConstants.DELETE_QUEUE_NAME)
    public void listenerDelete(Long id){
        try {
            DeleteRequest request = new DeleteRequest("test02_index",id.toString());
            client.delete(request,RequestOptions.DEFAULT);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
