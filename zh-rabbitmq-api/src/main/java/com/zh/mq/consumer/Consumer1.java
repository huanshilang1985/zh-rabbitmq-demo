package com.zh.mq.consumer;

import com.rabbitmq.client.*;
import com.zh.mq.util.ConnectionUtil;

import java.io.IOException;

/**
 * 消息消费者
 *
 * @author he.zhang
 * @date 2020/3/27 17:48
 */
public class Consumer1 {

    public static void main(String[] args) {
        try {
            getMessage();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void getMessage() throws Exception {
        Connection connection = ConnectionUtil.getConnection();
        Channel channel = connection.createChannel();
        // 第一次连接时还要确认是否创建通道，没有的话使用channel.queueDeclare()方法创建
        // 定义消费者，传入channel
        DefaultConsumer consumer = new DefaultConsumer(channel){
            /**
             * 服务的监听器
             * @param consumerTag 消费者标识
             * @param envelope    消息详细信息：包括交换机，路由键，消息标识
             * @param properties  消息配置
             * @param body   消息内容
             */
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                // 打印消息
                System.out.println(new String(body, "UTF-8"));
                // 手动确认消息
                // 参数1：消费的唯一标识，从envelope内获取
                // 参数2：声明是否批量确认
                channel.basicAck(envelope.getDeliveryTag(), false);
            }

        };
        // 开始消费。指定消费队列的名称，绑定消费者
        // 参数1-queue：消费通道名称
        // 参数2-autoAck：自动确认开关，默认是false，false状态需要手动确认消费消息
        // 参数3-callback：消费对象
        channel.basicConsume(ConnectionUtil.QUEUE_NAME, false, consumer);
        // 消费者是不需要关闭连接的，因为要一直监听
    }

}
