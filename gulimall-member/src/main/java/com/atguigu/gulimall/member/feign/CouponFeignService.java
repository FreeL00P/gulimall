package com.atguigu.gulimall.member.feign;

import com.atguigu.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * CouponFeignService
 *
 * @author fj
 * @date 2022/12/6 23:14
 */
@FeignClient("gulimall-coupon")
public interface CouponFeignService {

    @RequestMapping("coupon/coupon/member/list")
    public R memberCoupons();
}
