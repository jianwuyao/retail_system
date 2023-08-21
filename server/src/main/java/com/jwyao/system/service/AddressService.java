package com.jwyao.system.service;

import com.jwyao.system.entity.Address;

import java.util.List;

public interface AddressService {

    List<Address> getAddressList(Long userId);

    void createAddress(Address address);

    void deleteAddress(String id);

    void updateAddress(Address address);

}
