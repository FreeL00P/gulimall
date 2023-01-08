package com.atguigu.gulimall.cart.service.impl;


import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.atguigu.common.utils.R;
import com.atguigu.gulimall.cart.feign.ProductFeignService;
import com.atguigu.gulimall.cart.interceptor.CartInterceptor;
import com.atguigu.gulimall.cart.service.CartService;
import com.atguigu.gulimall.cart.to.UserInfoTo;
import com.atguigu.gulimall.cart.vo.CartItem;
import com.atguigu.gulimall.cart.vo.SkuInfoVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * CartServiceImpl
 *
 * @author fj
 * @date 2023/1/7 20:24
 */
@Slf4j
@Service
public class CartServiceImpl implements CartService {

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private ProductFeignService productFeignService;

    @Autowired
    private CartInterceptor cartInterceptor;

    @Autowired
    ThreadPoolExecutor executor;
    private final String CART_PREFIX = "gulimall:cart:";

    @Override
    public CartItem addToCart(Long skuId, Integer num) throws ExecutionException, InterruptedException {
        CartItem cartItem = new CartItem();
        BoundHashOperations<String, Object, Object> cartOps = getCartOps();
        CompletableFuture<Void> getSkuInfoTask = CompletableFuture.runAsync(() -> {
            //1、远程查询当前要添加的商品信息
            R r = productFeignService.info(skuId);
            SkuInfoVo skuInfo = r.getData("skuInfo", new TypeReference<SkuInfoVo>() {
            });
            cartItem.setCheck(true);
            cartItem.setSkuId(skuId);
            cartItem.setPrice(skuInfo.getPrice());
            cartItem.setCount(1);
            cartItem.setImage(skuInfo.getSkuDefaultImg());
            cartItem.setTitle(skuInfo.getSkuTitle());
        },executor);
        CompletableFuture<Void> getSkuSaleAttrValues = CompletableFuture.runAsync(() -> {
            //远程查询sku属性组合信息
            List<String> values = productFeignService.getSkuSaleAttrValues(skuId);
            cartItem.setSkuAttr(values);
        }, executor);
        CompletableFuture.allOf(getSkuInfoTask,getSkuSaleAttrValues).get();
        String s = JSON.toJSONString(cartItem);
        System.out.println("cartItem.toString()); = " + cartItem.toString());
        System.out.println("s = " + s);
        cartOps.put(skuId.toString(), s);
        return cartItem;
    }

    /**
     * 获取我们要操作的购物车
     * @return
     */
    private BoundHashOperations<String, Object, Object>  getCartOps() {
        UserInfoTo userInfoTo = CartInterceptor.threadLocal.get();
        String cartKey="";
        if (userInfoTo != null) {
            cartKey=CART_PREFIX+userInfoTo.getUserId();
        }else{
            cartKey=CART_PREFIX+userInfoTo.getUserKey();
        }
        return redisTemplate.boundHashOps(cartKey);
    }
}
