package com.zh.mq.consumer;

import com.rabbitmq.client.*;
import com.zh.mq.util.ConnectionUtil;

import java.io.IOException;

/**
 * 消息消费者4
 * 测试Producer3direct，路由器类型是direct，需要消息生产者根据路由键发送到指定队列中，消费端不需要特别控制，只需要监听队列即可。
 *
 * @author he.zhang
 * @date 2020/3/27 17:48
 */
public class Consumer4direct {

    public static void main(String[] args) throws Exception {
        Connection connection = ConnectionUtil.getConnection();
        Channel channel = connection.createChannel();
        // 创建队列
        channel.queueDeclare("queue1", true, false, false, null);
        DefaultConsumer consumer = new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                System.out.println("Consumer4 == " + new String(body, "UTF-8"));
                channel.basicAck(envelope.getDeliveryTag(), false);
            }
        };
        // 测试自动确认
        channel.basicConsume("queue1", true, consumer);
    }

}
