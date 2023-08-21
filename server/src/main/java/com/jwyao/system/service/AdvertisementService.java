package com.jwyao.system.service;

import com.jwyao.system.entity.Advertisement;

import java.util.List;

public interface AdvertisementService {

    List<Advertisement> getAdList();

    void createAd(Advertisement ad);

    void deleteAd(String id);

    void updateAd(Advertisement ad);

}
