package com.atguigu.gulimall.order.config;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

/**
 * GuliFeignConfig
 *
 * @author fj
 * @date 2023/2/26 16:30
 */
@Configuration
public class GuliFeignConfig {

    public RequestInterceptor requestInterceptor(){
        return new RequestInterceptor(){
            @Override
            public void apply(RequestTemplate requestTemplate) {
                ServletRequestAttributes attributes=(ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
                HttpServletRequest request = attributes.getRequest();//老请求
                if (request!=null) {
                    String cookie = request.getHeader("Cookie");
                    //给新请求同步Cookie数据
                    requestTemplate.header("Cookie",cookie);
                }
                System.out.println("feign远程之前先进行RequestInterceptor.apply");
            }
        };
    }

}
