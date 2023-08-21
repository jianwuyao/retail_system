package com.jwyao.system.task;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.jwyao.system.entity.Thing;
import com.jwyao.system.mapper.ThingMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@EnableScheduling
public class ScheduleTask {

    private static ThingMapper thingMapper;

    private static StringRedisTemplate stringRedisTemplate;

    @Autowired
    public void setThingMapper(ThingMapper thingMapper) {
        ScheduleTask.thingMapper = thingMapper;
    }

    @Autowired
    public void setStringRedisTemplate(StringRedisTemplate stringRedisTemplate) {
        ScheduleTask.stringRedisTemplate = stringRedisTemplate;
    }

    /**
     * 每两小时执行一次的定时任务
     */
    @Scheduled(cron = "0 0 */2 * * ?")
    public void cacheThingRepertoryScheduleTask() {
        log.info("执行定时任务...");
        List<Thing> things = thingMapper.selectList(new QueryWrapper<>());
        HashOperations<String, String, Integer> ops = stringRedisTemplate.opsForHash();
        for (Thing thing : things) {
            ops.put("thingRepertory", String.valueOf(thing.getId()), thing.getRepertory());
        }
    }

}
