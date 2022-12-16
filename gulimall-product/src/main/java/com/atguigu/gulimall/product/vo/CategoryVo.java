package com.atguigu.gulimall.product.vo;

import lombok.Data;

/**
 * CatelogVo
 * 获取品牌关联的分类信息 返回类
 * @author fj
 * @date 2022/12/16 15:58
 */
@Data
public class CategoryVo {
    /**
     * 分类id
     */
    private Long catelogId;
    /**
     * 分类名称
     */
    private String catelogName;
}
