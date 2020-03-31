package com.zh.mq.producer;

import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.zh.mq.util.ConnectionUtil;

/**
 * 消息生产者2
 *
 * 使用Direct类型的交换机，发送消息会根据绑定队列和路由键，一起判断。
 * direct是默认类型，需要路由键和队列完全匹配才能收发信息。
 *
 * @author he.zhang
 * @date 2020/3/27 17:49
 */
public class Producer3direct {

    public static void main(String[] args) throws Exception {
        // 创建连接
        Connection connection = ConnectionUtil.getConnection();
        // 创建通道
        Channel channel = connection.createChannel();
        // 删除交换机，因为之前测试可能申请过这个名字的交换机，更改类型会报错
        channel.exchangeDelete("exchangeTest");
        // 创建交换机exchange
        // 参数1：交换机名称
        // 参数2：交换机类型
        channel.exchangeDeclare("exchangeTest", BuiltinExchangeType.DIRECT);
        // 交换机与队列绑定
        // 参数1：队列名称
        // 参数2：交换机名称
        // 参数3：绑定队列的routingKey路由键，direct，routingKey是必填的
        channel.queueBind("queue1", "exchangeTest", "info.user");
        channel.queueBind("queue2", "exchangeTest", "error.user");
//        channel.queueBind("queue2", "exchangeTest", "");

        // 参数1：交换机名称，为空时，使用默认交换机AMQP default
        // 参数2：routingKey路由键，下面填写的路由键是info.user，所以会发送到queue2队列中
        // 参数3：。。
        // 参数4：消息内容，Byte类型
        channel.basicPublish("exchangeTest", "info.user", null, "hello2".getBytes());
        // 关闭通道
        channel.close();
        // 关闭连接
        connection.close();
    }


}
