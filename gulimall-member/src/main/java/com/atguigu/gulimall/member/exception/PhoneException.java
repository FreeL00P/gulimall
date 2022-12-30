package com.atguigu.gulimall.authserver.exception;

/**
 *
 * @author fj
 * @date 2022/12/30 16:17
 */
public class PhoneException extends RuntimeException {

    public PhoneException() {
        super("存在相同的手机号");
    }
}
