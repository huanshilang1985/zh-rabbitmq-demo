package com.zh.mq.producer;

import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.zh.mq.util.ConnectionUtil;

/**
 * 消息生产者3
 *
 * 使用Topic类型的交换机，发送消息会根据绑定队列和路由键，一起判断。
 * 路由键可以用.分段的，并且支持模糊匹配，*表示模糊匹配一段，#表示模糊匹配所有
 *
 * @author he.zhang
 * @date 2020/3/27 17:49
 */
public class Producer4topic {

    public static void main(String[] args) throws Exception {
        // 创建连接
        Connection connection = ConnectionUtil.getConnection();
        // 创建通道
        Channel channel = connection.createChannel();
        // 删除交换机，因为之前测试可能申请过这个名字的交换机，更改类型会报错
        channel.exchangeDelete("exchangeTest");
        // 创建交换机exchange
        // 参数1：交换机名称
        // 参数2：交换机类型
        channel.exchangeDeclare("exchangeTest", BuiltinExchangeType.TOPIC);
        // 交换机与队列绑定
        // 匹配debug开头，user结尾，中间随机的路由键
        channel.queueBind("queue1", "exchangeTest", "debug.*.user");
        // 匹配所有error开头的所有路由键
        channel.queueBind("queue2", "exchangeTest", "error.#");
        // 匹配中断是email，前后段随机的路由键
        channel.queueBind("queue3", "exchangeTest", "*.email.*");

        // 随机拼接消息，分发给各个队列
        String[] str1 = new String[]{"error","debug","info"};
        String[] str2 = new String[]{"user","order","email"};
        String[] str3 = new String[]{"a","b","c"};
        for(int i = 0; i < 3; i++){
            for(int j = 0; j< 3; j++){
                for(int k = 0; k< 3; k++){
                    String routingKey = str1[i]+"."+str2[j]+"."+str3[k];
                    String message = routingKey + " hello";
                    channel.basicPublish("exchangeTest", routingKey, null, message.getBytes());
                }
            }
        }

        // 关闭通道
        channel.close();
        // 关闭连接
        connection.close();
    }


}
