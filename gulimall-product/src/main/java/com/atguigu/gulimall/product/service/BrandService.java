package com.atguigu.gulimall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.gulimall.product.entity.BrandEntity;

import java.util.Map;

/**
 * 品牌
 *
 * @author freeL00P
 * @email 1084472249@qq.com
 * @date 2022-12-06 13:42:45
 */
public interface BrandService extends IService<BrandEntity> {

    PageUtils queryPage(Map<String, Object> params);

    /**
     * 数据同步更新
     * @param brand
     */
    void updateDetail(BrandEntity brand);
}

