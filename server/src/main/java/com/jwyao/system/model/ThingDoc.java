package com.jwyao.system.model;

import com.jwyao.system.entity.Thing;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 商品-基于ES文档的实体类
 */
@Data
@NoArgsConstructor
public class ThingDoc implements Serializable {

    private static final long serialVersionUID = 1L;

    public Long id;
    
    public String title;
    
    public String cover;
    
    public String description;
    
    public Double price;
    
    public Integer status;

    public Integer repertory;

    public Integer browseCount;

    public Long classificationId;

    public String suggestion;

    public ThingDoc(Thing thing) {

        this.id = thing.getId();

        this.title = thing.getTitle();

        this.cover = thing.getCover();

        this.description = thing.getDescription();

        this.price = Double.parseDouble(thing.getPrice());

        this.status = thing.getStatus();

        this.repertory = thing.getRepertory();

        this.browseCount = thing.getBrowseCount();

        this.classificationId = thing.getClassificationId();

        this.suggestion = this.title;

    }

}
