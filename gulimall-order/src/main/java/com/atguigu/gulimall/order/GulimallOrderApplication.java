package com.atguigu.gulimall.order;

import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

/**
 * 使用rabbitMQ
 * 1、引入amqp场景RabbitAutoConfiguration就会自动生效
 * 2、给容器中自动配置；RabbitTemplate、AmqbAdmin等
 * 3、给配置文件配置spring.rabbitmq信息
 * 4、@EnableRabbit开启功能
 * 5、监听消息使用@RabbitListener；必须有@EnableRabbit
 * @RabbitListener 标注在类和方法上
 * @RabbitHandler 标注在方法上，消息接收策略
 *
 */
@EnableRabbit
@SpringBootApplication
@EnableRedisHttpSession
@EnableDiscoveryClient
@EnableFeignClients
public class GulimallOrderApplication {

    public static void main(String[] args) {
        SpringApplication.run(GulimallOrderApplication.class, args);
    }

}
