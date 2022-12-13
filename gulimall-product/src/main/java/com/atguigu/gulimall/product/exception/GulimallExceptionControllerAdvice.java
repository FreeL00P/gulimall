package com.atguigu.gulimall.product.exception;

import com.atguigu.common.exception.BizCodeEnum;
import com.atguigu.common.utils.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

/**
 * GulimallExceptionControllerAdvice
 *
 * @author fj
 * @date 2022/12/12 21:31
 */
@Slf4j
@ResponseBody
@ControllerAdvice(basePackages = "com.atguigu.gulimall.product.controller")
public class GulimallExceptionControllerAdvice {

    @ExceptionHandler(value= MethodArgumentNotValidException.class)
    public R handleVaildException(MethodArgumentNotValidException e){
        log.error("数据校验出现问题{}，异常类型为{}",e.getMessage(),e.getClass());
        BindingResult result = e.getBindingResult();
        Map<String, String> map = new HashMap<String, String>();
        result.getFieldErrors().forEach((item)->{
            //获取校验的错误信息
            //获取发送错误的属性名
            map.put(item.getField(),item.getDefaultMessage());
        });
        return R.error(BizCodeEnum.VAILD_EXCEPTION.getCode(),BizCodeEnum.VAILD_EXCEPTION.getMsg()).put("data",map);
    }

    //处理任意类型异常
    @ExceptionHandler(value= Throwable.class)
    public R handleException(Throwable throwable){
        return R.error(BizCodeEnum.UNKNOW_EXCEPTION.getCode(), BizCodeEnum.UNKNOW_EXCEPTION.getMsg());
    }

}
