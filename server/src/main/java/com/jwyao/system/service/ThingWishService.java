package com.jwyao.system.service;

import com.jwyao.system.entity.ThingWish;

import java.util.List;
import java.util.Map;

public interface ThingWishService {

    List<ThingWish> getUserWishList(Long userId);

    void createThingWish(ThingWish thingWish);

    void deleteThingWish(String id);

    ThingWish getThingWish(Long userId, Long thingId);

}
