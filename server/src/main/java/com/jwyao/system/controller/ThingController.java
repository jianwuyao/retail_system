package com.jwyao.system.controller;

import com.jwyao.system.common.APIResponse;
import com.jwyao.system.common.ResponseCode;
import com.jwyao.system.entity.Thing;
import com.jwyao.system.model.SearchPageResult;
import com.jwyao.system.model.SearchRequestParams;
import com.jwyao.system.service.ThingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

/**
 * 商品管理
 */
@RestController
@RequestMapping("/thing")
public class ThingController {

    @Value("${file.upload-path}")
    private String uploadPath;

    @Autowired
    private ThingService thingService;

    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public APIResponse list(String keyword, String sort, String classification, String tag, String backstage) {
        List<Thing> list = thingService.getThingList(keyword, sort, classification, tag, backstage);
        return new APIResponse(ResponseCode.SUCCESS, "查询成功", list);
    }

    @RequestMapping(value = "/search", method = RequestMethod.POST)
    public APIResponse search(SearchRequestParams params) {
        SearchPageResult result = thingService.searchThingList(params);
        return new APIResponse(ResponseCode.SUCCESS, "查询成功", result);
    }

    @RequestMapping(value = "/suggestion", method = RequestMethod.GET)
    public APIResponse getSuggestions(@RequestParam("key") String prefix) {
        List<String> suggestions = thingService.getSuggestions(prefix);
        return new APIResponse(ResponseCode.SUCCESS, "查询成功", suggestions);
    }

    @Cacheable(value = "thingDetail", key = "#id")
    @RequestMapping(value = "/detail", method = RequestMethod.GET)
    public APIResponse detail(Long id) {
        Thing thing = thingService.getThingById(id);
        return new APIResponse(ResponseCode.SUCCESS, "查询成功", thing);
    }

    @RequestMapping(value = "/create", method = RequestMethod.POST)
    @Transactional
    public APIResponse create(Thing thing) throws IOException {
        String url = saveThing(thing);
        if (!StringUtils.isEmpty(url)) {
            thing.cover = url;
        }
        if (thingService.createThing(thing)) {
            return new APIResponse(ResponseCode.SUCCESS, "创建成功");
        } else {
            return new APIResponse(ResponseCode.FAIL, "商品名称重复");
        }
    }

    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    @Transactional
    public APIResponse delete(String ids) {
        // 批量删除
        String[] arr = ids.split(",");
        for (String id : arr) {
            thingService.deleteThing(id);
        }
        return new APIResponse(ResponseCode.SUCCESS, "删除成功");
    }

    @CacheEvict(value = "thingDetail", key = "#thing.id")
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    @Transactional
    public APIResponse update(Thing thing) throws IOException {
        String url = saveThing(thing);
        if (!StringUtils.isEmpty(url)) {
            thing.cover = url;
        }
        thingService.updateThing(thing);
        return new APIResponse(ResponseCode.SUCCESS, "更新成功");
    }

    // 保存商品图片
    public String saveThing(Thing thing) throws IOException {
        MultipartFile file = thing.getImageFile();
        String newFileName = null;
        if (file != null && !file.isEmpty()) {
            String oldFileName = file.getOriginalFilename();
            String randomStr = UUID.randomUUID().toString();
            newFileName = randomStr + oldFileName.substring(oldFileName.lastIndexOf("."));
            String filePath = uploadPath + File.separator + "thing" + File.separator + newFileName;
            File destFile = new File(filePath);
            if(!destFile.getParentFile().exists()){
                destFile.getParentFile().mkdirs();
            }
            file.transferTo(destFile);
        }
        if (!StringUtils.isEmpty(newFileName)) {
            thing.cover = newFileName;
        }
        return newFileName;
    }

}
