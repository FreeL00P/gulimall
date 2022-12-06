package com.atguigu.gulimall.product.dao;

import com.atguigu.gulimall.product.entity.CategoryEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 商品三级分类
 * 
 * @author freeL00P
 * @email 1084472249@qq.com
 * @date 2022-12-06 13:42:45
 */
@Mapper
public interface CategoryDao extends BaseMapper<CategoryEntity> {
	
}
