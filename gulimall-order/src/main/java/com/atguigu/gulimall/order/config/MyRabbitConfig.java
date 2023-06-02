package com.atguigu.gulimall.order.config;

import org.springframework.amqp.core.ReturnedMessage;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

/**
 * MyRabbitConfig
 *
 * @author fj
 * @date 2023/2/13 21:24
 */
@Configuration
public class MyRabbitConfig {

    @Autowired
    RabbitTemplate rabbitTemplate;

    /**
     * 发送的对象消息转换为json
     * @return
     */
//    @Bean
//    public MessageConverter messageConverter(){
//        return new Jackson2JsonMessageConverter();
//    }

    /**
     * 定制RabbitTemplate
     * 1服务端接收到消息回调
     * 2 消息正确抵达队列进行回调
     * 3 消费端确认（保证每个消息都能够被正确消费，此时才可以让broker删除这个消息）
     *    1 消息默认是自动确认的，只要消息接收到，客户端会自动确认服务端会移除这个消息
     *     问题：
     *         当接收到很多消息时，自动回复给服务器ack但只有一个消息处理成功，宕机，发生消息丢失
     *         手动确认，处理一个，确认一个。没有ack,消息就一直的unacked状态，消息不会丢失，会重新变为Ready
     */
    @PostConstruct
    public void initRabbitTemplate(){
        //设置确认回调
        rabbitTemplate.setConfirmCallback(new RabbitTemplate.ConfirmCallback() {
            /**
             * 只要消息到达服务器 就会触发
             * @param correlationData 当前消息的唯一关联数据
             * @param b 消息是否成功收到
             * @param s 失败的原因
             */
            @Override
            public void confirm(CorrelationData correlationData, boolean b, String s) {
                System.out.println("confirm...correlationData[ "+correlationData+"]==>ack["+b+"]course"+s );
            }
        });
        //消息抵达队列的确认回调
        rabbitTemplate.setReturnsCallback(new RabbitTemplate.ReturnsCallback() {
            /**
             *
             * @param returnedMessage
             *
             */
            @Override
            public void returnedMessage(ReturnedMessage returnedMessage) {
                System.out.println("returnedMessage = " + returnedMessage.toString());
            }
        });
    }

}
