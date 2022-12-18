package com.atguigu.gulimall.product.service.impl;


import com.atguigu.gulimall.product.dao.AttrAttrgroupRelationDao;
import com.atguigu.gulimall.product.dao.AttrDao;
import com.atguigu.gulimall.product.entity.AttrAttrgroupRelationEntity;
import com.atguigu.gulimall.product.entity.AttrEntity;
import com.atguigu.gulimall.product.service.AttrService;
import com.atguigu.gulimall.product.vo.AttrGroupRelationVo;
import com.atguigu.gulimall.product.vo.AttrGroupWithAttrsVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.common.utils.Query;

import com.atguigu.gulimall.product.dao.AttrGroupDao;
import com.atguigu.gulimall.product.entity.AttrGroupEntity;
import com.atguigu.gulimall.product.service.AttrGroupService;
import org.springframework.util.StringUtils;


@Service("attrGroupService")
public class AttrGroupServiceImpl extends ServiceImpl<AttrGroupDao, AttrGroupEntity> implements AttrGroupService {

    @Autowired
    private AttrAttrgroupRelationDao relationDao;

    @Autowired
    private AttrDao attrDao;

    @Autowired
    private AttrService attrService;
    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<AttrGroupEntity> page = this.page(
                new Query<AttrGroupEntity>().getPage(params),
                new QueryWrapper<AttrGroupEntity>()
        );

        return new PageUtils(page);
    }

    /**
     * 根据菜单id获取分类属性分组
     * @param params
     * @param catelogId
     * @return
     */
    @Override
    public PageUtils queryPage(Map<String, Object> params, Long catelogId) {
        QueryWrapper<AttrGroupEntity> wrapper = new QueryWrapper<>();

        String key=(String)params.get("key");
        //判断是否传了搜索条件
        if (!StringUtils.isEmpty(key)){
            wrapper.and((obj)->{
                obj.eq("attr_group_id",key).or().like("attr_group_name",key);
            });
        }
        //如果传过来的id为0则返回所有分组
        if(catelogId == 0){
            IPage<AttrGroupEntity> page=this.page(
                new Query<AttrGroupEntity>().getPage(params),
                wrapper
            );
            return new PageUtils(page);
        }else{
            wrapper.eq("catelog_id",catelogId);
            IPage<AttrGroupEntity> page=this.page(
                    new Query<AttrGroupEntity>().getPage(params),
                    wrapper
            );
            return new PageUtils(page);
        }
    }
    //获取分类下所有分组&关联属性
    @Override
    public List<AttrGroupWithAttrsVo> getAttrgroupWithAttr(Long catelogId) {
        //根据catelogId在AttrGroup表中查询分组信息
        List<AttrGroupEntity> groupEntityList = this.list(new QueryWrapper<AttrGroupEntity>().eq("catelog_id", catelogId));
        if (groupEntityList.size()!= 0&&groupEntityList!=null){
            //查询每个分组关联的属性信息
            List<AttrGroupWithAttrsVo> attrGroupWithAttrsVos = groupEntityList.stream().map(item -> {
                /*Long attrGroupId = item.getAttrGroupId();
                //在关系表中查询关联信息
                List<AttrAttrgroupRelationEntity> relationEntities = relationDao.selectList(new QueryWrapper<AttrAttrgroupRelationEntity>()
                        .eq("attr_group_id", attrGroupId));
                //获取关联的属性id
                List<Long> attrIds = relationEntities.stream().map(relation -> {
                    return relation.getAttrId();
                }).collect(Collectors.toList());
                //根据属性id查询属性信息
                List<AttrEntity> attrEntities = attrDao.selectBatchIds(attrIds);
                //将信息保存到最终返回Vo类
                AttrGroupWithAttrsVo attrGroupWithAttrsVo = new AttrGroupWithAttrsVo();
                BeanUtils.copyProperties(item, attrGroupWithAttrsVo);
                attrGroupWithAttrsVo.setAttrs(attrEntities);*/
                AttrGroupWithAttrsVo attrGroupWithAttrsVo = new AttrGroupWithAttrsVo();
                BeanUtils.copyProperties(item, attrGroupWithAttrsVo);
                //查询所有属性
                List<AttrEntity> attrEntities = attrService.getAttrRelationByAttrGroupId(attrGroupWithAttrsVo.getAttrGroupId());
                attrGroupWithAttrsVo.setAttrs(attrEntities);
                return attrGroupWithAttrsVo;
            }).collect(Collectors.toList());
            return attrGroupWithAttrsVos;
        }else{
            return null;
        }
    }


}