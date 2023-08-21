package com.jwyao.system.config;

import com.alibaba.fastjson.JSON;
import com.jwyao.system.model.SearchPageResult;
import com.jwyao.system.model.SearchRequestParams;
import com.jwyao.system.model.ThingDoc;
import org.apache.http.HttpHost;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class ESConfig {

    @Value("${base_host}")
    private String host;

    /**
     * 注册RestHighLevelClient
     * @return
     */
    @Bean
    public RestHighLevelClient client(){
        return  new RestHighLevelClient(RestClient.builder(
                HttpHost.create("https://" + host + ":9200")
        ));
    }

    /**
     * 构建查询条件
     * @param params
     * @param request
     */
    public static void buildBasicQuery(SearchRequestParams params, SearchRequest request) {
        // 构建BooleanQuery
        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
        // 关键字搜索
        String key = params.getKey();
        if (key == null || "".equals(key)) {
            boolQuery.must(QueryBuilders.matchAllQuery());
        } else {
            boolQuery.must(QueryBuilders.matchQuery("all", key));
        }
        // 设置查询条件
        request.source().query(boolQuery);
    }

    /**
     * 解析响应
     * @param response
     * @return
     */
    public static SearchPageResult handleResponse(SearchResponse response) {
        // 解析响应
        SearchHits searchHits = response.getHits();
        long total = searchHits.getTotalHits().value;
        SearchHit[] hits = searchHits.getHits();
        List<ThingDoc> thingDocs = new ArrayList<>();
        for (SearchHit hit : hits) {
            // 获取文档source
            String json = hit.getSourceAsString();
            // 反序列化
            ThingDoc thingDoc = JSON.parseObject(json, ThingDoc.class);
            // 滤除下架商品
            if (thingDoc.getStatus() != 1) {
                thingDocs.add(thingDoc);
            }
        }
        return new SearchPageResult(total, thingDocs);
    }

}
