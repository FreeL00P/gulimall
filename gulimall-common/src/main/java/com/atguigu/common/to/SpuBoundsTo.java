package com.atguigu.common.to;

import lombok.Data;

import java.math.BigDecimal;

/**
 * SpuBoundsTo
 *
 * @author fj
 * @date 2022/12/17 14:00
 */
@Data
public class SpuBoundsTo {
    private Long spuId;
    private BigDecimal buyBounds;
    private BigDecimal growBounds;
}
