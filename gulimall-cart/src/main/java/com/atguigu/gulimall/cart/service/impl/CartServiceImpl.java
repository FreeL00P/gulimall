package com.atguigu.gulimall.cart.service.impl;


import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.atguigu.common.utils.R;
import com.atguigu.gulimall.cart.feign.ProductFeignService;
import com.atguigu.gulimall.cart.interceptor.CartInterceptor;
import com.atguigu.gulimall.cart.service.CartService;
import com.atguigu.gulimall.cart.to.UserInfoTo;
import com.atguigu.gulimall.cart.vo.Cart;
import com.atguigu.gulimall.cart.vo.CartItem;
import com.atguigu.gulimall.cart.vo.SkuInfoVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.stream.Collectors;

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

        BoundHashOperations<String, Object, Object> cartOps = getCartOps();
        String res =(String) cartOps.get(skuId.toString());
        if (StringUtils.isEmpty(res)) {
            //购物车无此商品
            CartItem cartItem = new CartItem();
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
            cartOps.put(skuId.toString(), s);
            return cartItem;
        }else{
            //购物车有此商品
            CartItem cartItem= JSON.parseObject(res, CartItem.class);
            cartItem.setCount(cartItem.getCount()+num);
            String s = JSON.toJSONString(cartItem);
            cartOps.put(skuId.toString(), s);
            return cartItem;
        }


    }

    @Override
    public CartItem getCartItem(Long skuId) {
        BoundHashOperations<String, Object, Object> cartOps = getCartOps();
        String res =(String) cartOps.get(skuId.toString());

        return   JSON.parseObject(res,CartItem.class);
    }

    @Override
    public Cart getCart() throws ExecutionException, InterruptedException {
        Cart cart=new Cart();
        //判断登陆状态
        UserInfoTo userInfoTo = CartInterceptor.threadLocal.get();
        if(userInfoTo.getUserId()!=null ){
            //登陆
            String cartKey  =CART_PREFIX+userInfoTo.getUserId();
            BoundHashOperations<String, Object, Object> hashOps = redisTemplate.boundHashOps(cartKey);
            //如果临时购物车的数据没有合并
            String tempKey = CART_PREFIX + userInfoTo.getUserKey();
            List<CartItem> tempCartItems = getCartItems(tempKey);
            if (tempCartItems!=null){
                //临时购物车有数据 合并
                for (CartItem item : tempCartItems) {
                    addToCart(item.getSkuId(),item.getCount());
                }
                //清空购物车
                clearCart(tempKey);
            }
            //获取登陆后的购物车数量,包含合并过来的临时购物车数据
            List<CartItem> cartItems = getCartItems(cartKey);
            cart.setItems(cartItems);
        }else{
            //未登陆
            String cartKey  =CART_PREFIX+ userInfoTo.getUserKey();
            //获取临时购物车的所有购物项
            List<CartItem> cartItems = getCartItems(cartKey);
            cart.setItems(cartItems);
        }
        return cart;
    }

    /**
     * 获取我们要操作的购物车
     * @retfatkunurn
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

    private List<CartItem> getCartItems(String cartKey){
        BoundHashOperations<String, Object, Object> hashOps = redisTemplate.boundHashOps(cartKey);
        List<Object> values = hashOps.values();
        if(values!=null && values.size()>0){
            List<CartItem> collect = values.stream().map(obj -> {
                String str=(String)obj;
                return  JSON.parseObject(str,CartItem.class);
            }).collect(Collectors.toList());
            return collect;
        }
        return null;
    }

    @Override
    public void clearCart(String cartKey) {
       redisTemplate.delete(cartKey);
    }

    @Override
    public void checkItem(Long skuId, Integer check) {
        BoundHashOperations<String, Object, Object> cartOps = getCartOps();
        CartItem cartItem = getCartItem(skuId);
        cartItem.setCheck(check == 1);
        String s = JSON.toJSONString(cartItem);
        cartOps.put(skuId.toString(),s);
    }

    @Override
    public void changeItemCount(Long skuId, Integer num) {
        CartItem cartItem = getCartItem(skuId);
        cartItem.setCount(num);
        BoundHashOperations<String, Object, Object> cartOps = getCartOps();
        cartOps.put(skuId.toString(),JSON.toJSONString(cartItem));

    }

    @Override
    public void deleteItem(Long skuId) {
        BoundHashOperations<String, Object, Object> cartOps = getCartOps();
        cartOps.delete(skuId.toString());
    }

    @Override
    public List<CartItem> getUserCartItems() {
        UserInfoTo userInfoTo = CartInterceptor.threadLocal.get();
        if (userInfoTo.getUserId() == null) {
            return null;
        }else {
            String cartKey=CART_PREFIX+userInfoTo.getUserId();
            //获取所有被选中的购物项
            List<CartItem> cartItems = getCartItems(cartKey);
            List<CartItem> collect = cartItems.stream().filter(item -> item.getCheck()).map(item->{
                //更新为最新价格信息
                R price = productFeignService.getPrice(item.getSkuId());
                String data =(String) price.get("data");
                return  item.setPrice(new BigDecimal(data));

            }).collect(Collectors.toList());
            return collect;
        }
    }
}
