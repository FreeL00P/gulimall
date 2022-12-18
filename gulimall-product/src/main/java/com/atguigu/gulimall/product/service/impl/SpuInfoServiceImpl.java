package com.atguigu.gulimall.product.service.impl;

import com.atguigu.common.to.SkuReductionTo;
import com.atguigu.common.to.SpuBoundsTo;
import com.atguigu.common.utils.R;
import com.atguigu.gulimall.product.entity.*;
import com.atguigu.gulimall.product.feign.CouponFeignService;
import com.atguigu.gulimall.product.service.*;
import com.atguigu.gulimall.product.vo.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.common.utils.Query;

import com.atguigu.gulimall.product.dao.SpuInfoDao;
import org.springframework.util.StringUtils;


@Service("spuInfoService")
public class SpuInfoServiceImpl extends ServiceImpl<SpuInfoDao, SpuInfoEntity> implements SpuInfoService {

    @Autowired
    SpuInfoDescService spuInfoDescService;

    @Autowired
    private SpuImagesService spuImagesService;

    @Autowired
    private ProductAttrValueService productAttrValueService;

    @Autowired
    private SkuInfoService skuInfoService;

    @Autowired
    private SkuImagesService skuImagesService;

    @Autowired
    private SkuSaleAttrValueService attrValueService;

    @Autowired
    private CouponFeignService couponFeignService;
    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SpuInfoEntity> page = this.page(
                new Query<SpuInfoEntity>().getPage(params),
                new QueryWrapper<SpuInfoEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public void saveSpuInfo(SpuSaveVo vo) {
        //1、保存spu基本信息 pms_spu_info
        SpuInfoEntity spuInfoEntity = new SpuInfoEntity();
        BeanUtils.copyProperties(vo,spuInfoEntity);
        this.save(spuInfoEntity);
        Long spuId = spuInfoEntity.getId();
        //2、保存spu的描述图片 pms_spu_info_desc
        List<String> decript = vo.getDecript();
        spuInfoDescService.saveDescInfo(spuId,decript);
        //3、保存spu的图片集 pms_spu_images
        List<String> images = vo.getImages();
        spuImagesService.saveImageInfo(spuId,images);
        //4、保存spu的规格参数 pms_product_attr_value
        List<BaseAttrs> attrValue = vo.getBaseAttrs();
        productAttrValueService.saveAttrValue(spuId,attrValue);
        //5、保存积分信息 远程调用
        Bounds bounds = vo.getBounds();
        SpuBoundsTo spuBoundsTo = new SpuBoundsTo();
        BeanUtils.copyProperties(bounds,spuBoundsTo);
        spuBoundsTo.setSpuId(spuId);
        R r = couponFeignService.saveSpuBounds(spuBoundsTo);
        if (r.getCode()!=0){
           log.error("远程服务调用保存积分信息失败");
        }
        //5、保存当前spu对应的所有sku信息
        //5.1、sku基本信息 pms_sku_info
        List<Skus> skus = vo.getSkus();
        skus.stream().forEach(item->{
            String defaultImg="";
            for (Images img : item.getImages()) {
                if (img.getDefaultImg()==1){
                    defaultImg=img.getImgUrl();
                }
            }
            SkuInfoEntity skuInfoEntity = new SkuInfoEntity();
            BeanUtils.copyProperties(item, skuInfoEntity);
            skuInfoEntity.setSkuDesc(String.join(",",item.getDescar()));
            skuInfoEntity.setBrandId(spuInfoEntity.getBrandId());
            skuInfoEntity.setCatalogId(spuInfoEntity.getCatalogId());
            skuInfoEntity.setSaleCount(0L);
            skuInfoEntity.setSpuId(spuId);
            skuInfoEntity.setSkuDefaultImg(defaultImg);
            skuInfoService.saveSkuInfo(skuInfoEntity);
            Long skuId = skuInfoEntity.getSkuId();
            //查找出默认图片
            List<SkuImagesEntity> imagesEntities = item.getImages().stream().map(img -> {
                SkuImagesEntity skuImagesEntity = new SkuImagesEntity();
                skuImagesEntity.setSkuId(skuId);
                skuImagesEntity.setImgUrl(img.getImgUrl());
                skuImagesEntity.setDefaultImg(img.getDefaultImg());
                return skuImagesEntity;
            }).filter(entity->{
                //过滤没有图片的数据
                return !StringUtils.isEmpty(entity.getImgUrl());
            }).collect(Collectors.toList());
            //5.2、sku的图片信息 pms_sku_images
            skuImagesService.saveBatch(imagesEntities);
            //5.3、sku销售属性信息 pms_sku_sale_attr_value
            List<Attr> attr = item.getAttr();
            List<SkuSaleAttrValueEntity> skuSaleAttrValueEntities = attr.stream().map(a -> {
                SkuSaleAttrValueEntity attrValueEntity = new SkuSaleAttrValueEntity();
                BeanUtils.copyProperties(a, attrValueEntity);
                attrValueEntity.setSkuId(skuId);
                return attrValueEntity;
            }).collect(Collectors.toList());
            attrValueService.saveBatch(skuSaleAttrValueEntities);
            //5.4、sku的优惠满减属性信息
            SkuReductionTo skuReductionTo = new SkuReductionTo();
            BeanUtils.copyProperties(item, skuReductionTo);
            skuReductionTo.setSkuId(skuId);
            skuReductionTo.setMemberPrice(item.getMemberPrice());
            if (skuReductionTo.getFullCount()>0 && skuReductionTo.getFullPrice().compareTo(new BigDecimal(0))==1){
                R r1 = couponFeignService.saveSkuReduction(skuReductionTo);
                if (r1.getCode()!=0){
                    log.error("远程保存sku优惠信息失败");
                }
            }


        });

    }

    @Override
    public PageUtils querySpuPage(Map<String, Object> params) {
        //判断前台是否传了某些特定参数
        QueryWrapper<SpuInfoEntity> wrapper = new QueryWrapper<>();


        String key =(String) params.get("key");//模糊查询参数
        if (!StringUtils.isEmpty(key)&&!"0".equalsIgnoreCase(key)) {
            wrapper.and(w->{
                w.eq("id",key).or().like("spu_name",key);
            });
        }

        String catelogId =(String)  params.get("catelogId");//分类id
        if (!StringUtils.isEmpty(catelogId)&&!"0".equalsIgnoreCase(catelogId)){
            wrapper.eq("catalog_id",catelogId);
        }

        String brandId =(String)  params.get("brandId");//品牌id
        if (!StringUtils.isEmpty(brandId)&&!"0".equalsIgnoreCase(brandId)){
            wrapper.eq("brand_id",brandId);
        }

        String status =(String)  params.get("status");//商品状态
        if (!StringUtils.isEmpty(status)&&!"0".equalsIgnoreCase(status)){
            wrapper.eq("publish_status",status);
        }

        IPage<SpuInfoEntity> page = this.page(
                new Query<SpuInfoEntity>().getPage(params),
                wrapper
        );
        return new PageUtils(page);
    }

    @Override
    public void spuUpById(Long spuId) {
        SpuInfoEntity spuInfoEntity = new SpuInfoEntity();
        spuInfoEntity.setId(spuId);
        spuInfoEntity.setPublishStatus(1);
        this.updateById(spuInfoEntity);
    }

}