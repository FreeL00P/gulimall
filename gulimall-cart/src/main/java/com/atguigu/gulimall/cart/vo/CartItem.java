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

    @Override
    public String toString() {
        return "CartItem{" +
                "skuId=" + skuId +
                ", check=" + check +
                ", title='" + title + '\'' +
                ", skuAttr=" + skuAttr +
                ", price=" + price +
                ", count=" + count +
                ", image='" + image + '\'' +
                ", totalPrice=" + totalPrice +
                '}';
    }

    private String title;

    private List<String> skuAttr;

    private BigDecimal price;

    private Integer count;

    private String image;

    public String image() {
        return image;
    }

    public CartItem setImage(String image) {
        this.image = image;
        return this;
    }

    private BigDecimal totalPrice;

    public Long skuId() {
        return skuId;
    }

    public CartItem setSkuId(Long skuId) {
        this.skuId = skuId;
        return this;
    }

    public Boolean getCheck() {
        return check;
    }

    public CartItem setCheck(Boolean check) {
        this.check = check;
        return this;
    }

    public String getTitle() {
        return title;
    }

    public CartItem setTitle(String title) {
        this.title = title;
        return this;
    }

    public List<String> getSkuAttr() {
        return skuAttr;
    }

    public CartItem setSkuAttr(List<String> skuAttr) {
        this.skuAttr = skuAttr;
        return this;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public CartItem setPrice(BigDecimal price) {
        this.price = price;
        return this;
    }

    public Integer getCount() {
        return count;
    }

    public CartItem setCount(Integer count) {
        this.count = count;
        return this;
    }

    public BigDecimal getTotalPrice() {
        return this.price.multiply(new BigDecimal(""+this.count));
    }

    public CartItem setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
        return this;
    }

}
