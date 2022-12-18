package com.atguigu.gulimall.ware.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.atguigu.gulimall.ware.dao.PurchaseDetailDao;
import com.atguigu.gulimall.ware.service.PurchaseDetailService;
import com.atguigu.gulimall.ware.vo.PurchaseDoneVo;
import com.atguigu.gulimall.ware.vo.PurchaseMergeVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.atguigu.gulimall.ware.entity.PurchaseEntity;
import com.atguigu.gulimall.ware.service.PurchaseService;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.common.utils.R;



/**
 * 采购信息
 *
 * @author freeL00P
 * @email 1084472249@qq.com
 * @date 2022-12-06 17:11:39
 */
@RestController
@RequestMapping("ware/purchase")
public class PurchaseController {
    @Autowired
    private PurchaseService purchaseService;

    /**
     * 列表
     */
    @RequestMapping("/list")
    //@RequiresPermissions("ware:purchase:list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = purchaseService.queryPage(params);

        return R.ok().put("page", page);
    }
    /**
     * 查询未领取的采购单
     */
    @GetMapping("/unreceive/list")
    public R purchaseUnReceive(@RequestParam Map<String, Object> params){
        PageUtils page = purchaseService.queryPageUnreceivePurchase(params);

        return R.ok().put("page", page);
    }
    /**
     * 领取采购单
     */
    @PostMapping("/received")
    public R received(@RequestBody List<Long> ids){
        purchaseService.receivedByIds(ids);
        return R.ok();
    }
    /**
     * 合并采购单
     */
    @PostMapping ("/merge")
    public R merge(@RequestBody PurchaseMergeVo vo){
        purchaseService.mergePurchase(vo);
        return R.ok();
    }
    /**
     * 完成采购单
     */
    @PostMapping ("/done")
    public R done(@RequestBody PurchaseDoneVo doneVo){
        purchaseService.purchaseDone(doneVo);
        return R.ok();
    }
    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    //@RequiresPermissions("ware:purchase:info")
    public R info(@PathVariable("id") Long id){
		PurchaseEntity purchase = purchaseService.getById(id);

        return R.ok().put("purchase", purchase);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    //@RequiresPermissions("ware:purchase:save")
    public R save(@RequestBody PurchaseEntity purchase){
		purchaseService.save(purchase);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    //@RequiresPermissions("ware:purchase:update")
    public R update(@RequestBody PurchaseEntity purchase){
		purchaseService.updateById(purchase);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    //@RequiresPermissions("ware:purchase:delete")
    public R delete(@RequestBody Long[] ids){
		purchaseService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
