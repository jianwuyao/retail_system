package com.jwyao.system.service;

import com.jwyao.system.entity.Thing;
import com.jwyao.system.model.SearchPageResult;
import com.jwyao.system.model.SearchRequestParams;

import java.util.List;

public interface ThingService {

    List<Thing> getThingList(String keyword, String sort, String classification, String tag, String backstage);

    SearchPageResult searchThingList(SearchRequestParams params);

    List<String> getSuggestions(String prefix);

    boolean createThing(Thing thing);

    void deleteThing(String id);

    void updateThing(Thing thing);

    Thing getThingById(Long id);

}
