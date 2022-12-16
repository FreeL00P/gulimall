package com.atguigu.gulimall.product.vo;

import lombok.Data;

/**
 * BrandVo
 * 获取分类关联的品牌接口返回类
 * @author fj
 * @date 2022/12/16 15:20
 */
@Data
public class BrandVo {
    /**
     * 品牌id
     */
    private Long brandId;

    /**
     * 品牌名字
     */
    private String brandName;
}
