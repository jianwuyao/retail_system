package com.jwyao.system;

import com.alibaba.fastjson.JSON;
import com.jwyao.system.entity.Thing;
import com.jwyao.system.model.ThingDoc;
import com.jwyao.system.service.ThingService;
import org.apache.http.HttpHost;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.util.List;

@SpringBootTest
public class ThingDocumentTest {

    private RestHighLevelClient client;

    @Autowired
    private ThingService thingService;

    @BeforeEach
    void setUp() {
        this.client = new RestHighLevelClient(RestClient.builder(
                HttpHost.create("http://192.168.100.100:9200")
        ));
    }

    @AfterEach
    void tearDown() throws IOException {
        this.client.close();
    }

    @Test
    void testAddDocument() throws IOException {
        // 1.根据id查询商品数据
        Thing thing = thingService.getThingById(1692825339107528705L);
        // 2.转换为文档类型
        ThingDoc thingDoc = new ThingDoc(thing);
        // 3.将ThingDoc转Json
        String json = JSON.toJSONString(thingDoc);
        // 4.准备Request对象
        IndexRequest request = new IndexRequest("thing").id(thingDoc.getId().toString());
        // 5.准备Json文档
        request.source(json, XContentType.JSON);
        // 6.发送请求
        client.index(request, RequestOptions.DEFAULT);
    }

    @Test
    void testGetDocumentById() throws IOException {
        // 1.准备Request
        GetRequest request = new GetRequest("thing", "1692825339107528705");
        // 2.发送请求，得到响应
        GetResponse response = client.get(request, RequestOptions.DEFAULT);
        // 3.解析响应结果
        String json = response.getSourceAsString();
        ThingDoc thingDoc = JSON.parseObject(json, ThingDoc.class);
    }

    @Test
    void testDeleteDocument() throws IOException {
        // 1.准备Request
        DeleteRequest request = new DeleteRequest("thing", "1692825339107528705");
        // 2.发送请求
        client.delete(request, RequestOptions.DEFAULT);
    }

    @Test
    void testUpdateDocument() throws IOException {
        // 1.准备Request
        UpdateRequest request = new UpdateRequest("thing", "1692825339107528705");
        // 2.准备请求参数
        request.doc("repertory", "130");
        // 3.发送请求
        client.update(request, RequestOptions.DEFAULT);
    }

    @Test
    void testBulkRequest() throws IOException {
        List<Thing> things = thingService.getThingList("", "", "", "", "");
        // 1.创建Request
        BulkRequest request = new BulkRequest();
        // 2.准备参数，添加多个新增的Request
        for (Thing thing : things) {
            // 2.1.转换为文档类型ThingDoc
            ThingDoc thingDoc = new ThingDoc(thing);
            // 2.2.创建新增文档的Request对象
            request.add(new IndexRequest("thing")
                    .id(thingDoc.getId().toString())
                    .source(JSON.toJSONString(thingDoc), XContentType.JSON));
        }
        // 3.发送请求
        client.bulk(request, RequestOptions.DEFAULT);
    }

    @Test
    public void testMatchAll() throws IOException {
        // 1.准备Request
        SearchRequest request = new SearchRequest("thing");
        // 2.准备DSL
        request.source().query(QueryBuilders.matchAllQuery());
        // 3.发送请求
        SearchResponse response = client.search(request, RequestOptions.DEFAULT);
        // 4.解析response
        SearchHits searchHits = response.getHits();
        long total = searchHits.getTotalHits().value;
        SearchHit[] hits = searchHits.getHits();
        for (SearchHit hit : hits) {
            // 获取文档source
            String json = hit.getSourceAsString();
            // 反序列化
            ThingDoc thingDoc = JSON.parseObject(json, ThingDoc.class);
        }
    }

}
