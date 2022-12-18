package com.atguigu.gulimall.product.service;

import com.atguigu.gulimall.product.entity.AttrEntity;
import com.atguigu.gulimall.product.vo.AttrGroupRelationVo;
import com.atguigu.gulimall.product.vo.AttrGroupWithAttrsVo;
import com.baomidou.mybatisplus.extension.service.IService;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.gulimall.product.entity.AttrGroupEntity;

import java.util.List;
import java.util.Map;

/**
 * 属性分组
 *
 * @author freeL00P
 * @email 1084472249@qq.com
 * @date 2022-12-06 13:42:45
 */
public interface AttrGroupService extends IService<AttrGroupEntity> {

    PageUtils queryPage(Map<String, Object> params);
    //根据菜单id获取分类属性分组
    PageUtils queryPage(Map<String, Object> params, Long catelogId);

    List<AttrGroupWithAttrsVo> getAttrgroupWithAttr(Long catelogId);
}

