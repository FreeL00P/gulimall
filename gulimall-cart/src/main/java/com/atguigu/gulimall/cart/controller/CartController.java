package com.atguigu.gulimall.cart.controller;

import com.atguigu.common.constant.AuthServerConstant;
import com.atguigu.gulimall.cart.interceptor.CartInterceptor;
import com.atguigu.gulimall.cart.service.CartService;
import com.atguigu.gulimall.cart.to.UserInfoTo;
import com.atguigu.gulimall.cart.vo.CartItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpSession;
import java.util.concurrent.ExecutionException;

/**
 * CartController
 *
 * @author fj
 * @date 2023/1/7 20:30
 */
@Controller
public class CartController {

    @Autowired
    CartService cartService;

    @GetMapping("/cart.html")
    public String cartListPage(HttpSession session){
        //快速得到用户信息 id user-key
        UserInfoTo userInfoTo = CartInterceptor.threadLocal.get();
        return "cartList";
    }

    /**
     * 添加商品到购物车
     * @param
     * @return
     */
    @GetMapping("/addToCart")
    public String addToCart(@RequestParam("skuId") Long skuId,
                            @RequestParam("num")  Integer num,
                            Model model) throws ExecutionException, InterruptedException {
        CartItem cartItem =cartService.addToCart(skuId, num);
        model.addAttribute("item",cartItem);
        return "success";
    }

}
