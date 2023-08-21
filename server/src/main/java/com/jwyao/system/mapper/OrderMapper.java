package com.jwyao.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jwyao.system.entity.Order;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface OrderMapper extends BaseMapper<Order> {

    List<Order> getList();

    List<Order> getUserOrderList(Long userId, String status);

}
