package com.atguigu.gulimall.cart.vo;

import java.math.BigDecimal;
import java.util.List;

/**
 * Cart
 *
 * @author fj
 * @date 2023/1/7 20:01
 */
public class Cart {

    private List<CartItem> items;

    private Integer countNum;//商品数量

    private Integer countType;//商品类型数量

    private BigDecimal totalAmount;//商品总价

    private BigDecimal reduce=new BigDecimal("0.00");//减免价格

    public List<CartItem> items() {
        return items;
    }

    public Cart setItems(List<CartItem> items) {
        this.items = items;
        return this;
    }

    public Integer countNum() {
        countNum=0;
        if (items == null&&items.size()>0){
            for (CartItem item : items) {
                countNum += item.count();
            }
        }
        return countNum;
    }

    public Cart setCountNum(Integer countNum) {
        this.countNum = countNum;
        return this;
    }

    public Integer countType() {
        return countType;
    }

    public Cart setCountType(Integer countType) {
        this.countType = countType;
        return this;
    }

    public BigDecimal totalAmount() {
        BigDecimal amount = new BigDecimal("0");
        if (items == null&&items.size()>0){
            for (CartItem item : items) {
                BigDecimal totalPrice = item.totalPrice();
                amount=amount.add(totalPrice);
            }
        }
        //减去优惠金额
        amount=amount.subtract(this.reduce());
        return amount;
    }

    public Cart setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
        return this;
    }

    public BigDecimal reduce() {
        return reduce;
    }

    public Cart setReduce(BigDecimal reduce) {
        this.reduce = reduce;
        return this;
    }
}
