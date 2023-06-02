package com.atguigu.gulimall.order.service.impl;

import com.atguigu.gulimall.order.entity.OrderEntity;
import com.atguigu.gulimall.order.entity.OrderReturnReasonEntity;
import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.common.utils.Query;

import com.atguigu.gulimall.order.dao.OrderItemDao;
import com.atguigu.gulimall.order.entity.OrderItemEntity;
import com.atguigu.gulimall.order.service.OrderItemService;


@Service("orderItemService")
@RabbitListener(queues = "helloQueue")
public class OrderItemServiceImpl extends ServiceImpl<OrderItemDao, OrderItemEntity> implements OrderItemService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<OrderItemEntity> page = this.page(
                new Query<OrderItemEntity>().getPage(params),
                new QueryWrapper<OrderItemEntity>()
        );

        return new PageUtils(page);
    }
    /**
     * 接收消息
     * queues[]声明监听的所有队列
     * 参数可以写以下类型
     * 1、Message 原生消息类型，头＋体
     * 2、T<发送的消息类型>
     */
    @RabbitHandler
    public void recieveMsg(Message msg, OrderReturnReasonEntity content, Channel channel) {
        System.out.println("接收到消息"+msg+"==>类型"+msg.getClass());
        byte[] body = msg.getBody();
        //消息头属性信息
        MessageProperties properties = msg.getMessageProperties();
        System.out.println("消息处理完成" + content.getName());
        //channel内按顺序自增
        long deliveryTag = msg.getMessageProperties().getDeliveryTag();
        System.out.println("deliveryTag = " + deliveryTag);
        //签收货物，非批量模式
        try {
            if (deliveryTag%2==0){
                channel.basicAck(deliveryTag,false);
                System.out.println("签收了货物"+deliveryTag);
            }else{
                //退货
                //b2 是否重新入队
                channel.basicNack(deliveryTag,false,false);
                System.out.println("没有签收了货物"+deliveryTag);
            }
        }catch (Exception e) {
            //网络中断
        }
    }
    @RabbitHandler
    public void recieveMsg2(OrderEntity content,Channel channel) {
        System.out.println("接收到消息"+content);
    }
}