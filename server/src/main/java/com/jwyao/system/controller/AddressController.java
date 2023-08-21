package com.jwyao.system.controller;

import com.jwyao.system.common.APIResponse;
import com.jwyao.system.common.BaseContext;
import com.jwyao.system.common.ResponseCode;
import com.jwyao.system.entity.Address;
import com.jwyao.system.permission.Access;
import com.jwyao.system.permission.AccessLevel;
import com.jwyao.system.service.AddressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 地址管理
 */
@RestController
@RequestMapping("/address")
public class AddressController {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private AddressService addressService;

    @Cacheable(value = "addressList", key = "#userId")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public APIResponse list(Long userId) {
        List<Address> list =  addressService.getAddressList(userId);
        return new APIResponse(ResponseCode.SUCCESS, "查询成功", list);
    }

    @CacheEvict(value = "addressList", key = "#address.userId")
    @RequestMapping(value = "/create", method = RequestMethod.POST)
    @Transactional
    public APIResponse create(Address address) {
        if (address.getIsDefault() == 1){
            List<Address> list = addressService.getAddressList(address.getUserId());
            for (Address otherAddress: list) {
                otherAddress.setIsDefault(0);
                addressService.updateAddress(otherAddress);
            }
        }
        addressService.createAddress(address);
        return new APIResponse(ResponseCode.SUCCESS, "创建成功");
    }

    @Access(level = AccessLevel.LOGIN)
    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    public APIResponse delete(String ids) {
        // 删除缓存
        Long userId = BaseContext.getCurrentId();
        stringRedisTemplate.delete("addressList::" + userId);
        // 批量删除
        String[] arr = ids.split(",");
        for (String id : arr) {
            addressService.deleteAddress(id);
        }
        return new APIResponse(ResponseCode.SUCCESS, "删除成功");
    }

    @CacheEvict(value = "addressList", key = "#address.userId")
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    @Transactional
    public APIResponse update(Address address) {
        if (address.getIsDefault() == 1) {
            List<Address> list = addressService.getAddressList(address.getUserId());
            for (Address otherAddress: list) {
                otherAddress.setIsDefault(0);
                addressService.updateAddress(otherAddress);
            }
        }
        addressService.updateAddress(address);
        return new APIResponse(ResponseCode.SUCCESS, "更新成功");
    }

}
