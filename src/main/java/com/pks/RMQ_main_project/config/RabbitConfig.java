package com.pks.RMQ_main_project.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.listener.MessageListenerContainer;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import com.pks.RMQ_main_project.consumer.ManagerQueueConsumer;
import com.pks.RMQ_main_project.consumerImpl.DynamicConsumer;


import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import java.util.Arrays;
import java.util.List;



@Configuration
@EnableRabbit
public class RabbitConfig {

    private static final Logger log = LoggerFactory.getLogger(RabbitConfig.class);

    private static final String MANAGER_QUEUE = "managerqueue";
    @Value("${rabbitmq.exchange.name:DemoExch}")
    private String exchangeName;
   
    
    
//    @Bean
//    public List<MessageListenerContainer> messageListenerContainers() {
//        // Get queue name and consumer names from properties
//        String queueName = environment.getProperty("rabbitmq.queue.name");
//        String[] consumerNames = environment.getProperty("rabbitmq.consumer.names", String.class).split(",");
//
//        // Create a container for each consumer (for the same queue)
//        return Arrays.stream(consumerNames)
//                .map(consumerName -> createMessageListenerContainer(queueName, consumerName))
//                .toList();
//    }
   
    @Bean
    public ConnectionFactory connectionFactory() {
        CachingConnectionFactory factory = new CachingConnectionFactory("localhost");
        factory.setUsername("guest");
        factory.setPassword("guest");
        factory.setCacheMode(CachingConnectionFactory.CacheMode.CHANNEL);
        factory.setVirtualHost("/");
        return factory;
    }
    
    
    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setExchange(exchangeName);
        // Using Jackson JSON converter as in your Publisher class
        template.setMessageConverter(new Jackson2JsonMessageConverter());
        return template;
    }
    
    
//	@Bean
//	public MessageListenerContainer messageListenerContainer(ConnectionFactory connectionFactory) {
//		log.info("Creating MessageListenerContainer for consumer 'MyConsumer'");
//		return createMessageListenerContainer("MyConsumer", connectionFactory);
//	}
    	
    	
    	 @Bean
    	    public Queue managerQueue() {
    		 Queue queue = new Queue(MANAGER_QUEUE, true, false, false);
    		 return queue;
    	    }
    	 
    	 @Bean
    	    public TopicExchange topicExchange() {
    	        return new TopicExchange(exchangeName, true, false);
    	    }
    	    
    	   @Bean
    	   public Binding managerBinding(Queue managerQueue, TopicExchange topicExchange) {
    	       // Bind manager queue with # wildcard to receive all messages
    	       return BindingBuilder.bind(managerQueue).to(topicExchange).with("#");
    	   }

 /*   public MessageListenerContainer createMessageListenerContainer( String consumerName,ConnectionFactory connectionFactory) {
        // Create a SimpleMessageListenerContainer
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
//        container.setQueueNames(queueName); // All containers listen to the same queue
        // Set consumer tag (name) for each consumer
        container.setConsumerTagStrategy(queue -> consumerName);

        // Attach a different listener for each consumer
        MessageListenerAdapter listenerAdapter = new MessageListenerAdapter(new DynamicConsumer(queueName, consumerName), "receive");
        container.setMessageListener(listenerAdapter);

        // Start the container dynamically
        log.info("Consumer '{}' created for queue '{}'", consumerName, queueName);

        return container;
    }*/
    
    @Bean
    public MessageListenerContainer managerQueueListenerContainer(ConnectionFactory connectionFactory) {
        log.info("Creating MessageListenerContainer for manager queue");
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.setQueueNames(MANAGER_QUEUE);
        container.setPrefetchCount(10);
        container.setConcurrentConsumers(1);
        
        MessageListenerAdapter listenerAdapter = new MessageListenerAdapter(new ManagerQueueConsumer(), "receive");
        //listenerAdapter.setMessageConverter(new Jackson2JsonMessageConverter());
        container.setMessageListener(listenerAdapter);
        container.setAutoStartup(true);
        
        return container;
    }
    
    @Bean
    public RabbitAdmin rabbitAdmin(ConnectionFactory connectionFactory) {
        return new RabbitAdmin(connectionFactory);
    }




}
