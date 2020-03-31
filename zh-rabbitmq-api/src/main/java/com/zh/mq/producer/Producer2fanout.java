package com.zh.mq.producer;

import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.zh.mq.util.ConnectionUtil;

/**
 * 消息生产者2
 *
 * 使用fanout类型的交换机，只会发送到绑定的队列中。
 *
 * @author he.zhang
 * @date 2020/3/27 17:49
 */
public class Producer2fanout {

    public static void main(String[] args) throws Exception {
        Producer2fanout.sendMessage("hello");
    }

    public static void sendMessage(String message) throws Exception {
        // 创建连接
        Connection connection = ConnectionUtil.getConnection();
        // 创建通道
        Channel channel = connection.createChannel();
        // 删除交换机，因为之前测试可能申请过这个名字的交换机，更改类型会报错
        channel.exchangeDelete("exchangeTest");
        // 创建交换机exchange
        // 参数1：交换机名称
        // 参数2：交换机类型
        channel.exchangeDeclare("exchangeTest", BuiltinExchangeType.FANOUT);
        // 交换机与队列绑定
        // 参数1：队列名称
        // 参数2：交换机名称
        // 参数3：routingKey路由键，fanout类型交换机是不需要路由键的
        channel.queueBind("queue1", "exchangeTest", "");
        // 发送消息
        // 参数1：交换机名称，为空时，使用默认交换机AMQP default
        // 参数2：routingKey路由键，fanout类型交换机是不需要路由键的
        // 参数3：。。
        // 参数4：消息内容，Byte类型
        channel.basicPublish("exchangeTest", "", null, message.getBytes());
        // 关闭通道
        channel.close();
        // 关闭连接
        connection.close();
    }


}
