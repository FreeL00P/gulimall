package com.atguigu.gulimall.cart.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * CartItem
 *
 * @author fj
 * @date 2023/1/7 20:02
 */


public class CartItem {

    private Long skuId;

    private Boolean check=true;

    private String title;

    private List<String> skuAttr;

    private BigDecimal price;

    private Integer count;

    private BigDecimal totalPrice;

    public Long skuId() {
        return skuId;
    }

    public CartItem setSkuId(Long skuId) {
        this.skuId = skuId;
        return this;
    }

    public Boolean check() {
        return check;
    }

    public CartItem setCheck(Boolean check) {
        this.check = check;
        return this;
    }

    public String title() {
        return title;
    }

    public CartItem setTitle(String title) {
        this.title = title;
        return this;
    }

    public List<String> skuAttr() {
        return skuAttr;
    }

    public CartItem setSkuAttr(List<String> skuAttr) {
        this.skuAttr = skuAttr;
        return this;
    }

    public BigDecimal price() {
        return price;
    }

    public CartItem setPrice(BigDecimal price) {
        this.price = price;
        return this;
    }

    public Integer count() {
        return count;
    }

    public CartItem setCount(Integer count) {
        this.count = count;
        return this;
    }

    public BigDecimal totalPrice() {
        return this.price.multiply(new BigDecimal(""+this.count));
    }

    public CartItem setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
        return this;
    }

}
