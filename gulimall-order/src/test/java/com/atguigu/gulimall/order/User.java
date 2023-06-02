package com.atguigu.gulimall.order;

import lombok.Data;
import lombok.ToString;

/**
 * User
 *
 * @author fj
 * @date 2023/2/13 22:00
 */
@Data
@ToString
public class User {
    private String username;

    public User(String username) {
        this.username = username;
    }
}
