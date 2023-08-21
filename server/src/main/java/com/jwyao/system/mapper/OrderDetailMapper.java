package com.jwyao.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jwyao.system.entity.OrderDetail;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface OrderDetailMapper extends BaseMapper<OrderDetail> {

    void batchInsert(List<OrderDetail> orderDetails);

}
