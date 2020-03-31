package com.zh.mq.consumer;

import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;


/**
 * 消息监听器
 *
 * 预取数量：（假设有两个消费者，100条消息，队列会按照轮训的规则，把所有消息轮训发给2个消费者，如果一个消费快，一个消费慢，就造成了消费快的服务一直很闲，消费慢的进程压力过大。）
 *            现象就是队列已经清零了，消费慢的线程还在慢慢的消费剩余的消息
 *
 * @author he.zhang
 * @date 2020/3/29 23:26
 */
@Component
public class MessageListener {

    /**
     * 消费者，监听testQueue队列
     * @param message 消息内容，此处接收的是Message对象，需要把body转码
     *                也可以使用MessageConverter 来自动统一处理
     * @throws Exception
     */
    @RabbitListener(queues = "testQueue", containerFactory = "simpleRabbitListenerContainerFactory")
    public void get(Message message, Channel channel) throws Exception {
        System.out.println("消费者1：" + new String(message.getBody(), "UTF-8"));
        // 消费预取数量，一次性拉取多少条数据。最多同时接收多少条数据。必须手动确认，不能自动确认。
        // 可以和批量确认一起使用
        channel.basicQos(10);

        // 假设消息是下订单的内容，下单成功的消息，手动确认
        if (placeOrder()){
            // 手动确认配置 参数1：消息唯一标识  参数2：是否批量确认
            // 建议开通批量确认，唯一标识是MQ自动生成的，传最后一次确认的ID，会把之前的全都确认
            // 开启批量确认需要提供个判断条件，并且要做好幂等性处理。（因为在未确认之前，连接中断的话，会造成重复消费的问题）
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);



        }
        // 下单失败的消息，采用消息退回策略
        else {
            // 批量消息退回
            // 参数1：消息标识
            // 参数2：是否批量退回，如果想要单条退回就用false
            // 参数3：是否回到消息队列，如果要废弃消息使用false，如果退回的可能会有重复消费可能，需要同时开通其他消费者处理。
            channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, true);
            // 单条消息退回，最早按照AMQP定义的接口，现在不经常用了
//            channel.basicReject();
        }

    }

    public boolean placeOrder(){
        return true;
    }

    /**
     * 消费者，监听testQueue队列，如果两个监听器同时监听一个通道，两个线程轮训消费消息
     * @param message 消息内容，String类型，直接输出
     * @throws Exception
     */
    @RabbitListener(queues = "testQueue")
    public void get2(String message) throws Exception {
        System.out.println("消费者2：" + message);
    }

}
