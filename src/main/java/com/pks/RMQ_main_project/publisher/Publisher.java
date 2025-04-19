package com.pks.RMQ_main_project.publisher;


import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.MessageDeliveryMode;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.pks.RMQ_main_project.config.RabbitConfig;
import com.pks.RMQ_main_project.dynamicQ.DynamicQueueService;

import entity.JsonMessage;

import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.core.DirectExchange;


@Service
public class Publisher {
    private static final Logger log = LoggerFactory.getLogger(Publisher.class);
    
    private final RabbitTemplate rabbitTemplate;
    private final AmqpAdmin amqpAdmin;
    private final String defaultExchange;
    private final MessageConverter messageConverter;
    
   
    @Autowired
    public Publisher(
            RabbitTemplate rabbitTemplate, 
            AmqpAdmin amqpAdmin,
            @Value("${rabbitmq.exchange.name:DemoExch}") String defaultExchange) {
        this.rabbitTemplate = rabbitTemplate;
        this.amqpAdmin = amqpAdmin;
        this.defaultExchange = defaultExchange;
        
        // Configure message converter (Jackson for JSON support)
        this.messageConverter = new Jackson2JsonMessageConverter();
        this.rabbitTemplate.setMessageConverter(this.messageConverter);
    }
    @Autowired
   private DynamicQueueService dynamicQueueService;
    
   
    public String sendStringMessage(String message, String queueName) {
    	log.info("queueName is : "+queueName);
    	log.info("message in controller is: {}", message);
        return sendMessage(message, queueName, defaultExchange, queueName);
    }
    
   
   
    public String sendObjectMessage(JsonMessage request) {
    	String message = request.getMessage();
    	String queueName = request.getRevId();
    	String exchangeName= request.getExchName();
        return sendMessage(message, queueName, defaultExchange, queueName);
    }
    
    public String sendMessage(Object message, String queueName, String exchangeName, String routingKey) {
        log.info("Sending message to queue: {} via exchange: {} with routing key: {}", 
                queueName, exchangeName, routingKey);
        
        try {
            // Ensure infrastructure exists
//           x
        	if(!dynamicQueueService.checkReservation(queueName))
            {
            	log.info("reservation id doesn't exist");
            	return "reservation id doesn't exist";
            }
        	 ensureInfrastructure(queueName, exchangeName, routingKey);
            
            // Send message using RabbitTemplate
            rabbitTemplate.convertAndSend(exchangeName, routingKey, message, msg -> {
                // Set message properties
                MessageProperties props = msg.getMessageProperties();
                props.setContentType(MessageProperties.CONTENT_TYPE_TEXT_PLAIN);
                props.setDeliveryMode(MessageDeliveryMode.PERSISTENT);
                props.setTimestamp(new Date());
                return msg;
            });
            
            log.info("Message published successfully to queue '{}' via exchange '{}'", queueName, exchangeName);
            return String.format("Message sent successfully to queue '%s'", queueName);
            
        } catch (AmqpException e) {
            log.error("Failed to send message to queue '{}': {}", queueName, e.getMessage(), e);
            return String.format("Failed to send message to queue '%s': %s", queueName, e.getMessage());
        }
    }
    
    /**
     * Ensures that the required RabbitMQ infrastructure (exchange, queue, binding) exists
     */
    private void ensureInfrastructure(String queueName, String exchangeName, String routingKey) {
        try {
        	 Queue queue = new Queue(queueName, true, false, false);
            // Declare queue (durable, not exclusive, not auto-delete)
            
            // Declare exchange (durable)
            Exchange exchange = new TopicExchange(exchangeName, true, false);
            amqpAdmin.declareExchange(exchange);
            
            // Bind queue to exchange with routing key
            Binding binding = BindingBuilder.bind(queue)
                    .to(new TopicExchange(exchangeName))
                    .with(routingKey);
            amqpAdmin.declareBinding(binding);
            
        } catch (AmqpException e) {
            log.error("Failed to set up messaging infrastructure: {}", e.getMessage(), e);
            throw e;
        }
    }

	
}