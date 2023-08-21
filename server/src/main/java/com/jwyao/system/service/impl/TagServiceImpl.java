package com.jwyao.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jwyao.system.mapper.TagMapper;
import com.jwyao.system.entity.Tag;
import com.jwyao.system.service.TagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class TagServiceImpl extends ServiceImpl<TagMapper, Tag> implements TagService {

    @Autowired
    private TagMapper tagMapper;

    @Override
    public List<Tag> getTagList() {
        return tagMapper.selectList(new QueryWrapper<>());
    }

    @Override
    public boolean createTag(Tag tag) {
        Map<String, Object> map = new HashMap<>();
        map.put("title", tag.getTitle());
        List<Tag> tags = tagMapper.selectByMap(map);
        if (tags != null && tags.size() != 0) {
            return false;
        }
        tagMapper.insert(tag);
        return true;
    }

    @Override
    public void deleteTag(String id) {
        tagMapper.deleteById(id);
    }

    @Override
    public void updateTag(Tag tag) {
        tagMapper.updateById(tag);
    }

}
