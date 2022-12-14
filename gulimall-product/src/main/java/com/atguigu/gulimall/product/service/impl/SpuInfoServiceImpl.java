package com.atguigu.gulimall.product.service.impl;

import com.atguigu.common.constant.ProductConstant;
import com.atguigu.common.es.SkuEsModel;
import com.atguigu.common.to.SkuReductionTo;
import com.atguigu.common.to.SpuBoundsTo;
import com.atguigu.common.utils.R;
import com.atguigu.common.vo.SkuHasStockVo;
import com.atguigu.gulimall.product.entity.*;
import com.atguigu.gulimall.product.feign.CouponFeignService;
import com.atguigu.gulimall.product.feign.SearchFeignService;
import com.atguigu.gulimall.product.feign.WareFeignService;
import com.atguigu.gulimall.product.service.*;
import com.atguigu.gulimall.product.vo.SpuSave.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashSet;
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
    private ProductAttrValueService attrValueService;

    @Autowired
    private SkuSaleAttrValueService skuSaleAttrValueService;
    @Autowired
    private AttrService attrService;

    @Autowired
    private SkuInfoService skuInfoService;

    @Autowired
    private SkuImagesService skuImagesService;

    @Autowired
    private BrandService brandService;

    @Autowired
    private CouponFeignService couponFeignService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private WareFeignService wareFeignService;

    @Autowired
    private SearchFeignService searchFeignService;

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
        //1?????????spu???????????? pms_spu_info
        SpuInfoEntity spuInfoEntity = new SpuInfoEntity();
        BeanUtils.copyProperties(vo,spuInfoEntity);
        this.save(spuInfoEntity);
        Long spuId = spuInfoEntity.getId();
        //2?????????spu??????????????? pms_spu_info_desc
        List<String> decript = vo.getDecript();
        spuInfoDescService.saveDescInfo(spuId,decript);
        //3?????????spu???????????? pms_spu_images
        List<String> images = vo.getImages();
        spuImagesService.saveImageInfo(spuId,images);
        //4?????????spu??????????????? pms_product_attr_value
        List<BaseAttrs> attrValue = vo.getBaseAttrs();
        attrValueService.saveAttrValue(spuId,attrValue);
        //5????????????????????? ????????????
        Bounds bounds = vo.getBounds();
        SpuBoundsTo spuBoundsTo = new SpuBoundsTo();
        BeanUtils.copyProperties(bounds,spuBoundsTo);
        spuBoundsTo.setSpuId(spuId);
        R r = couponFeignService.saveSpuBounds(spuBoundsTo);
        if (r.getCode()!=0){
           log.error("??????????????????????????????????????????");
        }
        //5???????????????spu???????????????sku??????
        //5.1???sku???????????? pms_sku_info
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
            //?????????????????????
            List<SkuImagesEntity> imagesEntities = item.getImages().stream().map(img -> {
                SkuImagesEntity skuImagesEntity = new SkuImagesEntity();
                skuImagesEntity.setSkuId(skuId);
                skuImagesEntity.setImgUrl(img.getImgUrl());
                skuImagesEntity.setDefaultImg(img.getDefaultImg());
                return skuImagesEntity;
            }).filter(entity->{
                //???????????????????????????
                return !StringUtils.isEmpty(entity.getImgUrl());
            }).collect(Collectors.toList());
            //5.2???sku??????????????? pms_sku_images
            skuImagesService.saveBatch(imagesEntities);
            //5.3???sku?????????????????? pms_sku_sale_attr_value
            List<Attr> attr = item.getAttr();
            List<SkuSaleAttrValueEntity> skuSaleAttrValueEntities = attr.stream().map(a -> {
                SkuSaleAttrValueEntity attrValueEntity = new SkuSaleAttrValueEntity();
                BeanUtils.copyProperties(a, attrValueEntity);
                attrValueEntity.setSkuId(skuId);
                return attrValueEntity;
            }).collect(Collectors.toList());
            skuSaleAttrValueService.saveBatch(skuSaleAttrValueEntities);
            //5.4???sku???????????????????????????
            SkuReductionTo skuReductionTo = new SkuReductionTo();
            BeanUtils.copyProperties(item, skuReductionTo);
            skuReductionTo.setSkuId(skuId);
            skuReductionTo.setMemberPrice(item.getMemberPrice());
            if (skuReductionTo.getFullCount()>0 && skuReductionTo.getFullPrice().compareTo(new BigDecimal(0))==1){
                R r1 = couponFeignService.saveSkuReduction(skuReductionTo);
                if (r1.getCode()!=0){
                    log.error("????????????sku??????????????????");
                }
            }


        });

    }

    @Override
    public PageUtils querySpuPage(Map<String, Object> params) {
        //??????????????????????????????????????????
        QueryWrapper<SpuInfoEntity> wrapper = new QueryWrapper<>();


        String key =(String) params.get("key");//??????????????????
        if (!StringUtils.isEmpty(key)&&!"0".equalsIgnoreCase(key)) {
            wrapper.and(w->{
                w.eq("id",key).or().like("spu_name",key);
            });
        }

        String catelogId =(String)  params.get("catelogId");//??????id
        if (!StringUtils.isEmpty(catelogId)&&!"0".equalsIgnoreCase(catelogId)){
            wrapper.eq("catalog_id",catelogId);
        }

        String brandId =(String)  params.get("brandId");//??????id
        if (!StringUtils.isEmpty(brandId)&&!"0".equalsIgnoreCase(brandId)){
            wrapper.eq("brand_id",brandId);
        }

        String status =(String)  params.get("status");//????????????
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
    public void spuUp(Long spuId) {
        //?????????????????????
        //???spuId??????attrsId
        List<ProductAttrValueEntity> baseAttrs= attrValueService.getAttrValueListBySpuId(spuId);
        //????????????attrId
        List<Long> attrIds = baseAttrs.stream().map(item->{
            return  item.getAttrId();
        }).collect(Collectors.toList());
        //?????????????????????id
        List<Long> searchAttrIds = attrService.getSearchAttrIds(attrIds);
        HashSet<Long> set = new HashSet<Long>(searchAttrIds);
        List<SkuEsModel.Attrs> attrs = baseAttrs.stream().filter(item -> {
                    return set.contains(item.getAttrId());
                }
        ).map(item->{
            SkuEsModel.Attrs attrs1 = new SkuEsModel.Attrs();
            BeanUtils.copyProperties(item,attrs1);
            return attrs1;
        }).collect(Collectors.toList());
        //??????spuId???????????????sku??????
        List<SkuInfoEntity> skuInfoEntities= skuInfoService.getSkusBySpuId(spuId);
        //??????skuIds
        List<Long> skuIds = skuInfoEntities.stream().map(SkuInfoEntity::getSkuId).collect(Collectors.toList());
        //TODO ????????????????????????
        Map<Long, Boolean> stockMap=null;
        try{

            List<SkuHasStockVo> hasStock = wareFeignService.getSkuHasStock(skuIds);
            stockMap = hasStock.stream().collect(Collectors.toMap(SkuHasStockVo::getSkuId, SkuHasStockVo::getHasStock));
        }catch (Exception e){
            e.printStackTrace();
            log.error("????????????????????????????????????{}",e);
        }
        Map<Long, Boolean> finalStockMap = stockMap;
        List<SkuEsModel> skuEsModels = skuInfoEntities.stream().map(item -> {
            SkuEsModel skuEsModel = new SkuEsModel();
            BeanUtils.copyProperties(item, skuEsModel);
            //??????????????????
            //skuPrice skuImg
            skuEsModel.setSkuPrice(item.getPrice());
            skuEsModel.setSkuImg(item.getSkuDefaultImg());
            //hasStock
             if(finalStockMap ==null){
                 skuEsModel.setHasStock(true);
             }else{
                 skuEsModel.setHasStock(finalStockMap.get(item.getSkuId()));
             }

            //????????????hotScore
            skuEsModel.setHotScore(0L);
            //brandName;brandImg
            //??????brandId??????
            BrandEntity brand = brandService.getById(item.getBrandId());
            skuEsModel.setBrandImg(brand.getLogo());
            skuEsModel.setBrandName(brand.getName());

            //catalogId,catalogName;
            CategoryEntity category = categoryService.getById(item.getCatalogId());
            skuEsModel.setCatalogName(category.getName());
            skuEsModel.setCatalogId(category.getCatId());
            //attrs
            skuEsModel.setAttrs(attrs);
            return skuEsModel;
        }).collect(Collectors.toList());
        //TODO ??????????????????ES??????
        R r = searchFeignService.productStatusUp(skuEsModels);
        if (r.getCode()==0){
            //??????????????????
            SpuInfoEntity spuInfoEntity = new SpuInfoEntity();
            spuInfoEntity.setId(spuId);
            spuInfoEntity.setPublishStatus(ProductConstant.StatusEnum.SPU_UP.getCode());
            //TODO ??????spu??????
            this.updateById(spuInfoEntity);
        }else{
            //??????????????????
            //TODO ???????????? ???????????????
        }
    }


}