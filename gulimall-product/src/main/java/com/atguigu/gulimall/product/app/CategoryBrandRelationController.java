package com.atguigu.gulimall.product.app;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import com.atguigu.gulimall.product.entity.BrandEntity;
import com.atguigu.gulimall.product.entity.CategoryEntity;
import com.atguigu.gulimall.product.vo.BrandVo;
import com.atguigu.gulimall.product.vo.CategoryVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.atguigu.gulimall.product.entity.CategoryBrandRelationEntity;
import com.atguigu.gulimall.product.service.CategoryBrandRelationService;
import com.atguigu.common.utils.R;



/**
 * 品牌分类关联
 *
 * @author freeL00P
 * @email 1084472249@qq.com
 * @date 2022-12-06 14:49:31
 */
@RestController
@RequestMapping("product/categorybrandrelation")
public class CategoryBrandRelationController {
    @Autowired
    private CategoryBrandRelationService categoryBrandRelationService;

    /**
     * 列表
     */
//    @GetMapping ("/catelog/list")
//    //@RequiresPermissions("product:categorybrandrelation:list")
//    public R list(@RequestParam("brandId") String brandId){
//        List<CategoryBrandRelationEntity> data = categoryBrandRelationService.list(new QueryWrapper<CategoryBrandRelationEntity>().eq("brand_id", brandId));
//
//        return R.ok().put("data", data);
//    }
    /**
     * 获取品牌关联的分类
     */
    @GetMapping ("/catelog/list")
    public R GetCatelogList(@RequestParam("brandId") Long brandId){
        List<CategoryEntity> list=categoryBrandRelationService.getCatelogListByBrandId(brandId);
        //上面返回的是分类详细信息，进一步处理只返回我们需要的数据
        List<CategoryVo> collect = list.stream().map(item -> {
            CategoryVo catelogVo = new CategoryVo();
            catelogVo.setCatelogId(item.getCatId());
            catelogVo.setCatelogName(item.getName());
            return catelogVo;
        }).collect(Collectors.toList());
        return R.ok().put("data", collect);
    }
    /**
     * 获取分类关联的品牌
     */
    @GetMapping("/brands/list")
    public R getBrandList(@RequestParam("catId") Long catId){
       List<BrandEntity> list =categoryBrandRelationService.getBrandListByCatId(catId);
       //上面返回的是品牌详细信息，进一步处理只返回我们需要的数据
        List<BrandVo> collect = list.stream().map(item -> {
            BrandVo brandVo = new BrandVo();
            brandVo.setBrandId(item.getBrandId());
            brandVo.setBrandName(item.getName());
            return brandVo;
        }).collect(Collectors.toList());
        return R.ok().put("data",collect);
    }
    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    //@RequiresPermissions("product:categorybrandrelation:info")
    public R info(@PathVariable("id") Long id){
		CategoryBrandRelationEntity categoryBrandRelation = categoryBrandRelationService.getById(id);

        return R.ok().put("categoryBrandRelation", categoryBrandRelation);
    }

    /**
     * 保存
     */
    @PostMapping("/save")
    //@RequiresPermissions("product:categorybrandrelation:save")
    public R save(@RequestBody CategoryBrandRelationEntity categoryBrandRelation){
		//categoryBrandRelationService.save(categoryBrandRelation);
        categoryBrandRelationService.saveDetail(categoryBrandRelation);
        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    //@RequiresPermissions("product:categorybrandrelation:update")
    public R update(@RequestBody CategoryBrandRelationEntity categoryBrandRelation){
		categoryBrandRelationService.updateById(categoryBrandRelation);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    //@RequiresPermissions("product:categorybrandrelation:delete")
    public R delete(@RequestBody Long[] ids){
		categoryBrandRelationService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
