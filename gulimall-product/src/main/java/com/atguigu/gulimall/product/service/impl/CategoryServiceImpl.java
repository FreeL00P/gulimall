package com.atguigu.gulimall.product.service.impl;

import com.atguigu.gulimall.product.service.CategoryBrandRelationService;
import com.atguigu.gulimall.product.vo.Catelog2Vo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.common.utils.Query;

import com.atguigu.gulimall.product.dao.CategoryDao;
import com.atguigu.gulimall.product.entity.CategoryEntity;
import com.atguigu.gulimall.product.service.CategoryService;


@Service("categoryService")
public class CategoryServiceImpl extends ServiceImpl<CategoryDao, CategoryEntity> implements CategoryService {

    @Autowired
    private CategoryBrandRelationService categoryBrandRelationService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<CategoryEntity> page = this.page(
                new Query<CategoryEntity>().getPage(params),
                new QueryWrapper<CategoryEntity>()
        );

        return new PageUtils(page);
    }

    /**
     * //查出所有分类以及子分类，
     * @return 以树形结构组装
     */
    @Override
    public List<CategoryEntity> listWithTree() {
         List<CategoryEntity> entities = baseMapper.selectList(null);
        //查询一级分类
        List<CategoryEntity> level1List= entities.stream().filter((categoryEntity ->{
            return categoryEntity.getParentCid()==0;
        })).map((menu->{
            //获取子分类
            menu.setChildren(getChildrenList(menu,entities));
            return menu;
        })).sorted((menu1,menu2)->{
            //排序
            return (menu1.getSort()==null?0:menu1.getSort())-(menu2.getSort()==null?0:menu2.getSort());
        }).collect(Collectors.toList());
        return level1List;
    }

    /**
     * 批量删除菜单列表
     * @param ids
     */
    @Override
    public void removeMenuByIds(List<Long> ids) {
        baseMapper.deleteBatchIds(ids);
    }

    /**
     * 获取分类全路径
     * @param catelogId
     * @return
     */
    @Override
    public Long[] getCatelogPath(Long catelogId) {
        List<Long> paths = new ArrayList<Long>();
        List<Long> parentPath = findParentCid(paths, catelogId);
        Collections.reverse(parentPath);
        return parentPath.toArray(new Long[parentPath.size()]);
    }

    /**
     * 级联更新关联表中的数据
     * @param category
     */
    @Override
    public void updateCascade(CategoryEntity category) {
        //更新自己
        this.updateById(category);
        categoryBrandRelationService.updateCategory(category.getCatId(),category.getName());
    }

    /**
     * 获取一级分类id
     * @return
     */
    @Override
    public List<CategoryEntity> getLevel1Category() {
        QueryWrapper<CategoryEntity> wrapper = new QueryWrapper<CategoryEntity>().eq("cat_level", 1);
        return this.list(wrapper);
    }

    @Override
    public Map<String, List<Catelog2Vo>> getCatalogJson() {
        //获取所有一级分类
        List<CategoryEntity> category1Level = this.getLevel1Category();
        Map<String, List<Catelog2Vo>> map = category1Level.stream().collect(Collectors.toMap(k -> k.getCatId().toString(), v -> {
            //查询一级分类下对应的二级分类
            List<CategoryEntity> category2Level = this.list(new QueryWrapper<CategoryEntity>().eq("parent_cid", v.getCatId()));
            List<Catelog2Vo> catelog2Vos = null;
            if (category2Level != null && category2Level.size() != 0) {
                //封装成Vo
                catelog2Vos = category2Level.stream().map(item -> {
                    Catelog2Vo catelog2Vo = new Catelog2Vo();
                    catelog2Vo.setId(item.getCatId().toString());
                    catelog2Vo.setName(item.getName());
                    catelog2Vo.setCatalog1Id(item.getParentCid().toString());
                    //查询二级分类下的三级分类
                    List<CategoryEntity> category3Level = this.list(new QueryWrapper<CategoryEntity>().eq("parent_cid", item.getCatId()));
                    List<Catelog2Vo.Catelog3Vo> catelog3Vos = null;
                    if (category3Level != null && category3Level.size() != 0) {
                        //封装成VO
                        catelog3Vos = category3Level.stream().map(level3 -> {
                            Catelog2Vo.Catelog3Vo catelog3Vo = new Catelog2Vo.Catelog3Vo();
                            catelog3Vo.setCatalog2Id(level3.getParentCid().toString());
                            catelog3Vo.setId(level3.getCatId().toString());
                            catelog3Vo.setName(level3.getName());
                            return catelog3Vo;
                        }).collect(Collectors.toList());
                    }
                    catelog2Vo.setCatalog3List(catelog3Vos);
                    return catelog2Vo;
                }).collect(Collectors.toList());
            }
            return catelog2Vos;
        }));
        return map;
    }

    public List<Long> findParentCid(List<Long> paths,Long catelogId){
        paths.add(catelogId);
        CategoryEntity category = this.getById(catelogId);
        if (category.getParentCid() != 0){
            return findParentCid(paths,category.getParentCid());
        }
        return paths;
    }


    /**
     * 从all中获取root的所有子分类（递归）
     * @param root 需要获取子分类的CategoryEntity
     * @param all 所有分类列表
     * @return
     */
    private List<CategoryEntity> getChildrenList(CategoryEntity root,List<CategoryEntity> all){
        List<CategoryEntity> children=all.stream().filter((categoryEntity)->{
            //返回所有分类列表中父id为root的id的分类
            return categoryEntity.getParentCid().equals(root.getCatId());
        }).map((categoryEntity)->{
            categoryEntity.setChildren(getChildrenList(categoryEntity, all));
            return categoryEntity;
        }).sorted((menu1, menu2) ->{
            return (menu1.getSort()==null?0:menu1.getSort())-(menu2.getSort()==null?0:menu2.getSort());
        }).collect(Collectors.toList());
        return  children;
    }
}