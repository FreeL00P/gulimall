package com.atguigu.gulimall.order.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * OrderItemVo
 *
 * @author fj
 * @date 2023/2/25 20:34
 */
@Data
public class OrderItemVo {

    private Long skuId;

    private Boolean check=true;

    private String title;

    private List<String> skuAttr;

    private BigDecimal price;

    private Integer count;

    private String image;

    private BigDecimal totalPrice;

    private boolean hasStock;
    private BigDecimal weight;
}
