package com.atguigu.gulimall.product;

import com.atguigu.gulimall.product.entity.BrandEntity;
import com.atguigu.gulimall.product.entity.SkuImagesEntity;
import com.atguigu.gulimall.product.service.*;
import com.atguigu.gulimall.product.vo.SkuItemSaleAttrVo;
import com.atguigu.gulimall.product.vo.SpuItemAttrGroupVo;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.List;
import java.util.UUID;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = GulimallProductApplication.class)
class GulimallProductApplicationTests {

    @Resource
    private BrandService brandService;

    @Autowired
    private SkuImagesService imagesService;

    @Autowired
    private SkuSaleAttrValueService skuSaleAttrValueService;

    @Autowired
    private AttrGroupService attrGroupService;

    @Test
    public void testApi(){
//        List<SkuImagesEntity> imagesBySkuId = imagesService.getImagesBySkuId(16L);
//        System.out.println("imagesBySkuId = " + imagesBySkuId);
//        List<SkuItemSaleAttrVo> saleAttrsBySpuId = skuSaleAttrValueService.getSaleAttrsBySpuId(13L);
//        System.out.println("saleAttrsBySpuId = " + saleAttrsBySpuId);
        List<SpuItemAttrGroupVo> withAttrsBySpuId = attrGroupService.getAttrgroupWithAttrsBySpuId(13L, 225L);
        System.out.println("withAttrsBySpuId = " + withAttrsBySpuId);
    }
    @Test
    public void contextLoads() {
//        BrandEntity brandEntity = new BrandEntity();
//        brandEntity.setDescript(" code");
//        brandEntity.setName("name");
//        brandService.save(brandEntity);
//        System.out.println("SUCCESSFUL");
        List<BrandEntity> list = brandService.list(new QueryWrapper<BrandEntity>().eq("name", "name"));
        System.out.println(list.toString());
    }

    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Test
    public void testStringRedisTemplate(){
        ValueOperations<String, String> ops = stringRedisTemplate.opsForValue();
        ops.set("hello", "world_"+ UUID.randomUUID().toString());
        String hello = ops.get("hello");
        System.out.println("保存的数据是= " + hello);
    }
}
