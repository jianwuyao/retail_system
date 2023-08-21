package com.jwyao.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jwyao.system.mapper.ThingCollectMapper;
import com.jwyao.system.service.ThingCollectService;
import com.jwyao.system.entity.ThingCollect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
class ThingCollectServiceImpl extends ServiceImpl<ThingCollectMapper, ThingCollect> implements ThingCollectService {

    @Autowired
    private ThingCollectMapper thingCollectMapper;

    @Override
    public List<ThingCollect> getUserCollectList(Long userId) {
        return thingCollectMapper.getUserCollectList(userId);
    }

    @Override
    public void createThingCollect(ThingCollect thingCollect) {
        thingCollectMapper.insert(thingCollect);
    }

    @Override
    public void deleteThingCollect(Long id) {
        thingCollectMapper.deleteById(id);
    }

    @Override
    public ThingCollect getThingCollect(Long userId, Long thingId) {
        QueryWrapper<ThingCollect> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("thing_id", thingId)
                .eq("user_id", userId);
        return thingCollectMapper.selectOne(queryWrapper);
    }

}
