package com.jwyao.system.controller;

import com.jwyao.system.common.APIResponse;
import com.jwyao.system.common.ResponseCode;
import com.jwyao.system.entity.ShoppingCart;
import com.jwyao.system.service.ShoppingCartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 购物车管理
 */
@RestController
@RequestMapping("/shoppingCart")
public class ShoppingCartController {

    @Autowired
    private ShoppingCartService shoppingCartService;

    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public APIResponse list(Long userId) {
        List<ShoppingCart> list = shoppingCartService.getShoppingCartList(userId);
        return new APIResponse(ResponseCode.SUCCESS, "查询成功", list);
    }

    @RequestMapping(value = "/deleteById", method = RequestMethod.POST)
    public APIResponse deleteById(Long id) {
        shoppingCartService.deleteShoppingCart(id);
        return new APIResponse(ResponseCode.SUCCESS, "删除成功");
    }

    @RequestMapping(value = "/deleteByUserId", method = RequestMethod.POST)
    public APIResponse deleteByUserId(Long userId) {
        shoppingCartService.deleteShoppingCartByUserId(userId);
        return new APIResponse(ResponseCode.SUCCESS, "删除成功");
    }

    @RequestMapping(value = "/updateCount", method = RequestMethod.POST)
    public APIResponse updateCount(ShoppingCart shoppingCart) {
        shoppingCartService.updateShoppingCart(shoppingCart);
        return new APIResponse(ResponseCode.SUCCESS, "更新成功");
    }

    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public APIResponse add(ShoppingCart shoppingCart) {
        shoppingCartService.addShoppingCart(shoppingCart);
        return new APIResponse(ResponseCode.SUCCESS, "添加成功");
    }

}
