package com.atguigu.gulimall.coupon.dao;

import com.atguigu.gulimall.coupon.entity.CouponEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 优惠券信息
 * 
 * @author freeL00P
 * @email 1084472249@qq.com
 * @date 2022-12-06 16:24:02
 */
@Mapper
public interface CouponDao extends BaseMapper<CouponEntity> {
	
}
