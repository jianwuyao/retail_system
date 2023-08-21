package com.jwyao.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jwyao.system.entity.Address;
import com.jwyao.system.mapper.AddressMapper;
import com.jwyao.system.service.AddressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AddressServiceImpl extends ServiceImpl<AddressMapper, Address> implements AddressService {

    @Autowired
    private AddressMapper addressMapper;

    @Override
    public List<Address> getAddressList(Long userId) {
        QueryWrapper<Address> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);
        queryWrapper.orderBy(true, false, "create_time");
        return addressMapper.selectList(queryWrapper);
    }

    @Override
    public void createAddress(Address address) {
        addressMapper.insert(address);
    }

    @Override
    public void deleteAddress(String id) {
        addressMapper.deleteById(id);
    }

    @Override
    public void updateAddress(Address address) {
        addressMapper.updateById(address);
    }

}
