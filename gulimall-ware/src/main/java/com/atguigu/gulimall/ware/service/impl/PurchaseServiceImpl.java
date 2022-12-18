package com.atguigu.gulimall.ware.service.impl;

import com.atguigu.common.constant.WareConstant;
import com.atguigu.gulimall.ware.dao.PurchaseDetailDao;
import com.atguigu.gulimall.ware.entity.PurchaseDetailEntity;
import com.atguigu.gulimall.ware.service.PurchaseDetailService;
import com.atguigu.gulimall.ware.service.WareSkuService;
import com.atguigu.gulimall.ware.vo.PurchaseDoneVo;
import com.atguigu.gulimall.ware.vo.PurchaseItemVo;
import com.atguigu.gulimall.ware.vo.PurchaseMergeVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.common.utils.Query;

import com.atguigu.gulimall.ware.dao.PurchaseDao;
import com.atguigu.gulimall.ware.entity.PurchaseEntity;
import com.atguigu.gulimall.ware.service.PurchaseService;
import org.springframework.transaction.annotation.Transactional;


@Service("purchaseService")
public class PurchaseServiceImpl extends ServiceImpl<PurchaseDao, PurchaseEntity> implements PurchaseService {

    @Autowired
    private PurchaseDetailService purchaseDetailService;

    @Autowired
    private WareSkuService wareSkuService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<PurchaseEntity> page = this.page(
                new Query<PurchaseEntity>().getPage(params),
                new QueryWrapper<PurchaseEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    @Transactional
    public void mergePurchase(PurchaseMergeVo vo) {
        Long purchaseId = vo.getPurchaseId();
        if (purchaseId == null) {
            PurchaseEntity purchaseEntity = new PurchaseEntity();
            purchaseEntity.setStatus(WareConstant.PurchaseStatusEnum.CREATED.getCode());
            this.save(purchaseEntity);
            purchaseId = vo.getPurchaseId();
        }
        List<Long> ids = vo.getItems();
        Long finalPurchaseId = purchaseId;
        List<PurchaseDetailEntity> collect = ids.stream().map(id -> {
            PurchaseDetailEntity entity = new PurchaseDetailEntity();
            entity.setPurchaseId(finalPurchaseId);
            entity.setId(id);
            entity.setStatus(WareConstant.PurchaseDetailStatusEnum.ASSIGNED.getCode());
            return entity;
        }).collect(Collectors.toList());
        purchaseDetailService.updateBatchById(collect);


    }
    //查询未领取的采购单
    @Override
    public PageUtils queryPageUnreceivePurchase(Map<String, Object> params) {
        IPage<PurchaseEntity> page = this.page(
                new Query<PurchaseEntity>().getPage(params),
                new QueryWrapper<PurchaseEntity>().eq("status",0)
                        .or().eq("status",1)
        );
        return new PageUtils(page);
    }
    //领取采购单

    @Override
    public void receivedByIds(List<Long> ids) {
        //确认当前采购单是新建或者已分配状态
        List<PurchaseEntity> collect = ids.stream().map(id -> {
            PurchaseEntity byId = this.getById(id);
            return byId;
        }).filter(item -> {
            if (item.getStatus() == WareConstant.PurchaseStatusEnum.CREATED.getCode() || item.getStatus() == WareConstant.PurchaseStatusEnum.ASSIGNED.getCode()) {
                return true;
            } else {
                return false;
            }
        }).map(item -> {
            //改变采购单状态
            item.setStatus(WareConstant.PurchaseStatusEnum.RECEIVE.getCode());
            return item;
        }).collect(Collectors.toList());
        //更新状态
        this.updateBatchById(collect);
        //改变采购项状态
        collect.forEach(item->{
            //根据采购单id获取所有采购项
            List<PurchaseDetailEntity> purchaseDetailList= purchaseDetailService.listDetailByPurchaseId(item.getId());
            //设置采购项状态
            List<PurchaseDetailEntity> collect1 = purchaseDetailList.stream().map(detail -> {
                PurchaseDetailEntity entity = new PurchaseDetailEntity();
                entity.setId(detail.getId());
                entity.setStatus(WareConstant.PurchaseDetailStatusEnum.BUYING.getCode());
                return entity;
            }).collect(Collectors.toList());
            purchaseDetailService.updateBatchById(collect1);
        });
    }

    @Override
    @Transactional
    public void purchaseDone(PurchaseDoneVo doneVo) {
        //改变采购项状态
        boolean flag=true;
        List<PurchaseItemVo> items = doneVo.getItems();
        List<PurchaseDetailEntity> detailList=new ArrayList<PurchaseDetailEntity>();
        for (PurchaseItemVo item : items) {
            PurchaseDetailEntity purchaseDetailEntity = new PurchaseDetailEntity();
            //判断采购项状态，如果有一项采购失败，则采购项的状态也为失败
            if(item.getStatus()==WareConstant.PurchaseDetailStatusEnum.HASERROR.getCode()){
                flag=false;
                purchaseDetailEntity.setStatus(item.getStatus());
            }else {
                //采购成功，设置采购状态
                purchaseDetailEntity.setStatus(WareConstant.PurchaseDetailStatusEnum.FINISH.getCode());
                //将成功采购的项，添加到库存
                PurchaseDetailEntity entity = purchaseDetailService.getById(item.getItemId());
                wareSkuService.addStock(entity.getSkuId(), entity.getWareId(),entity.getSkuNum());

            }
            purchaseDetailEntity.setId(item.getItemId());
            detailList.add(purchaseDetailEntity);
        }
        purchaseDetailService.updateBatchById(detailList);
        //设置采购单状态
        PurchaseEntity purchaseEntity = new PurchaseEntity();
        purchaseEntity.setId(doneVo.getId());
        if(flag){//success
            purchaseEntity.setStatus(WareConstant.PurchaseStatusEnum.FINISH.getCode());
        }else{
            purchaseEntity.setStatus(WareConstant.PurchaseStatusEnum.HASERROR.getCode());
        }
        this.updateById(purchaseEntity);
    }


}