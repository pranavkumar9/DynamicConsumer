package com.pks.RMQ_main_project.consumerImpl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.AmqpRejectAndDontRequeueException;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;

public class DynamicConsumer {
    private static final Logger log = LoggerFactory.getLogger(DynamicConsumer.class);
    
    private final String queueName;
    private final String consumerName;
    private int processedMessageCount = 0;

    public DynamicConsumer(String queueName, String consumerName) {
        this.queueName = queueName;
        this.consumerName = consumerName;
        log.info("Created new consumer '{}' for queue '{}'", consumerName, queueName);
    }
    
    //commenting recieve msg
//    public void receive(String messageContent) {
//        log.info("Received string Message'{}' for queues '{}'", messageContent,queueName);
//
//        // Process message
//        processMessage(messageContent);
//    }

    //commenting recieve msg

//    public void receive(Message message) {
//        MessageProperties properties = message.getMessageProperties();
//        String messageId = properties.getMessageId();
//        log.info("Received Object Message for queues: {}", message);
//        try {
//            log.debug("Consumer '{}' processing message {} from queue '{}'", 
//                     consumerName, messageId, queueName);
//
//            // Convert message body to string
//            String messageContent = new String(message.getBody());
//            
//            // Process the message
//            processMessage(messageContent, properties);
//            
//            // Update metrics
//            processedMessageCount++;
//            
//            log.info("Successfully processed message {} from queue '{}'. Total processed: {}", 
//                    messageId, queueName, processedMessageCount);
//            
//        } catch (Exception e) {
//            log.error("Error processing message {} from queue '{}': {}", 
//                     messageId, queueName, e.getMessage(), e);
//                     
//            // Determine if message should be retried or discarded
//            throw new AmqpRejectAndDontRequeueException("Non-retryable error occurred", e);
//          
//        }
//    }

    private void processMessage(String messageContent, MessageProperties properties) {
        // Here you can add your specific message processing logic
        log.info("Consumer '{}' received message from '{}': {}", 
                consumerName, queueName, messageContent);
        
        // Add your custom processing logic here
        // For example, you might want to:
        // - Parse JSON/XML content
        // - Validate message format
        // - Process business logic
        // - Store data in database
        // - Call external services
    }
    
    private void processMessage(String messageContent) {
        log.info("Processing message: {}", messageContent);
        log.info("message recieved to consumer");
        // Add your business logic here
    }

   

    private boolean isRetryableError(Exception e) {
        // Define which exceptions should trigger a retry
        // For example, temporary network issues might be retryable
        // while data validation errors might not be
        return e instanceof TemporaryProcessingException;
    }

    // Custom exception for temporary processing failures
    private static class TemporaryProcessingException extends RuntimeException {
        public TemporaryProcessingException(String message) {
            super(message);
        }
    }

    // Getters for monitoring
    public int getProcessedMessageCount() {
        return processedMessageCount;
    }

    public String getQueueName() {
        return queueName;
    }

    public String getConsumerName() {
        return consumerName;
    }
}