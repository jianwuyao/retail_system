package com.jwyao.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jwyao.system.entity.ShoppingCart;
import com.jwyao.system.mapper.ShoppingCartMapper;
import com.jwyao.system.service.ShoppingCartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ShoppingCartServiceImpl extends ServiceImpl<ShoppingCartMapper, ShoppingCart> implements ShoppingCartService {

    @Autowired
    private ShoppingCartMapper shoppingCartMapper;

    @Override
    public List<ShoppingCart> getShoppingCartList(Long userId) {
        QueryWrapper<ShoppingCart> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);
        shoppingCartMapper.selectList(queryWrapper);
        return shoppingCartMapper.selectList(queryWrapper);
    }

    @Override
    public void deleteShoppingCart(Long id) {
        shoppingCartMapper.deleteById(id);
    }

    @Override
    public void deleteShoppingCartByUserId(Long userId) {
        QueryWrapper<ShoppingCart> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);
        shoppingCartMapper.delete(queryWrapper);
    }

    @Override
    public void addShoppingCart(ShoppingCart shoppingCart) {
        QueryWrapper<ShoppingCart> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", shoppingCart.getUserId());
        queryWrapper.eq("thing_id", shoppingCart.getThingId());
        ShoppingCart cart = shoppingCartMapper.selectOne(queryWrapper);
        if (cart != null) {
            cart.setCount(cart.getCount() + 1);
            shoppingCartMapper.updateById(cart);
        } else {
            shoppingCart.setCount(1);
            shoppingCartMapper.insert(shoppingCart);
        }
    }

    @Override
    public void updateShoppingCart(ShoppingCart shoppingCart) {
        shoppingCartMapper.updateById(shoppingCart);
    }

}
