package com.atguigu.gulimall.member.vo;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;

/**
 * UserRegistVo
 *
 * @author fj
 * @date 2022/12/30 16:17
 */
@Data
public class MemberRegistVo {


    private String username;


    private String password;


    private String phone;

}
