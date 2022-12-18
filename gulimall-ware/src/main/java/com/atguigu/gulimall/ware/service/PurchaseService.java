package com.atguigu.gulimall.ware.service;

import com.atguigu.gulimall.ware.vo.PurchaseDoneVo;
import com.atguigu.gulimall.ware.vo.PurchaseMergeVo;
import com.baomidou.mybatisplus.extension.service.IService;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.gulimall.ware.entity.PurchaseEntity;

import java.util.List;
import java.util.Map;

/**
 * 采购信息
 *
 * @author freeL00P
 * @email 1084472249@qq.com
 * @date 2022-12-06 17:11:39
 */
public interface PurchaseService extends IService<PurchaseEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void mergePurchase(PurchaseMergeVo vo);

    PageUtils queryPageUnreceivePurchase(Map<String, Object> params);

    void receivedByIds(List<Long> ids);

    void purchaseDone(PurchaseDoneVo doneVo);
}

