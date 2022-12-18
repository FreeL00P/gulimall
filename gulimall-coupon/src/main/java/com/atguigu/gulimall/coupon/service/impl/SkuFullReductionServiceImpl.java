package com.atguigu.gulimall.coupon.service.impl;

import com.atguigu.common.to.MemberPrice;
import com.atguigu.common.to.SkuReductionTo;
import com.atguigu.gulimall.coupon.entity.MemberPriceEntity;
import com.atguigu.gulimall.coupon.entity.SkuLadderEntity;
import com.atguigu.gulimall.coupon.service.MemberPriceService;
import com.atguigu.gulimall.coupon.service.SkuLadderService;
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

import com.atguigu.gulimall.coupon.dao.SkuFullReductionDao;
import com.atguigu.gulimall.coupon.entity.SkuFullReductionEntity;
import com.atguigu.gulimall.coupon.service.SkuFullReductionService;


@Service("skuFullReductionService")
public class SkuFullReductionServiceImpl extends ServiceImpl<SkuFullReductionDao, SkuFullReductionEntity> implements SkuFullReductionService {

    @Autowired
    private SkuLadderService skuLadderService;

    @Autowired
    private MemberPriceService memberPriceService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SkuFullReductionEntity> page = this.page(
                new Query<SkuFullReductionEntity>().getPage(params),
                new QueryWrapper<SkuFullReductionEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public void saveSkuReduction(SkuReductionTo skuReductionTo) {

        SkuLadderEntity skuLadderEntity = new SkuLadderEntity();
        skuLadderEntity.setSkuId(skuReductionTo.getSkuId());
        skuLadderEntity.setFullCount(skuReductionTo.getFullCount());
        skuLadderEntity.setDiscount(skuReductionTo.getDiscount());
        skuLadderEntity.setAddOther(skuReductionTo.getCountStatus());
        if(skuReductionTo.getFullCount()>0){
            skuLadderService.save(skuLadderEntity);
        }


        SkuFullReductionEntity skuFullReductionEntity = new SkuFullReductionEntity();
        BeanUtils.copyProperties(skuReductionTo, skuFullReductionEntity);
        if(skuFullReductionEntity.getFullPrice().compareTo(new BigDecimal(0))==1){
            this.save(skuFullReductionEntity);
        }


        List<MemberPrice> memberPrice = skuReductionTo.getMemberPrice();
        if(memberPrice.size()!= 0&&memberPrice!=null){
            List<MemberPriceEntity> collect = memberPrice.stream().map(item -> {
                MemberPriceEntity priceEntity = new MemberPriceEntity();
                priceEntity.setSkuId(skuReductionTo.getSkuId());
                priceEntity.setMemberLevelId(item.getId());
                priceEntity.setMemberLevelName(item.getName());
                priceEntity.setMemberPrice(item.getPrice());
                priceEntity.setAddOther(1);
                return priceEntity;
            }).filter(item->{
                return item.getMemberPrice().compareTo(new BigDecimal(0))==1;
            }).collect(Collectors.toList());
            if(collect!= null){
                memberPriceService.saveBatch(collect);
            }

        }



    }

}