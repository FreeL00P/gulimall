package com.atguigu.gulimall.ware.vo;

import lombok.Data;

/**
 * PurchaseItemVo
 *
 * @author fj
 * @date 2022/12/18 12:42
 */
@Data
public class PurchaseItemVo {
    private Long itemId;//采购项id
    private Integer status;//采购状态
    private String reason;//原因
}
