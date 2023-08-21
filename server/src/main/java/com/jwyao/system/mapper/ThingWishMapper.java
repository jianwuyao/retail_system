package com.jwyao.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jwyao.system.entity.ThingWish;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ThingWishMapper extends BaseMapper<ThingWish> {

    List<ThingWish> getUserWishList(Long userId);

    Integer getThingWishCount(Long thingId);

}
