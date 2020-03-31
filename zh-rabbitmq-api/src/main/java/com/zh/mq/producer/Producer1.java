package com.zh.mq.producer;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.zh.mq.util.ConnectionUtil;

/**
 * 消息生产者1
 *
 * @author he.zhang
 * @date 2020/3/27 17:49
 */
public class Producer1 {

    public static void main(String[] args) throws Exception {
        Producer1.sendMessage("hello");
    }

    public static void sendMessage(String message) throws Exception {
        // 创建连接
        Connection connection = ConnectionUtil.getConnection();
        // 创建通道
        Channel channel = connection.createChannel();
        // 创建队列
        // 参数1: ConnectionUtil.QUEUE_NAME，队列名称
        // 参数2：true  队列是否需要持久化（队列持久化和消息持久化是两回事）
        // 参数3：false 是否（排他队列）单一队列，这里表示此队列是否只给这个连接使用
        // 参数4：false 是否自动删除，表示在没有人使用时是否自动删除
        // 参数5：null 是个Map类型，可以传一些队列参数
        channel.queueDeclare(ConnectionUtil.QUEUE_NAME,true,false,false,null);
        // 参数1：交换机名称，为空时，使用默认交换机AMQP default
        // 参数2：队列名称，在绑定队列之后，会把队列名称作为交换机的路由键使用
        // 参数3：。。
        // 参数4：消息内容，Byte类型
        channel.basicPublish("", ConnectionUtil.QUEUE_NAME, null, message.getBytes());
        // 关闭通道
        channel.close();
        // 关闭连接
        connection.close();
    }


}
