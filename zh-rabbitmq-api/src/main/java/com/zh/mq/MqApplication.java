package com.zh.mq;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 项目的启动类
 *
 * @author he.zhang
 * @date 2020/3/27 17:41
 */
@SpringBootApplication
public class MqApplication {

    public static void main(String[] args) {
        SpringApplication.run(MqApplication.class);
        System.out.println("========= MqApplication started =======");
    }

}
