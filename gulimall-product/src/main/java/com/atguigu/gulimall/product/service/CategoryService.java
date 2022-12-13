package com.atguigu.gulimall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.gulimall.product.entity.CategoryEntity;

import java.util.List;
import java.util.Map;

/**
 * 商品三级分类
 *
 * @author freeL00P
 * @email 1084472249@qq.com
 * @date 2022-12-06 13:42:45
 */
public interface CategoryService extends IService<CategoryEntity> {

    PageUtils queryPage(Map<String, Object> params);

    //查出所有分类以及子分类，以树形结构组装
    List<CategoryEntity> listWithTree();
    //批量删除菜单列表
    void removeMenuByIds(List<Long> ids);
    //根据菜单ID查询层级路径将id封装成一个数组
    Long[] getCatelogPath(Long catelogId);

    void updateCascade(CategoryEntity category);
}

