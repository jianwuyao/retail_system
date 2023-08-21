package com.jwyao.system.controller;

import com.jwyao.system.common.APIResponse;
import com.jwyao.system.common.ResponseCode;
import com.jwyao.system.entity.Classification;
import com.jwyao.system.service.ClassificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;

/**
 * 商品分类管理
 */
@RestController
@RequestMapping("/classification")
public class ClassificationController {

    @Autowired
    private ClassificationService classificationService;

    @Cacheable(value = "classificationList")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public APIResponse list() {
        List<Classification> list = classificationService.getClassificationList();
        return new APIResponse(ResponseCode.SUCCESS, "查询成功", list);
    }

    @CacheEvict(value = "classificationList", allEntries = true)
    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public APIResponse create(Classification classification) throws IOException {
        if (classificationService.createClassification(classification)) {
            return new APIResponse(ResponseCode.SUCCESS, "创建成功");
        } else {
            return new APIResponse(ResponseCode.FAIL, "分类名称重复");
        }
    }

    @CacheEvict(value = "classificationList", allEntries = true)
    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    public APIResponse delete(String ids) {
        // 批量删除
        String[] arr = ids.split(",");
        for (String id : arr) {
            classificationService.deleteClassification(id);
        }
        return new APIResponse(ResponseCode.SUCCESS, "删除成功");
    }

    @CacheEvict(value = "classificationList", allEntries = true)
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public APIResponse update(Classification classification) throws IOException {
        classificationService.updateClassification(classification);
        return new APIResponse(ResponseCode.SUCCESS, "更新成功");
    }

}
