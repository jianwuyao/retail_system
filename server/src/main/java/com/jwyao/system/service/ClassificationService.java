package com.jwyao.system.service;

import com.jwyao.system.entity.Classification;

import java.util.List;

public interface ClassificationService {

    List<Classification> getClassificationList();

    boolean createClassification(Classification Classification);

    void deleteClassification(String id);

    void updateClassification(Classification Classification);

}
