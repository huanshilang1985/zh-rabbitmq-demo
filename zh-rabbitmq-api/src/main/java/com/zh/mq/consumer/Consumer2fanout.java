package com.zh.mq.consumer;

import com.rabbitmq.client.*;
import com.zh.mq.util.ConnectionUtil;

import java.io.IOException;

/**
 * 消息消费者2
 * 测试Producer2fanout，消费者队列绑定了交换机，会收到消息
 *
 * @author he.zhang
 * @date 2020/3/27 17:48
 */
public class Consumer2fanout {

    public static void main(String[] args) throws Exception {
        Connection connection = ConnectionUtil.getConnection();
        Channel channel = connection.createChannel();
        // 创建队列，队列在哪里创建都可以
        channel.queueDeclare("queue1", true, false, false, null);
        DefaultConsumer consumer = new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                System.out.println("Consumer2 == " + new String(body, "UTF-8"));
                channel.basicAck(envelope.getDeliveryTag(), false);
            }
        };
        // 测试自动确认
        channel.basicConsume("queue1", true, consumer);
    }
}
