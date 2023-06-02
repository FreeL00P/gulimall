package com.atguigu.gulimall.order.vo;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

/**
 * OrderConfirmVo
 *
 * @author fj
 * @date 2023/2/25 20:22
 */
//订单确认页需要用到的数据
public class OrderConfirmVo {

    //收货地址
    @Getter @Setter
    List<MemberAddressVo> address;

    @Getter @Setter
    //选中的购物项
    List<OrderItemVo> items;

    //发票记录

    @Getter @Setter
    //优惠卷信息
    Integer integration;

    //订单总额
    BigDecimal total;

    public Integer getCount() {
        Integer i = 0;
        if (items != null) {
            for (OrderItemVo item : items) {
                i += item.getCount();
            }
        }
        return i;
    }
    public BigDecimal total() {
        BigDecimal sum = new BigDecimal("0");
        if (items!=null){
            for (OrderItemVo item : items) {
                BigDecimal decimal = item.getPrice().multiply(new BigDecimal(item.getCount().toString()));
                sum=sum.add(decimal);
            }
        }

        return total;
    }

    BigDecimal payPrice;//应付价格

    public BigDecimal payPrice() {
        return total;
    }
    //防重令牌
    String orderToken;
}
