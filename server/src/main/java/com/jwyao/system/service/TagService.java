package com.jwyao.system.service;

import com.jwyao.system.entity.Tag;

import java.util.List;

public interface TagService {

    List<Tag> getTagList();

    boolean createTag(Tag tag);

    void deleteTag(String id);

    void updateTag(Tag tag);

}
