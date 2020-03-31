package com.zh.mq.util;

import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

/**
 * RabbitMQ连接工具类
 *
 * @author he.zhang
 * @date 2020/3/27 17:44
 */
public class ConnectionUtil {

    /**
     * MQ默认队列
     */
    public static final String QUEUE_NAME = "testQueue";

    /**
     * MQ交换器
     */
    public static final String EXCHANGE_NAME = "exchange";

    public static Connection getConnection() throws Exception {
        // 创建一个连接工厂
        ConnectionFactory factory = new ConnectionFactory();
        // 设置rabbitmq服务器地址
        factory.setHost("192.168.220.11");
        // 端口
        factory.setPort(5672);
        factory.setUsername("***");
        factory.setPassword("***");
        factory.setVirtualHost("testhost");
        return factory.newConnection();
    }

}
