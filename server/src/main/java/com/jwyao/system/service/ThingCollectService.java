package com.jwyao.system.service;

import com.jwyao.system.entity.Thing;
import com.jwyao.system.entity.ThingCollect;

import java.util.List;

public interface ThingCollectService {

    List<ThingCollect> getUserCollectList(Long userId);

    void createThingCollect(ThingCollect thingCollect);

    void deleteThingCollect(Long id);

    ThingCollect getThingCollect(Long userId, Long thingId);

}
