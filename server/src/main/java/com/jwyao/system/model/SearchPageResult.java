package com.jwyao.system.model;

import lombok.Data;

import java.util.List;

@Data
public class SearchPageResult {

    private Long total;
    private List<ThingDoc> things;

    public SearchPageResult() {}

    public SearchPageResult(Long total, List<ThingDoc> things) {
        this.total = total;
        this.things = things;
    }

}
