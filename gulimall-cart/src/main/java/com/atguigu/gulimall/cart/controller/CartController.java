package com.atguigu.gulimall.cart.controller;

import com.atguigu.common.constant.AuthServerConstant;
import com.atguigu.gulimall.cart.interceptor.CartInterceptor;
import com.atguigu.gulimall.cart.to.UserInfoTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpSession;

/**
 * CartController
 *
 * @author fj
 * @date 2023/1/7 20:30
 */
@Controller
public class CartController {

    @GetMapping("/cart.html")
    public String cartListPage(HttpSession session){
        //快速得到用户信息 id user-key
        UserInfoTo userInfoTo = CartInterceptor.threadLocal.get();
        return "cartList";
    }

    /**
     * 添加商品到购物车
     * @param session
     * @return
     */
    @GetMapping("/addToCart")
    public String addToCart(HttpSession session){
        return "success";
    }

}
