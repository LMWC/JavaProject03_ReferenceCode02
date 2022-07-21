package com.hmall.search.service.Impl;

import com.alibaba.fastjson.JSON;
import com.hmall.common.dto.PageDTO;
import com.hmall.common.pojo.Item;
import com.hmall.search.pojo.ItemDoc;
import com.hmall.search.pojo.RequestParam;
import com.hmall.search.service.ClientService;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.lucene.search.function.CombineFunction;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.functionscore.FunctionScoreQueryBuilder;
import org.elasticsearch.index.query.functionscore.ScoreFunctionBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.sort.SortOrder;
import org.elasticsearch.search.suggest.Suggest;
import org.elasticsearch.search.suggest.SuggestBuilder;
import org.elasticsearch.search.suggest.SuggestBuilders;
import org.elasticsearch.search.suggest.completion.CompletionSuggestion;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ClientServiceImpl implements ClientService {

    @Autowired
    private RestHighLevelClient client;


    /**
     * 搜索数据
     * @param requestParam
     * @return
     */
    @Override
    public PageDTO<ItemDoc> list(RequestParam requestParam) {
        SearchRequest request = new SearchRequest("test02_index");
        try {
            getBoolQueryBuilder(requestParam,request);

            request.source().from(requestParam.getPage()).size(requestParam.getSize());
            SearchResponse respone = client.search(request,RequestOptions.DEFAULT);
            return pageDTO(respone,"name");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * 搜索过滤
     * @param requestParam
     * @return
     */
    @Override
    public Map<String, List<String>> filters(RequestParam requestParam) {
        try {
            SearchRequest request = new SearchRequest("test02_index");
            getBoolQueryBuilder(requestParam,request);
            request.source().size(0);
            request.source().aggregation(AggregationBuilders
                    .terms("categoryAgg")
                    .field("category")
                    .size(20));
            request.source().aggregation(AggregationBuilders
                    .terms("brandAgg")
                    .field("brand")
                    .size(20));
            SearchResponse response = client.search(request, RequestOptions.DEFAULT);
            Aggregations aggregations = response.getAggregations();
            List<String> getCategory = getList(aggregations,"categoryAgg");
            List<String> getBrand = getList(aggregations,"brandAgg");
            Map<String, List<String>> result = new HashMap<>();
            result.put("category",getCategory);
            result.put("brand",getBrand);
            return result;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 自动补全
     * @param prefix
     * @return
     */
    @Override
    public List<String> suggestion(String prefix) {
        try {
            SearchRequest request = new SearchRequest("test02_index");
            request.source().suggest(new SuggestBuilder().addSuggestion("Suggestions",
                    SuggestBuilders.completionSuggestion("suggestion")
                            .prefix(prefix)
                            .skipDuplicates(true)
                            .size(10)
                    ));
            SearchResponse response = client.search(request, RequestOptions.DEFAULT);
            Suggest suggest = response.getSuggest();
            CompletionSuggestion mySuggestion = suggest.getSuggestion("Suggestions");
            List<CompletionSuggestion.Entry.Option> options = mySuggestion.getOptions();
            List<String> list = new ArrayList<>(options.size());
            for (CompletionSuggestion.Entry.Option option : options) {
                Text text = option.getText();
                list.add(text.toString());
            }
            return list;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 提取桶数据
     * @param aggregations
     * @param name
     * @return
     */
    private List<String> getList(Aggregations aggregations, String name) {
        List<String> list = new ArrayList<>();
        Terms terms = aggregations.get(name);
        List<? extends Terms.Bucket> buckets = terms.getBuckets();
        for (Terms.Bucket bucket : buckets) {
            String keyAsString = bucket.getKeyAsString();
            list.add(keyAsString);
        }
        return list;
    }


    /**
     * 筛选条件
     * @param requestParam
     * @return
     */
    private void getBoolQueryBuilder(RequestParam requestParam,SearchRequest request) {
        BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder();
        if (!ObjectUtils.isEmpty(requestParam.getKey())) {
            boolQueryBuilder.must(QueryBuilders.matchQuery("name", requestParam.getKey()));
            request.source().highlighter(new HighlightBuilder()
                    .field("name")
                    .requireFieldMatch(false));
        } else {
            boolQueryBuilder.must(QueryBuilders.matchAllQuery());
        }
        if (!ObjectUtils.isEmpty(requestParam.getCategory())) {
            boolQueryBuilder.must(QueryBuilders.termQuery("category", requestParam.getCategory()));
        }
        if (!ObjectUtils.isEmpty(requestParam.getBrand())) {
            boolQueryBuilder.must(QueryBuilders.termQuery("brand", requestParam.getBrand()));
        }
        Integer maxPrice = requestParam.getMaxPrice();
        if (!ObjectUtils.isEmpty(maxPrice)) {
            boolQueryBuilder.filter(QueryBuilders.rangeQuery("price")
                    .gt(requestParam.getMinPrice() * 100)
                    .lte(requestParam.getMaxPrice() * 100));
        }
        if(requestParam.getSortBy().equals("price")){
            request.source().sort("price", SortOrder.ASC);
        } else if(requestParam.getSortBy().equals("sold")){
            request.source().sort("sold", SortOrder.DESC);
        }
        FunctionScoreQueryBuilder functionScoreQueryBuilder =
                QueryBuilders.functionScoreQuery(
                        boolQueryBuilder, new FunctionScoreQueryBuilder.FilterFunctionBuilder[]{
                                new FunctionScoreQueryBuilder.FilterFunctionBuilder(
                                        QueryBuilders.matchQuery("isAD", true),
                                        ScoreFunctionBuilders.weightFactorFunction(10)
                                )
                        }).boostMode(CombineFunction.MULTIPLY);
        request.source().query(functionScoreQueryBuilder);
    }

    /**
     * 解析结果
     * @param response
     * @return
     */
    public PageDTO<ItemDoc> pageDTO(SearchResponse response,String name){
        List<ItemDoc> list = new ArrayList<>();
        SearchHits hits = response.getHits();
        long total = hits.getTotalHits().value;

        for (SearchHit hit : hits.getHits()) {
            ItemDoc itemDoc = JSON.parseObject(hit.getSourceAsString(), ItemDoc.class);
            String result = highlightParse(hit.getHighlightFields(), name);
            if(!ObjectUtils.isEmpty(result)){
                itemDoc.setName(result);
            }
            list.add(itemDoc);
        }
        return new PageDTO<>(total,list);
    }

    /**
     * 获取高亮字段
     */
    public String highlightParse(Map<String, HighlightField>highlightFieldMap,String fireName){
        if(!CollectionUtils.isEmpty(highlightFieldMap)){
            HighlightField values = highlightFieldMap.get(fireName);
            if(values != null && fireName != null){
                Text[] fragments = values.getFragments();
                if(fragments != null && fragments.length > 0){
                    return fragments[0].toString();
                }
            }
        }
        return null;
    }
}
