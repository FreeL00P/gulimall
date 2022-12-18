package com.atguigu.gulimall.product.service;

import com.atguigu.gulimall.product.entity.ProductAttrValueEntity;
import com.atguigu.gulimall.product.vo.AttrGroupRelationVo;
import com.atguigu.gulimall.product.vo.AttrRespVo;
import com.atguigu.gulimall.product.vo.AttrVo;
import com.baomidou.mybatisplus.extension.service.IService;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.gulimall.product.entity.AttrEntity;

import java.util.List;
import java.util.Map;

/**
 * 商品属性
 *
 * @author freeL00P
 * @email 1084472249@qq.com
 * @date 2022-12-06 13:42:44
 */
public interface AttrService extends IService<AttrEntity> {

    PageUtils queryPage(Map<String, Object> params);
    //保存属性【规格参数，销售属性】
    void saveAttrVo(AttrVo attr);
    //获取分类规格参数
    PageUtils queryPage(Map<String, Object> params, Long catelogId,String type);
    //查询属性详情
    AttrRespVo getAttrInfo(Long attrId);


    void updateAttr(AttrVo attr);

    List<AttrEntity> getAttrRelationByAttrGroupId(Long attrGroupId);

    void deleteAttrRelation(AttrGroupRelationVo[] vos);

    PageUtils getNoRelation(Long attrGroupId, Map<String, Object> params);


}

