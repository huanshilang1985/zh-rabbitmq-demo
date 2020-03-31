package com.zh.mq;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * SpringBoot + RabbitMQ 消息消费者项目
 * 启动类
 *
 * @author he.zhang
 * @date 2020/3/29 22:35
 */
@SpringBootApplication
public class ConsumerApplication {

    public static void main(String[] args) {
        SpringApplication.run(ConsumerApplication.class);
        System.out.println("======= ConsumerApplication Started ======");
    }

}
