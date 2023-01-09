package com.atguigu.gulimall.cart.controller;

import com.atguigu.common.constant.AuthServerConstant;
import com.atguigu.gulimall.cart.interceptor.CartInterceptor;
import com.atguigu.gulimall.cart.service.CartService;
import com.atguigu.gulimall.cart.to.UserInfoTo;
import com.atguigu.gulimall.cart.vo.Cart;
import com.atguigu.gulimall.cart.vo.CartItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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

    @GetMapping("/checkItem")
    public String checkItem(@RequestParam("skuId") Long skuId,
                            @RequestParam("check") Integer check){
        cartService.checkItem(skuId, check);
        return "redirect:http://cart.gulimall.com/cart.html";
    }

    @GetMapping("/cart.html")
    public String cartListPage(Model model) throws ExecutionException, InterruptedException {
//        //快速得到用户信息 id user-key
//        UserInfoTo userInfoTo = CartInterceptor.threadLocal.get();
        Cart cart =cartService.getCart();
        model.addAttribute("cart", cart);
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
                            RedirectAttributes ra) throws ExecutionException, InterruptedException {
        CartItem cartItem =cartService.addToCart(skuId, num);
        //ra.addAttribute("item",cartItem);
        ra.addAttribute("skuId",skuId);
        return "redirect:http://cart.gulimall.com/addToCartSuccess.html";
    }

    @GetMapping("/addToCartSuccess.html")
    public String addToCartSuccess(@RequestParam("skuId") Long skuId,Model model) {
        //重定向到成功页面，再次查询购物车数据
        CartItem cartItem= cartService.getCartItem(skuId);
        model.addAttribute("item",cartItem);
        return "success";
    }
}
