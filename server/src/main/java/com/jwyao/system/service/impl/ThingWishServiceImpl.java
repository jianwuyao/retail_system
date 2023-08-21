package com.jwyao.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jwyao.system.mapper.ThingWishMapper;
import com.jwyao.system.service.ThingWishService;
import com.jwyao.system.entity.ThingWish;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
class ThingWishServiceImpl extends ServiceImpl<ThingWishMapper, ThingWish> implements ThingWishService {

    @Autowired
    private ThingWishMapper thingWishMapper;

    @Override
    public List<ThingWish> getUserWishList(Long userId) {
        return thingWishMapper.getUserWishList(userId);
    }

    @Override
    public void createThingWish(ThingWish thingWish) {
        thingWishMapper.insert(thingWish);;
    }

    @Override
    public void deleteThingWish(String id) {
        thingWishMapper.deleteById(id);
    }

    @Override
    public ThingWish getThingWish(Long userId, Long thingId) {
        QueryWrapper<ThingWish> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("thing_id", thingId)
                .eq("user_id", userId);
        return thingWishMapper.selectOne(queryWrapper);
    }
}
