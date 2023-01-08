package com.atguigu.gulimall.cart.service;

import com.atguigu.gulimall.cart.vo.CartItem;
import org.springframework.stereotype.Controller;

import java.util.concurrent.ExecutionException;

/**
 * CartService
 *
 * @author fj
 * @date 2023/1/7 20:24
 */

public interface CartService {
    CartItem addToCart(Long skuId, Integer num) throws ExecutionException, InterruptedException;
}
