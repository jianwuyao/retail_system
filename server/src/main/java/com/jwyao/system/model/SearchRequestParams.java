package com.jwyao.system.model;

import lombok.Data;

@Data
public class SearchRequestParams {

    private String key;

    private Integer page;

    private Integer size;

    private String sortBy;

}
