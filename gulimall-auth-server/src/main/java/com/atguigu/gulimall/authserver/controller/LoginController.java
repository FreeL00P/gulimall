package com.atguigu.gulimall.authserver.controller;

import com.atguigu.common.utils.R;
import com.atguigu.gulimall.authserver.feign.ThirdPartFeignService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.UUID;

/**
 * LoginController
 *
 * @author fj
 * @date 2022/12/27 19:44
 */
@Controller
public class LoginController {

    @Autowired
    private ThirdPartFeignService thirdPartFeignService;

/*    @GetMapping("/login.html")
    public String loginPage(){
        return "login";
    }
    @GetMapping("/reg.html")
    public String regPage(){
        return "reg";
    }*/
    @GetMapping("/sms/sendcode")
    public R sendCode(@RequestParam("phone") String phone){
        //1、接口防刷

        //2、验证码的再次校验
        String code = UUID.randomUUID().toString().substring(0, 4);

        thirdPartFeignService.sendCode(phone,code);
        return R.ok();
    }
}
