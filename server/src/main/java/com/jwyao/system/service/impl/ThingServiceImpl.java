package com.jwyao.system.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jwyao.system.config.ESConfig;
import com.jwyao.system.entity.Classification;
import com.jwyao.system.entity.Thing;
import com.jwyao.system.entity.ThingTag;
import com.jwyao.system.mapper.*;
import com.jwyao.system.model.SearchPageResult;
import com.jwyao.system.model.SearchRequestParams;
import com.jwyao.system.model.ThingDoc;
import com.jwyao.system.service.ThingService;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.search.sort.SortOrder;
import org.elasticsearch.search.suggest.Suggest;
import org.elasticsearch.search.suggest.SuggestBuilder;
import org.elasticsearch.search.suggest.SuggestBuilders;
import org.elasticsearch.search.suggest.completion.CompletionSuggestion;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ThingServiceImpl extends ServiceImpl<ThingMapper, Thing> implements ThingService {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private RestHighLevelClient client;

    @Autowired
    private ThingMapper thingMapper;

    @Autowired
    private ThingTagMapper thingTagMapper;

    @Autowired
    private ClassificationMapper classificationMapper;

    @Autowired
    private CommentMapper commentMapper;

    @Autowired
    private ThingWishMapper thingWishMapper;

    @Autowired
    private ThingCollectMapper thingCollectMapper;

    @Override
    public List<Thing> getThingList(String keyword, String sort, String classification, String tag, String backstage) {
        QueryWrapper<Thing> queryWrapper = new QueryWrapper<>();
        // 根据分类筛选
        if (StringUtils.isNotBlank(classification) && !classification.equals("-1")) {
            queryWrapper.eq(true, "classification_id", classification);
        }
        // 前台只显示上架商品
        if (StringUtils.isBlank(backstage) || !"8848".equals(backstage)) {
            queryWrapper.eq(true, "status", 0);
        }
        // 搜索
        queryWrapper.like(StringUtils.isNotBlank(keyword), "title", keyword);
        // 默认按时间排序
        queryWrapper.orderBy(true, false, "create_time");
        List<Thing> things = thingMapper.selectList(queryWrapper);
        // 其他排序
        List<Thing> sortedThings;
        if (StringUtils.isNotBlank(sort) && sort.equals("hot")) {
            sortedThings = things.stream().sorted(Comparator.comparing(Thing::getBrowseCount).reversed()).collect(Collectors.toList());
        } else if (StringUtils.isNotBlank(sort) && sort.equals("recommend")) {
            sortedThings = things.stream().peek(thing -> thing.setRecommendCount(commentMapper.getThingCommentCount(thing.getId())))
                    .sorted(Comparator.comparing(Thing::getRecommendCount).reversed())
                    .collect(Collectors.toList());
        } else {
            sortedThings = things;
        }
        // 筛选tag
        if (StringUtils.isNotBlank(tag)) {
            List<Thing> tThings = new ArrayList<>();
            QueryWrapper<ThingTag> thingTagQueryWrapper = new QueryWrapper<>();
            thingTagQueryWrapper.eq("tag_id", tag);
            List<ThingTag> thingTagList = thingTagMapper.selectList(thingTagQueryWrapper);
            for (Thing thing : sortedThings) {
                for (ThingTag thingTag : thingTagList) {
                    if (thing.getId().equals(thingTag.getThingId())) {
                        tThings.add(thing);
                    }
                }
            }
            sortedThings.clear();
            sortedThings.addAll(tThings);
        }
        // 附加tag
        for (Thing thing : sortedThings) {
            QueryWrapper<ThingTag> thingTagQueryWrapper = new QueryWrapper<>();
            thingTagQueryWrapper.lambda().eq(ThingTag::getThingId, thing.getId());
            List<ThingTag> thingTags = thingTagMapper.selectList(thingTagQueryWrapper);
            List<Long> tags = thingTags.stream().map(ThingTag::getTagId).collect(Collectors.toList());
            thing.setTags(tags);
        }
        return sortedThings;
    }

    @Override
    public SearchPageResult searchThingList(SearchRequestParams params) {
        try {
            // 1.准备Request
            SearchRequest request = new SearchRequest("thing");
            // 2.准备DSL语句
            ESConfig.buildBasicQuery(params, request);
            // 2.1分页
            int page = params.getPage();
            int size = params.getSize();
            request.source().from((page - 1) * size).size(size);
            // 2.2排序
            String sortBy = params.getSortBy();
            if (sortBy != null) {
                if (sortBy.equals("price")) {
                    request.source().sort("price", SortOrder.ASC);
                } else if (sortBy.equals("browse_count")) {
                    request.source().sort("browse_count", SortOrder.DESC);
                }
            }
            // 3.发送请求
            SearchResponse response = client.search(request, RequestOptions.DEFAULT);
            // 4.解析响应
            return ESConfig.handleResponse(response);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<String> getSuggestions(String prefix) {
        try {
            // 1.准备Request
            SearchRequest request = new SearchRequest("thing");
            // 2.准备DSL语句
            request.source().suggest(new SuggestBuilder().addSuggestion(
                    "suggestions",
                    SuggestBuilders.completionSuggestion("suggestion")
                            .prefix(prefix).skipDuplicates(true).size(5)
            ));
            // 3.发起请求
            SearchResponse response = client.search(request, RequestOptions.DEFAULT);
            // 4.解析结果
            Suggest suggest = response.getSuggest();
            // 4.1.根据补全查询名称，获取补全结果
            CompletionSuggestion suggestions = suggest.getSuggestion("suggestions");
            // 4.2.获取options
            List<CompletionSuggestion.Entry.Option> options = suggestions.getOptions();
            // 4.3.遍历
            List<String> list = new ArrayList<>(options.size());
            for (CompletionSuggestion.Entry.Option option : options) {
                String text = option.getText().toString();
                list.add(text);
            }
            return list;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean createThing(Thing thing) {
        Map<String, Object> map = new HashMap<>();
        map.put("title", thing.getTitle());
        List<Thing> things = thingMapper.selectByMap(map);
        if (things != null && things.size() != 0) {
            return false;
        }
        if (thing.getBrowseCount() == null) {
            thing.setBrowseCount(0);
        }
        thingMapper.insert(thing);
        addThingTags(thing);
        updateES(thing);
        return true;
    }

    @CacheEvict(value = "thingDetail", key = "#id")
    @Override
    public void deleteThing(String id) {
        thingMapper.deleteById(id);
        deleteThingTags(Long.valueOf(id));
        deleteES(id);
    }

    @Override
    public void updateThing(Thing thing) {
        thingMapper.updateById(thing);
        HashOperations<String, String, String> ops = stringRedisTemplate.opsForHash();
        ops.put("thingRepertory", String.valueOf(thing.getId()), String.valueOf(thing.getRepertory()));
        if (thing.getTags() != null) {
            deleteThingTags(thing.getId());
            addThingTags(thing);
        }
        updateES(thing);
    }

    @Override
    public Thing getThingById(Long id) {
        Thing thing = thingMapper.selectById(id);
        // 设置浏览数+1
        thing.setBrowseCount(thing.getBrowseCount() + 1);
        thingMapper.updateById(thing);
        // 设置评论数
        thing.setRecommendCount(commentMapper.getThingCommentCount(id));
        // 设置心愿数
        thing.setWishCount(thingWishMapper.getThingWishCount(id));
        // 设置收藏数
        thing.setCollectCount(thingCollectMapper.getThingCollectCount(id));
        // 设置分类
        Classification classification = classificationMapper.selectById(thing.getClassificationId());
        thing.setClassification(classification.getTitle());
        return thing;
    }

    public void addThingTags(Thing thing) {
        // 新增tag
        if (thing.getTags() != null) {
            for (Long tag : thing.getTags()) {
                ThingTag thingTag = new ThingTag();
                thingTag.setThingId(thing.getId());
                thingTag.setTagId(tag);
                thingTagMapper.insert(thingTag);
            }
        }
    }

    public void deleteThingTags(Long thingId) {
        // 删除tag
        Map<String, Object> map = new HashMap<>();
        map.put("thing_id", thingId);
        thingTagMapper.deleteByMap(map);
    }

    public void updateES(Thing thing) {
        try {
            ThingDoc thingDoc = new ThingDoc(thing);
            IndexRequest request = new IndexRequest("thing").id(thingDoc.getId().toString());
            request.source(JSON.toJSONString(thingDoc), XContentType.JSON);
            client.index(request, RequestOptions.DEFAULT);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void deleteES(String thingId) {
        try {
            DeleteRequest request = new DeleteRequest("thing", thingId);
            client.delete(request, RequestOptions.DEFAULT);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
