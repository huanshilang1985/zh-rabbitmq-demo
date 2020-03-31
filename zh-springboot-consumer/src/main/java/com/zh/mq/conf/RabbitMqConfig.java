package com.zh.mq.conf;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

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
        return factory;
    }

    /**
     * 创建RabbitTemplate，用于访问RabbitMQ
     * @return RabbitTemplate
     */
    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory){
        RabbitTemplate template = new RabbitTemplate();
        template.setConnectionFactory(connectionFactory);
        return template;
    }

    /**
     * 默认交换机
     * @return DirectExchange
     */
    @Bean
    public DirectExchange defaultExchange() {
        return new DirectExchange("directExchange");
    }

    /**
     * 默认通道
     * @return
     */
    @Bean
    public Queue queue() {
        // 通道名称
        // 是否持久化
        return new Queue("testQueue", true);
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

//    /**
//     * 消息监听容器，这个方法与@RabbitListener注解类似。但是@RabbitListener的队列是写死的，不能动态写
//     * @return SimpleMessageListenerContainer
//     */
//    @Bean
//    public SimpleMessageListenerContainer simpleMessageListenerContainer(){
//        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
//        container.setConnectionFactory(connectionFactory());
//        // 消息监听器
//        container.setMessageListener(new MessageListener() {
//            @Override
//            public void onMessage(Message message) {
//
//            }
//        });
//        // 消费的队列，参数是String名称，可以是多个队列
//        container.setQueueNames();
//        // 消费的队列，参数是Queue对象，需要提前创建
//        // container.addQueues();
//        // 消息队列确认方式
//        container.setAcknowledgeMode(AcknowledgeMode.MANUAL);
//        return container;
//    }

    /**
     * 监听消费者行为
     * @return SimpleRabbitListenerContainerFactory
     */
    @Bean
    public SimpleRabbitListenerContainerFactory simpleRabbitListenerContainerFactory(){
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory());
        // 确认方式: NONE-不确认，MANUAL-手动确认, AUTO-自动确认
        factory.setAcknowledgeMode(AcknowledgeMode.MANUAL);
        // 消息预期数量
        factory.setPrefetchCount(1);
        return factory;
    }

}
