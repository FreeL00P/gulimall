package com.atguigu.gulimall.cart.to;

import lombok.Data;

/**
 * UserInfoVo
 *
 * @author fj
 * @date 2023/1/7 20:52
 */
@Data
public class UserInfoTo {

    private Long userId;

    private String userKey;

    private boolean tempUser=false;

}
