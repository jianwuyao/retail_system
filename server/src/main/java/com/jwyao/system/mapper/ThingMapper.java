package com.jwyao.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jwyao.system.entity.Thing;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ThingMapper extends BaseMapper<Thing> {

    void updateThingRepertory(Long thingId, Integer count);

}
