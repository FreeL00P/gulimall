package com.atguigu.gulimall.ware.vo;

import lombok.Data;

import java.util.List;

/**
 * PurchaseMergeVo
 *
 * @author fj
 * @date 2022/12/17 22:19
 */
@Data
public class PurchaseMergeVo {
    private Long purchaseId;
    private List<Long> items;
}
