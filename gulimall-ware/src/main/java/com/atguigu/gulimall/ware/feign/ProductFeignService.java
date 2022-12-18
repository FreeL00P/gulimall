package com.atguigu.gulimall.ware.feign;

import com.atguigu.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * ProductFeignService
 *
 * @author fj
 * @date 2022/12/18 13:54
 */
@FeignClient("gulimall-product")
public interface ProductFeignService {
    @RequestMapping("/product/skuinfo/info/{skuId}")
    //@RequiresPermissions("product:spuinfo:info")
    public R info(@PathVariable("skuId") Long skuId);
}
