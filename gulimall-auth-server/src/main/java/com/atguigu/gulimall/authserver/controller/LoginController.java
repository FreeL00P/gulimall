package com.atguigu.gulimall.authserver.controller;

import com.alibaba.fastjson.TypeReference;
import com.atguigu.common.constant.AuthServerConstant;
import com.atguigu.common.exception.BizCodeEnum;
import com.atguigu.common.utils.R;
import com.atguigu.common.vo.MemberResponseVo;
import com.atguigu.gulimall.authserver.feign.MemberFeignService;
import com.atguigu.gulimall.authserver.feign.ThirdPartFeignService;
import com.atguigu.gulimall.authserver.vo.UserLoginVo;
import com.atguigu.gulimall.authserver.vo.UserRegistVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.atguigu.common.constant.AuthServerConstant.SMS_CODE_CACHE_PREFIX;

/**
 * LoginController
 *
 * @author fj
 * @date 2022/12/27 19:44
 */
@Controller
@Slf4j
public class LoginController {

    @Autowired
    private ThirdPartFeignService thirdPartFeignService;

    @Autowired
    private MemberFeignService memberFeignService;

    @Autowired
    private RedisTemplate redisTemplate;

/*    @GetMapping("/login.html")
    public String loginPage(){
        return "login";
    }
    @GetMapping("/reg.html")
    public String regPage(){
        return "reg";
    }*/
    @GetMapping("/sms/sendcode")
    @ResponseBody
    public R sendCode(@RequestParam("phone") String phone){
        //在缓存中查验证码
        String redisCode = (String) redisTemplate.opsForValue().get(SMS_CODE_CACHE_PREFIX + phone);
        //分割redisCode
        if (!StringUtils.isEmpty(redisCode)){
            long l = Long.parseLong(redisCode.split("_")[1]);
            if(System.currentTimeMillis()-l<60*1000){
                //60s内不能再发
                return R.error(BizCodeEnum.SMS_CODE_EXCEPTION.getCode(), BizCodeEnum.SMS_CODE_EXCEPTION.getMsg());
            }
        }

        //1、接口防刷

        //2、验证码的再次校验  redis key-phone value-code 后面接上保存时间，用来判断用户是否在60s内获取重复多次验证码
        String code = UUID.randomUUID().toString().substring(0, 5)+"_"+System.currentTimeMillis();
        //redis缓存验证码
        redisTemplate.opsForValue().set(AuthServerConstant.SMS_CODE_CACHE_PREFIX+phone,
                code,
                10,
                TimeUnit.MINUTES);
        thirdPartFeignService.sendCode(phone,code);
        return R.ok();
    }

    @PostMapping("/regist")
    public String regist(@Valid UserRegistVo registVo, BindingResult result, RedirectAttributes redirectAttributes){
        if (result.hasErrors()) {
            Map<String, String> errors = result.getFieldErrors().stream()
                    .collect(Collectors.toMap(FieldError::getField, FieldError::getDefaultMessage));

            redirectAttributes.addFlashAttribute("errors", errors);
            //如果校验出错，重定向到注册页
            //重定向携带数据，利用session原理。将数据放在session中 只要跳到下一个页面取出这个数据后，session里的数据就会删除
            //TODO:分布式下的session问题
            return "redirect:http://auth.gulimall.com/reg.html";
        }
        //调用远程服务进行注册
        //1、校验验证码
        String code = registVo.getCode();
        String redisCode =(String) redisTemplate.opsForValue().get(AuthServerConstant.SMS_CODE_CACHE_PREFIX + registVo.getPhone());
        if (!StringUtils.isEmpty(redisCode)) {
            if (code.equals(redisCode.split("_")[0])) {
                //验证码校验成功
                R r = memberFeignService.regist(registVo);
                if (r.getCode()==0){
                    //成功
                    //删除验证码
                    redisTemplate.delete(AuthServerConstant.SMS_CODE_CACHE_PREFIX + registVo.getPhone());
                    return "redirect:http://auth.gulimall.com/login.html";
                }else {
                    //失败
                    Map<String, String> errors = new HashMap<String, String>();
                    errors.put("msg",r.getMsg());
                    redirectAttributes.addFlashAttribute("errors", errors);
                    return "redirect:http://auth.gulimall.com/reg.html";
                }
            }else{
                Map<String, String> errors = new HashMap<String, String>();
                errors.put("code","验证码错误");
                redirectAttributes.addFlashAttribute("errors", errors);
                return "redirect:http://auth.gulimall.com/reg.html";
            }
        }else{
            Map<String, String> errors = new HashMap<String, String>();
            errors.put("code","验证码错误");
            redirectAttributes.addFlashAttribute("errors", errors);
            return "redirect:http://auth.gulimall.com/reg.html";
        }

    }

    @PostMapping("/login")
    public String login(UserLoginVo vo,RedirectAttributes redirectAttributes, HttpSession session){
        //远程调用登陆
        R r = memberFeignService.login(vo);
        if (r.getCode()==0){
            //成功
            MemberResponseVo data = r.getData("data", new TypeReference<MemberResponseVo>() {
            });
            session.setAttribute(AuthServerConstant.LOGIN_USER,data);
            return "redirect:http://gulimall.com";
        }else {
            Map<String,String> map=new HashMap<String,String>();
            map.put("msg",r.getMsg());
            redirectAttributes.addFlashAttribute("errors",map);
            return "redirect:http://auth.gulimall.com/login.html";
        }

    }

    @GetMapping("/login.html")
    public String loginPage(HttpSession session){
        Object attribute = session.getAttribute(AuthServerConstant.LOGIN_USER);
        if (attribute == null){
            //未登陆
            return "login";
        }else{
            return "redirect:http://gulimall.com";
        }

    }
}
