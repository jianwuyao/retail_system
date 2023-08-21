package com.jwyao.system.controller;

import com.jwyao.system.common.APIResponse;
import com.jwyao.system.common.ResponseCode;
import com.jwyao.system.entity.Tag;
import com.jwyao.system.service.TagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 标签管理
 */
@RestController
@RequestMapping("/tag")
public class TagController {

    @Autowired
    private TagService tagService;

    @Cacheable(value = "tagList")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public APIResponse list() {
        List<Tag> list = tagService.getTagList();
        return new APIResponse(ResponseCode.SUCCESS, "查询成功", list);
    }

    @CacheEvict(value = "tagList", allEntries = true)
    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public APIResponse create(Tag tag) {
        if (tagService.createTag(tag)) {
            return new APIResponse(ResponseCode.SUCCESS, "创建成功");
        } else {
            return new APIResponse(ResponseCode.FAIL, "标签名称重复");
        }
    }

    @CacheEvict(value = "tagList", allEntries = true)
    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    public APIResponse delete(String ids) {
        // 批量删除
        String[] arr = ids.split(",");
        for (String id : arr) {
            tagService.deleteTag(id);
        }
        return new APIResponse(ResponseCode.SUCCESS, "删除成功");
    }

    @CacheEvict(value = "tagList", allEntries = true)
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public APIResponse update(Tag tag) {
        tagService.updateTag(tag);
        return new APIResponse(ResponseCode.SUCCESS, "更新成功");
    }

}
