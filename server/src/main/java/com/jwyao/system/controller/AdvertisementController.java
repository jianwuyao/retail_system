package com.jwyao.system.controller;

import com.jwyao.system.common.APIResponse;
import com.jwyao.system.common.ResponseCode;
import com.jwyao.system.entity.Advertisement;
import com.jwyao.system.service.AdvertisementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

/**
 * 广告管理
 */
@RestController
@RequestMapping("/advertisement")
public class AdvertisementController {

    @Value("${file.upload-path}")
    private String uploadPath;

    @Autowired
    private AdvertisementService advertisementService;

    @Cacheable(value = "advertisementList")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public APIResponse list() {
        List<Advertisement> list = advertisementService.getAdList();
        return new APIResponse(ResponseCode.SUCCESS, "查询成功", list);
    }

    @CacheEvict(value = "advertisementList", allEntries = true)
    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public APIResponse create(Advertisement advertisement) throws IOException {
        String image = saveAd(advertisement);
        if (!StringUtils.isEmpty(image)) {
            advertisement.image = image;
        }
        advertisementService.createAd(advertisement);
        return new APIResponse(ResponseCode.SUCCESS, "创建成功");
    }

    @CacheEvict(value = "advertisementList", allEntries = true)
    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    public APIResponse delete(String ids) {
        // 批量删除
        String[] arr = ids.split(",");
        for (String id : arr) {
            advertisementService.deleteAd(id);
        }
        return new APIResponse(ResponseCode.SUCCESS, "删除成功");
    }

    @CacheEvict(value = "advertisementList", allEntries = true)
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public APIResponse update(Advertisement advertisement) throws IOException {
        String image = saveAd(advertisement);
        if (!StringUtils.isEmpty(image)) {
            advertisement.image = image;
        }
        advertisementService.updateAd(advertisement);
        return new APIResponse(ResponseCode.SUCCESS, "更新成功");
    }

    // 保存广告图片
    public String saveAd(Advertisement advertisement) throws IOException {
        MultipartFile file = advertisement.getImageFile();
        String newFileName = null;
        if (file != null && !file.isEmpty()) {
            String oldFileName = file.getOriginalFilename();
            String randomStr = UUID.randomUUID().toString();
            newFileName = randomStr + oldFileName.substring(oldFileName.lastIndexOf("."));
            String filePath = uploadPath + File.separator + "advertisement" + File.separator + newFileName;
            File destFile = new File(filePath);
            if (!destFile.getParentFile().exists()){
                destFile.getParentFile().mkdirs();
            }
            file.transferTo(destFile);
        }
        if (!StringUtils.isEmpty(newFileName)) {
            advertisement.image = newFileName;
        }
        return newFileName;
    }

}
