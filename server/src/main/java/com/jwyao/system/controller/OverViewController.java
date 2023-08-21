package com.jwyao.system.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.jwyao.system.common.APIResponse;
import com.jwyao.system.common.ResponseCode;
import com.jwyao.system.entity.Order;
import com.jwyao.system.entity.Thing;
import com.jwyao.system.mapper.OrderMapper;
import com.jwyao.system.mapper.OverviewMapper;
import com.jwyao.system.mapper.ThingMapper;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 统计分析
 */
@RestController
@RequestMapping("/overview")
public class OverViewController {

    @Autowired
    private ThingMapper thingMapper;

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private OverviewMapper overviewMapper;

    @RequestMapping(value = "/count", method = RequestMethod.GET)
    public APIResponse count() {
        Map<String, Object> map = new HashMap<>();
        // 商品总数
        QueryWrapper<Thing> queryWrapper = new QueryWrapper<>();
        map.put("thingSum", thingMapper.selectCount(queryWrapper));
        // 七日新增
        long now = System.currentTimeMillis();
        long sevenMillis = now - 7 * 24 * 60 * 60 * 1000;
        queryWrapper.ge("create_time", sevenMillis);
        map.put("weekIncreased", thingMapper.selectCount(queryWrapper));
        // 未付订单
        QueryWrapper<Order> orderQueryWrapper = new QueryWrapper<>();
        orderQueryWrapper.eq("status", "1");
        map.put("unPaid", orderMapper.selectCount(orderQueryWrapper));
        // 未付订单人数
        orderQueryWrapper = new QueryWrapper<>();
        orderQueryWrapper.select("distinct user_id");
        orderQueryWrapper.eq("status", "1");
        orderQueryWrapper.groupBy("user_id");
        map.put("unPaidPeople", orderMapper.selectList(orderQueryWrapper).size());
        // 已付订单
        orderQueryWrapper = new QueryWrapper<>();
        orderQueryWrapper.eq("status", "2");
        map.put("paid", orderMapper.selectCount(orderQueryWrapper));
        // 已付订单人数
        orderQueryWrapper = new QueryWrapper<>();
        orderQueryWrapper.select("distinct user_id");
        orderQueryWrapper.eq("status", "2");
        orderQueryWrapper.groupBy("user_id");
        map.put("paidPeople", orderMapper.selectList(orderQueryWrapper).size());
        // 取消订单
        orderQueryWrapper = new QueryWrapper<>();
        orderQueryWrapper.eq("status", "7");
        map.put("cancel", orderMapper.selectCount(orderQueryWrapper));
        // 取消订单人数
        orderQueryWrapper = new QueryWrapper<>();
        orderQueryWrapper.select("distinct user_id");
        orderQueryWrapper.eq("status", "7");
        orderQueryWrapper.groupBy("user_id");
        map.put("cancelPeople", orderMapper.selectList(orderQueryWrapper).size());
        // 网站流量
        List<Object> visitList = new ArrayList<>();
        List<String> sevenList = getSevenDate();
        for (String day : sevenList) {
            Map<String, String> visitMap = new HashMap<>();
            visitMap.put("day", day);
            List<Integer> webVisitData = overviewMapper.getWebVisitData(day);
            int pv = webVisitData.stream().mapToInt(Integer::intValue).sum();
            int uv = webVisitData.size();
            visitMap.put("pv", String.valueOf(pv));
            visitMap.put("uv", String.valueOf(uv));
            visitList.add(visitMap);
        }
        map.put("visitList", visitList);
        // 热门商品
        List<Object> popularThing = overviewMapper.getPopularThing();
        map.put("popularThing", popularThing);
        // 热门分类
        List<Object> popularClassification = overviewMapper.getPopularClassification();
        map.put("popularClassification", popularClassification);
        return new APIResponse(ResponseCode.SUCCESS, "查询成功", map);
    }

    public static List<String> getSevenDate() {
        List<String> dateList = new ArrayList<>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        for (int i = 0; i < 7; i++) {
            Date date = DateUtils.addDays(new Date(), -i);
            String formatDate = sdf.format(date);
            dateList.add(formatDate);
        }
        Collections.reverse(dateList);
        return dateList;
    }

}
