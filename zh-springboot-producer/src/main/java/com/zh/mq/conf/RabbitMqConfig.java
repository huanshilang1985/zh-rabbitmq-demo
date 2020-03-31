package com.zh.mq.conf;

import com.alibaba.fastjson.JSON;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.MessageConversionException;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

/**
 * @author he.zhang
 * @date 2020/3/29 23:05
 */
@Configuration
public class RabbitMqConfig {

    /**
     * 创建RabbitMQ的连接
     * @return ConnectionFactory
     */
    @Bean
    public ConnectionFactory connectionFactory(){
        CachingConnectionFactory  factory = new CachingConnectionFactory();
        factory.setHost("192.168.220.11");
        factory.setPort(5672);
        factory.setUsername("zhanghe");
        factory.setPassword("zhanghe");
        // 虚拟地址，类型与MySQL的库，是相互独立的，可以在管理后台简历
//        factory.setVirtualHost("testmq");
        //是否开启消息确认机制：消息确认、失败回调
        factory.setPublisherConfirms(true);
        return factory;
    }

    /**
     * 创建RabbitTemplate，用于访问RabbitMQ
     * 如果有多个回调业务，要把此方法设置为非单例模式，在使用@PostConstruct在调用时注入ConfirmCallback方法
     * @return RabbitTemplate
     */
    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory){
        RabbitTemplate template = new RabbitTemplate();
        template.setConnectionFactory(connectionFactory);
        // 1. 消息发送确认，发送消息失败的回调方法。这个类可以单独写，如果业务比较单一可以使用匿名内部类
        // 这块处理的是消息到交换机之间的逻辑
        template.setConfirmCallback(new RabbitTemplate.ConfirmCallback() {
            /**
             * 消息确认机制
             * @param correlationData SpringBoot提供的业务标识对象（建议在发送消息时，提供这个对象，方便失败时处理）
             * @param ack   有没有成功发送到MQ
             * @param cause 失败原因
             */
            @Override
            public void confirm(CorrelationData correlationData, boolean ack, String cause) {
                // 按照业务处理具体业务逻辑，比如错误消息入库等。
                System.out.println(correlationData);
                System.out.println(ack);
                System.out.println(cause);

            }
        });
        // 2. 开始失败回调
        // 处理的是交换机到队列之间的逻辑
        template.setMandatory(true);
        template.setReturnCallback(new RabbitTemplate.ReturnCallback() {
            /**
             * 返回消息：失败回调出现了的都是比较严重的问题，所以返回信息比较多
             *   失败原因可能是路由键不存在，通道未绑定等等
             * @param message  发送消息 "hello" + 发送消息的配置
             * @param replyCode 状态码：成功是200
             * @param replyText 失败的信息
             * @param exchange  交换机
             * @param routingKey 路由键
             */
            @Override
            public void returnedMessage(Message message, int replyCode, String replyText, String exchange, String routingKey) {
                System.out.println(message);
                System.out.println(replyCode);
                System.out.println(replyText);
                System.out.println(exchange);
                System.out.println(routingKey);
            }
        });

        // 消息转换器，可以统一处理消息格式
        template.setMessageConverter(new MessageConverter() {
            // 发送消息，要重写的方法
            @Override
            public Message toMessage(Object o, MessageProperties messageProperties) throws MessageConversionException {
                // 默认格式SpringBoot会转换一次，所以直接接受String输出的是字节码。设置text格式后，转换时会默认用UTF-8转码数据
                messageProperties.setContentType("text/xml");
                messageProperties.setContentEncoding("UTF-8");
                byte[] bytes;
                if (o instanceof String){
                    bytes = ((String) o).getBytes();
                } else {
                    bytes = JSON.toJSONBytes(o);
                }
                Message message = new Message(bytes, messageProperties);
                return message;
            }
            // 接收消息，要重写的方法
            @Override
            public Object fromMessage(Message message) throws MessageConversionException {
                return null;
            }
        });

        return template;
    }

    /**
     * 默认交换机
     * @return DirectExchange
     */
    @Bean
    public DirectExchange defaultExchange() {
        Map<String, Object> map = new HashMap<>();
        // 声明备用交换机，只能在声明交换机时，才能声明备用交换机
        // 如果被交换机没有被路由到，就会启用备用交换机
        // 备用交换机需要提前创建
        map.put("alternate-exchange", "exchangeTest");
        // 参数1：交换机名称
        // 参数2：是否持久化
        // 参数3：是否自删除
        // 参数4: 配置参数
        return new DirectExchange("directExchange", false, false, map);
    }

    /**
     * 默认通道
     * @return
     */
    @Bean
    public Queue queue() {
        Map<String,Object> map = new HashMap<>();
        // 定义死信交换机：交换机名称
        map.put("x-dead-letter-exchange", "deadExchange");
        // 死信交换机的路由键：重定向的路由键
        map.put("x-dead-letter-routing-key", "test.key");

        // 参数：队列名称，是否持久化，是否排他队列，是否自动删除，设置参数
        return new Queue("testQueue", true, false, false, map);
    }

    /**
     * 绑定关系
     * @return
     */
    @Bean
    public Binding binding() {
        //绑定一个队列  to: 绑定到哪个交换机上面 with：绑定的路由建（routingKey）
        return BindingBuilder.bind(queue()).to(defaultExchange()).with("direct.key");
    }

    /**
     * 定义交换机，用于死信交换机
     * @return DirectExchange
     */
    @Bean
    public DirectExchange deadExchange() {
        DirectExchange directExchange = new DirectExchange("deadExchange");
        return directExchange;
    }

    /**
     * 死信交换机的绑定队列
     * @return Binding
     */
    @Bean
    public Binding deadBinding() {
        //绑定一个队列  to: 绑定到哪个交换机上面 with：绑定的路由建（routingKey）
        // 这个应该是另新建一个队列，专门用于处理死信
        return BindingBuilder.bind(queue()).to(defaultExchange()).with("test.key");
    }



}
