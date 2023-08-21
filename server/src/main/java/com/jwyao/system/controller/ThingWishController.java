package com.jwyao.system.controller;

import com.jwyao.system.common.APIResponse;
import com.jwyao.system.common.BaseContext;
import com.jwyao.system.common.ResponseCode;
import com.jwyao.system.entity.ThingWish;
import com.jwyao.system.permission.Access;
import com.jwyao.system.permission.AccessLevel;
import com.jwyao.system.service.ThingWishService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 商品-用户心愿单关联管理
 */
@RestController
@RequestMapping("/thingWish")
public class ThingWishController {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private ThingWishService thingWishService;

    @Access(level = AccessLevel.LOGIN)
    @RequestMapping(value = "/wish", method = RequestMethod.POST)
    public APIResponse wish(ThingWish thingWish) {
        // 删除缓存
        Long userId = BaseContext.getCurrentId();
        stringRedisTemplate.delete("userWishList::" + userId);
        if (thingWishService.getThingWish(thingWish.getUserId(), thingWish.getThingId()) != null){
            return new APIResponse(ResponseCode.SUCCESS, "您已添加过了");
        } else {
            thingWishService.createThingWish(thingWish);
        }
        return new APIResponse(ResponseCode.SUCCESS, "添加成功");
    }

    @Access(level = AccessLevel.LOGIN)
    @RequestMapping(value = "/unWish", method = RequestMethod.POST)
    public APIResponse unWish(String id) {
        // 删除缓存
        Long userId = BaseContext.getCurrentId();
        stringRedisTemplate.delete("userWishList::" + userId);
        thingWishService.deleteThingWish(id);
        return new APIResponse(ResponseCode.SUCCESS, "取消收藏成功");
    }

    @Cacheable(value = "userWishList", key = "#userId")
    @RequestMapping(value = "/getUserWishList", method = RequestMethod.GET)
    public APIResponse getUserWishList(Long userId) {
        List<ThingWish> lists = thingWishService.getUserWishList(userId);
        return new APIResponse(ResponseCode.SUCCESS, "获取成功", lists);
    }

}
