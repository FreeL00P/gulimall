package com.atguigu.gulimall.product.service.impl;


import org.springframework.stereotype.Service;
import java.util.Map;
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
        //如果传过来的id为0则返回所有分组
        if(catelogId == 0){
            IPage<AttrGroupEntity> page=this.page(
                new Query<AttrGroupEntity>().getPage(params),
                new QueryWrapper<AttrGroupEntity>()
            );
            return new PageUtils(page);
        }else{
            QueryWrapper<AttrGroupEntity> wrapper = new QueryWrapper<>();
            wrapper.eq("catelog_id",catelogId);
            String key=(String)params.get("key");
            //判断是否传了搜索条件
            if (!StringUtils.isEmpty(key)){
                wrapper.and((obj)->{
                   obj.eq("attr_group_id",key).or().like("attr_group_name",key);
                });
            }
            IPage<AttrGroupEntity> page=this.page(
                    new Query<AttrGroupEntity>().getPage(params),
                   wrapper
            );
            return new PageUtils(page);
        }


    }

}