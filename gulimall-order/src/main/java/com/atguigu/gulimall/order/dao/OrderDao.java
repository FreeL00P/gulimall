package com.atguigu.gulimall.order.dao;

import com.atguigu.gulimall.order.entity.OrderEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 订单
 * 
 * @author freeL00P
 * @email 1084472249@qq.com
 * @date 2022-12-06 17:09:01
 */
@Mapper
public interface OrderDao extends BaseMapper<OrderEntity> {
	
}
