package com.atguigu.common.constant;

/**
 * ProductConstand
 *
 * @author fj
 * @date 2022/12/14 15:24
 */
public class ProductConstant {
    public enum AttrEnum{
        ATTR_TYPE_BASE(1,"基本属性"),
        ATTR_TYPE_SALE(0,"销售属性");
        private int code;
        private String msg;
        AttrEnum(int code,String msg){
            this.code = code;
            this.msg = msg;
        }

        public Integer getCode() {
            return code;
        }

        public String msg() {
            return msg;
        }
    }
}
