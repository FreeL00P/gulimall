package com.atguigu.gulimall.product.vo;

import lombok.Data;

/**
 * AttrRespVo
 *
 * @author fj
 * @date 2022/12/14 11:21
 */
@Data
public class AttrRespVo extends AttrVo{
    /**
     * 所属分类名字
     */
    private String catelogName;
    /**
     * 所属组名字
     */
    private String groupName;

    /**
     * 完整分类路径
     */
    private Long[] catelogPath;
}
