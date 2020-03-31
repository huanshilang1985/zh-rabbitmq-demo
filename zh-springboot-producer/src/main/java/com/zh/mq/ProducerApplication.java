package com.zh.mq;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * SpringBoot + RabbitMQ 消息生产者项目
 * 启动类
 *
 * @author he.zhang
 * @date 2020/3/29 22:33
 */
@SpringBootApplication
public class ProducerApplication {

    public static void main(String[] args) {
        SpringApplication.run(ProducerApplication.class);
        System.out.println("======= ProducerApplication Started ======");
    }

}
