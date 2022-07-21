package com.hmall.search.test;

import com.alibaba.fastjson.JSON;
import com.hmall.common.client.ItemClient;
import com.hmall.common.dto.PageDTO;
import com.hmall.common.pojo.Item;
import com.hmall.search.pojo.ItemDoc;
import org.apache.http.HttpHost;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.*;

@SpringBootTest
public class testSearch2 {
    ExecutorService poolExecutor = Executors.newFixedThreadPool(500);

    private RestHighLevelClient client;

    @Autowired
    private ItemClient itemClient;

    /**
     * 测试导入数据
     * @throws Exception
     */
    @Test
    public void page() throws Exception {
        PageDTO<Item> page = itemClient.findPage(1, 100);
        List<Item> list = page.getList();
        BulkRequest request = new BulkRequest("test02_index");
        for (Item item : list) {
            ItemDoc itemDoc = new ItemDoc(item);
            request.add(new IndexRequest("test02_index")
                    .id(item.getId().toString())
                    .source(JSON.toJSONString(itemDoc), XContentType.JSON));
        }
        client.bulk(request, RequestOptions.DEFAULT);
        Thread.sleep(5000);
        System.out.println(page);
    }


    /**
     * 导入索引库
     */
    @Test
    public void IndexTest(){
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(10,10,60,
                TimeUnit.SECONDS,new ArrayBlockingQueue<Runnable>(3),
                new ThreadPoolExecutor.DiscardOldestPolicy());
        int pageSize = 1000;
        Integer itemCount = 88476;

        int totalPageNum = (int)Math.ceil(itemCount/pageSize);
        for(int pageNum = 1; pageNum <= totalPageNum; pageNum++){
            PageDTO<Item> itemPageDTO = itemClient.findPage(pageNum, pageSize);
            if(itemPageDTO.getList() != null && itemPageDTO.getList().size() != 0){
                threadPoolExecutor.execute(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            BulkRequest request = new BulkRequest("test02_index");
                            for (Item item : itemPageDTO.getList()) {
                                ItemDoc itemDoc = new ItemDoc(item);
                                request.add(new IndexRequest("test02_index")
                                        .id(item.getId().toString())
                                        .source(JSON.toJSONString(itemDoc), XContentType.JSON));
                            }
                            client.bulk(request, RequestOptions.DEFAULT);
                        }catch (Exception ex){
                            throw new RuntimeException(ex);
                        }
                    }
                });
                try {
                    Thread.sleep(3000L);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }



    @BeforeEach
    void init(){
        client = new RestHighLevelClient(
                RestClient.builder(HttpHost.create("http://192.168.211.131:9200"))
        );
    }

    @AfterEach
    void after() throws IOException {
        client.close();
    }

}
