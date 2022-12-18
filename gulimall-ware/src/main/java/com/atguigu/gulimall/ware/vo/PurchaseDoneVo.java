package com.atguigu.gulimall.ware.vo;

import lombok.Data;

import java.util.List;

/**
 * PurchaseDoneVo
 *
 * @author fj
 * @date 2022/12/18 12:40
 */
@Data
public class PurchaseDoneVo {
    private Long id;//采购单id
    private List<PurchaseItemVo> items;
}
