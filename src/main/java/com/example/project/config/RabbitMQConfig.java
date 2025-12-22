package com.example.project.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * RabbitMQ配置类
 * 配置消息队列、交换机和绑定
 */
@Configuration
public class RabbitMQConfig {

    /**
     * 示例队列名称
     */
    public static final String EXAMPLE_QUEUE = "example.queue";
    
    /**
     * 示例交换机名称
     */
    public static final String EXAMPLE_EXCHANGE = "example.exchange";
    
    /**
     * 示例路由键
     */
    public static final String EXAMPLE_ROUTING_KEY = "example.routing.key";

    /**
     * 配置消息转换器
     * 使用Jackson2JsonMessageConverter将消息转换为JSON格式
     *
     * @return Jackson2JsonMessageConverter
     */
    @Bean
    public Jackson2JsonMessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    /**
     * 配置RabbitTemplate
     * 用于发送消息
     *
     * @param connectionFactory ConnectionFactory
     * @return RabbitTemplate
     */
    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(messageConverter());
        return rabbitTemplate;
    }

    /**
     * 配置监听器容器工厂
     * 用于创建消息监听器容器
     *
     * @param connectionFactory ConnectionFactory
     * @return SimpleRabbitListenerContainerFactory
     */
    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(ConnectionFactory connectionFactory) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(messageConverter());
        return factory;
    }

    /**
     * 创建示例队列
     * 持久化、非独占、非自动删除
     *
     * @return Queue
     */
    @Bean
    public Queue exampleQueue() {
        return QueueBuilder.durable(EXAMPLE_QUEUE).build();
    }

    /**
     * 创建示例交换机
     * 持久化、非自动删除
     *
     * @return TopicExchange
     */
    @Bean
    public TopicExchange exampleExchange() {
        return ExchangeBuilder.topicExchange(EXAMPLE_EXCHANGE).durable(true).build();
    }

    /**
     * 绑定队列和交换机
     * 将示例队列绑定到示例交换机，使用示例路由键
     *
     * @return Binding
     */
    @Bean
    public Binding exampleBinding(Queue exampleQueue, TopicExchange exampleExchange) {
        return BindingBuilder.bind(exampleQueue).to(exampleExchange).with(EXAMPLE_ROUTING_KEY);
    }

}
