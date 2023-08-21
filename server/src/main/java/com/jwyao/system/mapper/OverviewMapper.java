package com.jwyao.system.mapper;

import org.apache.ibatis.annotations.MapKey;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface OverviewMapper {

    List<Object> getPopularThing();

    List<Object> getPopularClassification();

    List<Integer> getWebVisitData(@Param("day") String day);

}
