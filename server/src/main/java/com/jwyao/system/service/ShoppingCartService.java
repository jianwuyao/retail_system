package com.jwyao.system.service;

import com.jwyao.system.entity.ShoppingCart;

import java.util.List;

public interface ShoppingCartService {

    List<ShoppingCart> getShoppingCartList(Long userId);

    void deleteShoppingCart(Long id);

    void updateShoppingCart(ShoppingCart shoppingCart);

    void deleteShoppingCartByUserId(Long userId);

    void addShoppingCart(ShoppingCart shoppingCart);

}
