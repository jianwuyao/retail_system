package com.jwyao.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jwyao.system.entity.ThingCollect;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ThingCollectMapper extends BaseMapper<ThingCollect> {

    List<ThingCollect> getUserCollectList(Long userId);

    Integer getThingCollectCount(Long thingId);

}
