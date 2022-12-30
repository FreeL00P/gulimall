package com.atguigu.gulimall.product.service;

import com.atguigu.gulimall.product.vo.SpuSave.BaseAttrs;
import com.baomidou.mybatisplus.extension.service.IService;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.gulimall.product.entity.ProductAttrValueEntity;

import java.util.List;
import java.util.Map;

/**
 * spu属性值
 *
 * @author freeL00P
 * @email 1084472249@qq.com
 * @date 2022-12-06 13:42:44
 */
public interface ProductAttrValueService extends IService<ProductAttrValueEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void saveAttrValue(Long spuId, List<BaseAttrs> attrValue);

    List<ProductAttrValueEntity> getSpuInfo(Long spuId);

    void updateSpuInfo(Long spuId, List<ProductAttrValueEntity> entities);

    List<ProductAttrValueEntity> getAttrValueListBySpuId(Long spuId);
}

