package com.atguigu.gulimall.member.exception;

/**
 *
 * @author fj
 * @date 2022/12/30 16:17
 */
public class UsernameException extends RuntimeException {


    public UsernameException() {
        super("存在相同的用户名");
    }
}
