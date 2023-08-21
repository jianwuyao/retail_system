package com.jwyao.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jwyao.system.mapper.AdvertisementMapper;
import com.jwyao.system.service.AdvertisementService;
import com.jwyao.system.entity.Advertisement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdvertisementServiceImpl extends ServiceImpl<AdvertisementMapper, Advertisement> implements AdvertisementService {

    @Autowired
    private AdvertisementMapper advertisementMapper;

    @Override
    public List<Advertisement> getAdList() {
        return advertisementMapper.selectList(new QueryWrapper<>());
    }

    @Override
    public void createAd(Advertisement ad) {
        advertisementMapper.insert(ad);
    }

    @Override
    public void deleteAd(String id) {
        advertisementMapper.deleteById(id);
    }

    @Override
    public void updateAd(Advertisement ad) {
        advertisementMapper.updateById(ad);
    }

}
