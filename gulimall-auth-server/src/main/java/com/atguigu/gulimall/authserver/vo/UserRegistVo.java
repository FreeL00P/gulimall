package com.atguigu.gulimall.authserver.vo;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

/**
 * UserRegistVo
 *
 * @author fj
 * @date 2022/12/30 16:17
 */
@Data
public class UserRegistVo {

    @NotEmpty(message="用户名必须填写")
    @Length(min=6,max=18,message = "用户名必须为6-18位字符")
    private String username;

    @NotEmpty(message="密码必须填写")
    @Length(min=6,max=18,message = "密码必须为6-18位字符")
    private String password;

    @NotEmpty(message = "手机号必须填写")
    @Pattern(regexp = "^1[3456789]\\d{9}$")
    private String phone;

    @NotEmpty(message="验证码必须填写")
    private String code;
}
