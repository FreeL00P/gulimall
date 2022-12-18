package com.atguigu.gulimall.product.feign;

import com.atguigu.common.to.SkuReductionTo;
import com.atguigu.common.to.SpuBoundsTo;
import com.atguigu.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * SpuFeignService
 *
 * @author fj
 * @date 2022/12/17 13:55
 */
@FeignClient("gulimall-coupon")
public interface CouponFeignService {
    @PostMapping("/coupon/spubounds/save")
    R saveSpuBounds(@RequestBody SpuBoundsTo spuBoundsTo);
    @PostMapping("/coupon/skufullreduction/saveInfo")
    R saveSkuReduction(@RequestBody SkuReductionTo skuReductionTo);
}
