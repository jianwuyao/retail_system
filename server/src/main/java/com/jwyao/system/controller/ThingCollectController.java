package com.jwyao.system.controller;

import com.jwyao.system.common.APIResponse;
import com.jwyao.system.common.BaseContext;
import com.jwyao.system.common.ResponseCode;
import com.jwyao.system.entity.ThingCollect;
import com.jwyao.system.permission.Access;
import com.jwyao.system.permission.AccessLevel;
import com.jwyao.system.service.ThingCollectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 商品-用户收藏关联管理
 */
@RestController
@RequestMapping("/thingCollect")
public class ThingCollectController {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private ThingCollectService thingCollectService;

    @Access(level = AccessLevel.LOGIN)
    @RequestMapping(value = "/collect", method = RequestMethod.POST)
    public APIResponse collect(ThingCollect thingCollect) {
        // 删除缓存
        Long userId = BaseContext.getCurrentId();
        stringRedisTemplate.delete("userCollectList::" + userId);
        if (thingCollectService.getThingCollect(thingCollect.getUserId(), thingCollect.getThingId()) != null) {
            return new APIResponse(ResponseCode.SUCCESS, "您已收藏过了");
        } else {
            thingCollectService.createThingCollect(thingCollect);
        }
        return new APIResponse(ResponseCode.SUCCESS, "收藏成功");
    }

    @Access(level = AccessLevel.LOGIN)
    @RequestMapping(value = "/unCollect", method = RequestMethod.POST)
    public APIResponse unCollect(Long id) {
        // 删除缓存
        Long userId = BaseContext.getCurrentId();
        stringRedisTemplate.delete("userCollectList::" + userId);
        thingCollectService.deleteThingCollect(id);
        return new APIResponse(ResponseCode.SUCCESS, "取消收藏成功");
    }

    @Cacheable(value = "userCollectList", key = "#userId")
    @RequestMapping(value = "/getUserCollectList", method = RequestMethod.GET)
    public APIResponse getUserCollectList(Long userId) {
        List<ThingCollect> lists = thingCollectService.getUserCollectList(userId);
        return new APIResponse(ResponseCode.SUCCESS, "获取成功", lists);
    }

}
