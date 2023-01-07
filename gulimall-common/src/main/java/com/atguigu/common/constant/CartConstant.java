package com.atguigu.common.constant;

import lombok.Data;

/**
 * CartConstant
 *
 * @author fj
 * @date 2023/1/7 20:59
 */
@Data
public class CartConstant {

    public static final String TEMP_USER_COOKIE_NAME = "user-key";

    public static final int TEMP_USER_COOKIE_TIMEOUT=60*60*24*30;
}
