package com.jwyao.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jwyao.system.mapper.ClassificationMapper;
import com.jwyao.system.entity.Classification;
import com.jwyao.system.service.ClassificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ClassificationServiceImpl extends ServiceImpl<ClassificationMapper, Classification> implements ClassificationService {

    @Autowired
    private ClassificationMapper classificationMapper;

    @Override
    public List<Classification> getClassificationList() {
        return classificationMapper.selectList(new QueryWrapper<>());
    }

    @Override
    public boolean createClassification(Classification classification) {
        Map<String, Object> map = new HashMap<>();
        map.put("title", classification.getTitle());
        List<Classification> classifications = classificationMapper.selectByMap(map);
        if (classifications != null && classifications.size() != 0) {
            return false;
        }
        classificationMapper.insert(classification);
        return true;
    }

    @Override
    public void deleteClassification(String id) {
        classificationMapper.deleteById(id);
    }

    @Override
    public void updateClassification(Classification classification) {
        classificationMapper.updateById(classification);
    }

}
