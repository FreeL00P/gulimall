package com.atguigu.gulimall.order.service.impl;

import com.atguigu.common.constant.CartConstant;
import com.atguigu.common.vo.MemberResponseVo;
import com.atguigu.gulimall.order.feign.CartFeignService;
import com.atguigu.gulimall.order.feign.MemberFeignService;
import com.atguigu.gulimall.order.interceptor.LoginUserInterceptor;
import com.atguigu.gulimall.order.vo.MemberAddressVo;
import com.atguigu.gulimall.order.vo.OrderConfirmVo;
import com.atguigu.gulimall.order.vo.OrderItemVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadPoolExecutor;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.common.utils.Query;

import com.atguigu.gulimall.order.dao.OrderDao;
import com.atguigu.gulimall.order.entity.OrderEntity;
import com.atguigu.gulimall.order.service.OrderService;


@Service("orderService")
public class OrderServiceImpl extends ServiceImpl<OrderDao, OrderEntity> implements OrderService {

    @Autowired
    MemberFeignService memberFeignService;

    @Autowired
    CartFeignService cartFeignService;

    @Autowired
    ThreadPoolExecutor executor;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<OrderEntity> page = this.page(
                new Query<OrderEntity>().getPage(params),
                new QueryWrapper<OrderEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public OrderConfirmVo confirmOrder() throws ExecutionException, InterruptedException {
        OrderConfirmVo orderConfirmVo = new OrderConfirmVo();
        //获取登陆用户信息
        MemberResponseVo memberResponseVo = LoginUserInterceptor.loginUser.get();

        CompletableFuture<Void> getAddressFuture = CompletableFuture.runAsync(() -> {
            //1、远程查询所有的收货地址
            List<MemberAddressVo> address = memberFeignService.getAddress(memberResponseVo.getId());
            orderConfirmVo.setAddress(address);
        },executor);

        CompletableFuture<Void> cartFuture = CompletableFuture.runAsync(() -> {
            //2、远程查询购物车中选中的购物项
            List<OrderItemVo> cartItems = cartFeignService.getCurrentUserCartItems();
            orderConfirmVo.setItems(cartItems);
        }, executor);

        //3、查询用户积分
        Integer integration = memberResponseVo.getIntegration();
        orderConfirmVo.setIntegration(integration);


        //4. 其他数据自动计算

        //TODO 5. 防重令牌

        CompletableFuture.allOf(getAddressFuture,cartFuture).get();
        return orderConfirmVo;
    }

}