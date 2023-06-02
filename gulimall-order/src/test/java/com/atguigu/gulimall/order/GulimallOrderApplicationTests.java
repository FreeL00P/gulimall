package com.atguigu.gulimall.order;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@Slf4j
class GulimallOrderApplicationTests {

    @Autowired
    AmqpAdmin amqpAdmin;

    @Autowired
    RabbitTemplate rabbitTemplate;

    @Test
    void contextLoads() {
    }
    /**
     * 1 如何创建Exchange、Queue、Binding
     *      1)使用Amqb进行创建
     * 2 如何收发消息
     */
    @Test
    public void createExchange() throws Exception {
        DirectExchange directExchange = new DirectExchange("helloExchange",true,false);
        amqpAdmin.declareExchange(directExchange);
        log.info("create exchange success");
    }
    @Test
    public void createQueue() throws Exception {
        Queue queue = new Queue("helloQueue",true,false,false);
        amqpAdmin.declareQueue(queue);
        log.info("create queue success");
    }

    @Test
    public void createBinding() {
        /**
         * String destination, 目的地
         * DestinationType destinationType,目的地类型
         * String exchange, 交换机
         * String routingKey,路由键
         * @Nullable Map<String, Object> arguments) {
         *
         */
        Binding binding = new Binding("helloQueue",
                Binding.DestinationType.QUEUE,
                "helloExchange",
                "hello",null);
            amqpAdmin.declareBinding(binding);
        log.info("create binding success");
    }

    @Test
    public void sendMsg() throws Exception {
        //发送消息
        rabbitTemplate.convertAndSend("helloExchange",
                "hello",
                new User("hello"));
        log.info("send message success");
    }
}
