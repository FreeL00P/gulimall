package com.atguigu.gulimall.cart.service.impl;

import com.atguigu.gulimall.cart.service.CartService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

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

}
