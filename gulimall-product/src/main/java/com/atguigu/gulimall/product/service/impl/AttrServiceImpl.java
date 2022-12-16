package com.atguigu.gulimall.product.service.impl;

import com.atguigu.common.constant.ProductConstant;
import com.atguigu.gulimall.product.dao.AttrAttrgroupRelationDao;
import com.atguigu.gulimall.product.dao.AttrGroupDao;
import com.atguigu.gulimall.product.dao.CategoryDao;
import com.atguigu.gulimall.product.entity.AttrAttrgroupRelationEntity;
import com.atguigu.gulimall.product.entity.AttrGroupEntity;
import com.atguigu.gulimall.product.entity.CategoryEntity;
import com.atguigu.gulimall.product.service.AttrGroupService;
import com.atguigu.gulimall.product.service.CategoryService;
import com.atguigu.gulimall.product.vo.AttrGroupRelationVo;
import com.atguigu.gulimall.product.vo.AttrRespVo;
import com.atguigu.gulimall.product.vo.AttrVo;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.common.utils.Query;

import com.atguigu.gulimall.product.dao.AttrDao;
import com.atguigu.gulimall.product.entity.AttrEntity;
import com.atguigu.gulimall.product.service.AttrService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;


@Service("attrService")
public class AttrServiceImpl extends ServiceImpl<AttrDao, AttrEntity> implements AttrService {

    @Autowired
    private CategoryDao categoryDao;

    @Autowired
    private AttrGroupDao attrGroupDao;
    @Autowired
    private AttrAttrgroupRelationDao relationDao;

    @Autowired
    private CategoryService categoryService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<AttrEntity> page = this.page(
                new Query<AttrEntity>().getPage(params),
                new QueryWrapper<AttrEntity>()
        );

        return new PageUtils(page);
    }

    /**
     * 保存属性【规格参数，销售属性】
     * @param attrVo
     */
    @Override
    public void saveAttrVo(AttrVo attrVo) {
        AttrEntity attr = new AttrEntity();
        BeanUtils.copyProperties(attrVo,attr);
        this.save(attr);
        //判断是不是基本属性
        if (attr.getAttrType()== ProductConstant.AttrEnum.ATTR_TYPE_BASE.getCode()){
            //获取属性值，添加到关系表
            AttrAttrgroupRelationEntity relationEntity = new AttrAttrgroupRelationEntity();
            relationEntity.setAttrGroupId(attrVo.getAttrGroupId());
            relationEntity.setAttrId( attr.getAttrId());
            relationDao.insert(relationEntity);
        }

    }

    /**
     * 获取分类规格参数
     * @param params
     * @param catelogId
     * @return
     */
    @Override
    public PageUtils queryPage(Map<String, Object> params, Long catelogId,String type) {
        QueryWrapper<AttrEntity> wrapper = new QueryWrapper<>();
        wrapper.eq("attr_type","base".equalsIgnoreCase(type)?
                ProductConstant.AttrEnum.ATTR_TYPE_BASE.getCode():
                ProductConstant.AttrEnum.ATTR_TYPE_SALE.getCode());
        //模糊查询的key
        String key = (String) params.get("key");
        IPage<AttrEntity> page = null;
        if(!StringUtils.isEmpty(key)){
            wrapper.like("attr_name", key).or().eq("attr_id",key);
        }
        //如果没有传catelogId 返回所有分类规格参数
        if(!StringUtils.isEmpty(catelogId)){
            page = this.page(
                    new Query<AttrEntity>().getPage(params),
                    wrapper
            );
        }else{
            wrapper.eq("catelog_id",catelogId);
             page = this.page(
                    new Query<AttrEntity>().getPage(params),
                    wrapper
            );
        }
        PageUtils pageUtils = new PageUtils(page);
        //因为attrEntity中缺少GroupName两个属性值需要获取
        List<AttrEntity> records = page.getRecords();
        List<AttrRespVo> respVoList= records.stream().map((attrEntity)->{
            AttrRespVo respVo = new AttrRespVo();
            BeanUtils.copyProperties(attrEntity, respVo);
            //获取attrId
            Long attrId = attrEntity.getAttrId();
            //封装查询条件
            QueryWrapper<AttrAttrgroupRelationEntity> attrWrapper =
                    new QueryWrapper<AttrAttrgroupRelationEntity>().eq("attr_id", attrId);
            if("base".equalsIgnoreCase(type)) {
                //从关系表在查询数据
                AttrAttrgroupRelationEntity relationEntity = relationDao.selectOne(attrWrapper);
                if(relationEntity != null){
                    //获取groupId
                    Long attrGroupId = relationEntity.getAttrGroupId();
                    //根据attrGroupId查询attrGroup
                    AttrGroupEntity attrGroupEntity = attrGroupDao.selectById(attrGroupId);
                    //设置GroupName
                    respVo.setGroupName(attrGroupEntity.getAttrGroupName());
                }
            }


            //获取分类id
            Long attrEntityCatelogId = attrEntity.getCatelogId();
            CategoryEntity categoryEntity = categoryDao.selectById(attrEntityCatelogId);
            if (categoryEntity!=null){
                respVo.setCatelogName(categoryEntity.getName());
            }
            return respVo;
        }).collect(Collectors.toList());
        pageUtils.setList(respVoList);
        return pageUtils;
    }

    /**
     * 查询属性详情
     * @param attrId
     * @return
     */
    @Override
    public AttrRespVo getAttrInfo(Long attrId) {
        AttrRespVo respVo = new AttrRespVo();
        //设置分类信息
        //根据attrId查询attr对象
        AttrEntity attrEntity = this.getById(attrId);
        BeanUtils.copyProperties(attrEntity,respVo);

        //获取categoryId查询分类完整路径
        Long catelogId = attrEntity.getCatelogId();
        Long[] catelogPath = categoryService.getCatelogPath(catelogId);
        respVo.setCatelogPath(catelogPath);
        CategoryEntity categoryEntity = categoryDao.selectById(catelogId);
        //设置分类名字
        if (categoryEntity != null){
            respVo.setCatelogName(categoryEntity.getName());
        }
        //判断是否是基本数据类型，只有基本数据类型才需要添加分组信息
        if (attrEntity.getAttrType()==ProductConstant.AttrEnum.ATTR_TYPE_BASE.getCode()){
            //设置分组信息
            //关系表中查询attrGroupId
            QueryWrapper<AttrAttrgroupRelationEntity> wrapper = new QueryWrapper<>();
            wrapper.eq("attr_id",attrId);
            AttrAttrgroupRelationEntity relationEntity = relationDao.selectOne(wrapper);
            if (relationEntity != null) {
                respVo.setAttrGroupId(relationEntity.getAttrGroupId());
                AttrGroupEntity attrGroupEntity = attrGroupDao.selectById(relationEntity.getAttrGroupId());
                if (attrGroupEntity !=null){
                    respVo.setGroupName(attrGroupEntity.getAttrGroupName());
                }
            }
        }

        return respVo;
    }

    @Override
    @Transactional
    public void updateAttr(AttrVo attr) {
        AttrEntity attrEntity = new AttrEntity();
        BeanUtils.copyProperties(attr, attrEntity);
        this.updateById(attrEntity);
        UpdateWrapper<AttrAttrgroupRelationEntity> wrapper = new UpdateWrapper<AttrAttrgroupRelationEntity>()
                .eq("attr_id", attr.getAttrId());
        if (attrEntity.getAttrType()==ProductConstant.AttrEnum.ATTR_TYPE_BASE.getCode()){
            //修改分组关联
            AttrAttrgroupRelationEntity relationEntity = new AttrAttrgroupRelationEntity();
            relationEntity.setAttrId(attr.getAttrId());
            relationEntity.setAttrGroupId(attr.getAttrGroupId());
            //判断是追加还是更新
            Integer count = relationDao.selectCount(wrapper);
            if (count >0){

                relationDao.update(relationEntity,wrapper);
            }else{
                relationDao.insert(relationEntity);
            }
        }
    }

    /**
     * 获取属性分组的关联的所有属性
     * @param attrGroupId
     * @return
     */
    @Override
    public List<AttrEntity> getAttrRelationByAttrGroupId(String attrGroupId) {
        //从关系表中获取关联的属性分组id
        QueryWrapper<AttrAttrgroupRelationEntity> wrapper = new QueryWrapper<>();
        wrapper.eq("attr_group_id",attrGroupId);
        List<AttrAttrgroupRelationEntity> relationEntities = relationDao.selectList(wrapper);
        List<Long> attrIds=relationEntities.stream().map(AttrAttrgroupRelationEntity::getAttrId).collect(Collectors.toList());
        if (attrIds==null||attrIds.size()==0){
            return null;
        }else{
            //根据获取到的ids集合查询attr对象
            return this.listByIds(attrIds);
        }

    }
    //删除属性与分组的关联关系
    @Override
    public void deleteAttrRelation(AttrGroupRelationVo[] vos) {
        List<AttrAttrgroupRelationEntity> entities= Arrays.asList(vos).stream().map((vo)->{
            AttrAttrgroupRelationEntity relation = new AttrAttrgroupRelationEntity();
            BeanUtils.copyProperties(vo,relation);
            return relation;
        }).collect(Collectors.toList());
        relationDao.deleteRelation(entities);
    }
    //获取属性分组没有关联的其他属性
    @Override
    public PageUtils getNoRelation(Long attrGroupId, Map<String, Object> params) {
        //1、当前分组只能关联直接所属分类里的所有属性
        //获取分类id
        AttrGroupEntity groupEntity = attrGroupDao.selectById(attrGroupId);
        Long catelogId = groupEntity.getCatelogId();
        //2、当前分组只能关联别的分组没有引用的属性
        //2.1、当前分类下的其他分组、
        //根据catelogId查询所有所属分类下的所有分组
        List<AttrGroupEntity> groups = attrGroupDao.selectList(new QueryWrapper<AttrGroupEntity>()
                .eq("catelog_id", catelogId));
        //在groupEntityList集合中获取所有属性attrId
        List<Long> attrGroupIds = groups.stream().map((group) -> {
            return group.getAttrGroupId();
        }).collect(Collectors.toList());
        //2.2、其他分组关联的属性
        //根据attrGroupId查询分组关联的属性
        List<AttrAttrgroupRelationEntity> relationEntities = relationDao.selectList(new QueryWrapper<AttrAttrgroupRelationEntity>()
                .in("attr_group_id", attrGroupIds));
        //获取属性id
        List<Long> attrIds = relationEntities.stream().map((relation) -> {
            return relation.getAttrId();
        }).collect(Collectors.toList());
        //从当前分类的所有属性中排除这些属性
        //根据分类id查询当前分类下的所有属性

        QueryWrapper<AttrEntity> wrapper = new QueryWrapper<>();
        if (attrIds!= null&&attrIds.size()>0) {
            wrapper.notIn("attr_id",attrIds);
        }
        wrapper.eq("catelog_id",catelogId).eq("attr_type",ProductConstant.AttrEnum.ATTR_TYPE_BASE.getCode());
        String key= (String) params.get("key");
        if(!StringUtils.isEmpty(key)){
           wrapper.and((w)->{
               w.eq("attr_id",key).or().like("attr_name",key);
           });
        }
        IPage<AttrEntity> page = this.page(new Query<AttrEntity>().getPage(params), wrapper);
        PageUtils pageUtils = new PageUtils(page);
        return pageUtils;
    }
}