package com.atguigu.gulimall.order.config;

import com.atguigu.gulimall.order.interceptor.LoginUserInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * OrderWebConfiguration
 *
 * @author fj
 * @date 2023/2/25 17:28
 */
@Configuration
public class OrderWebConfiguration implements WebMvcConfigurer {

    @Autowired
    LoginUserInterceptor loginUserInterceptor;
    //添加一个拦截器
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
       registry.addInterceptor(loginUserInterceptor).addPathPatterns("/**");
    }
}
