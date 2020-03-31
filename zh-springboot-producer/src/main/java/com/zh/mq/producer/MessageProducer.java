package com.zh.mq.producer;

import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author he.zhang
 * @date 2020/3/29 23:24
 */
@Component
public class MessageProducer {

    @Autowired
    RabbitTemplate rabbitTemplate;

    public void testSend() {
        // 业务标识，发送失败时才有用。
        CorrelationData data = new CorrelationData("订单ID");

        // 参数1：交换机名字
        // 参数2：路由键
        // 参数3：消息内容
        rabbitTemplate.convertAndSend("directExchange", "direct.key", "hello", data);
    }

}
