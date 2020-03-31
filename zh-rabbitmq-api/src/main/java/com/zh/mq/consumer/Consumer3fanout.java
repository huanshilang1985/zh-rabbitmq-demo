package com.zh.mq.consumer;

import com.rabbitmq.client.*;
import com.zh.mq.util.ConnectionUtil;

import java.io.IOException;

/**
 * 消息消费者3
 * 测试Producer2fanout，缴费者队列未绑定交换机，收不到消息
 *
 * @author he.zhang
 * @date 2020/3/27 17:48
 */
public class Consumer3fanout {

    public static void main(String[] args) throws Exception {
        Connection connection = ConnectionUtil.getConnection();
        Channel channel = connection.createChannel();
        // 创建队列
        channel.queueDeclare("queue2", true, false, false, null);
        DefaultConsumer consumer = new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                System.out.println("Consumer3fanout == " + new String(body, "UTF-8"));
                channel.basicAck(envelope.getDeliveryTag(), false);
            }
        };
        // 测试自动确认
        channel.basicConsume("queue2", true, consumer);
    }

}
