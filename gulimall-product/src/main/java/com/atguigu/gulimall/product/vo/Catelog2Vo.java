package com.atguigu.gulimall.product.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Catelog2Vo
 *
 * @author fj
 * @date 2022/12/21 20:11
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Catelog2Vo {
    private String catalog1Id;//一级分类id
    private List<Catelog3Vo> catalog3List;//三级子分类
    private String id;
    private String name;
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Catelog3Vo{
        private String catalog2Id;//二级分类id
        private String id;
        private String name;
    }
}
