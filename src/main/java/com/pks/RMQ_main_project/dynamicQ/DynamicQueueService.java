package com.pks.RMQ_main_project.dynamicQ;

import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pks.RMQ_main_project.consumerImpl.DynamicConsumer;

import jakarta.annotation.PreDestroy;

@Service
public class DynamicQueueService {
    private static final Logger log = LoggerFactory.getLogger(DynamicQueueService.class);
    private final ConnectionFactory connectionFactory;
    private final Map<String, String> reservationMap;
    private final Map<String, SimpleMessageListenerContainer> listenerContainers;
    
    @Autowired
    private RabbitAdmin rabbitAdmin;

    public DynamicQueueService(ConnectionFactory connectionFactory) {
    	this.connectionFactory = connectionFactory;
        this.reservationMap = new ConcurrentHashMap<>();
        this.listenerContainers = new ConcurrentHashMap<>();
    }
    
    public boolean checkReservation(String reservationId) {
    	if (reservationId == null) {
            throw new IllegalArgumentException("Reservation ID cannot be null");
        }
    	else {
    		 return reservationMap.containsKey(reservationId);
    	}
    	
    }
    

    public synchronized String addReservation(String reservationId, String tableNo) {
        if (reservationId == null || tableNo == null) {
            throw new IllegalArgumentException("Reservation ID and table number cannot be null");
        }

        if (reservationMap.containsKey(reservationId)) {
            log.warn("Reservation ID {} already exists", reservationId);
            return String.format("Reservation ID: %s already exists", reservationId);
        }

        try {
            // Declare a durable queue
            Queue dynamicQueue = new Queue(reservationId, true, false, false);
            rabbitAdmin.declareQueue(dynamicQueue);
            
            // Add to our tracking map
            reservationMap.put(reservationId, tableNo);
            
            // Set up consumer
//            addConsumerToQueue(reservationId);
            
            log.info("Successfully created reservation queue: {} for table: {}", reservationId, tableNo);
            return reservationId;
            
        } catch (Exception e) {
            log.error("Failed to create reservation queue: {}", reservationId, e);
            throw new QueueOperationException("Failed to create reservation", e);
        }
    }

    public synchronized String deleteReservation(String reservationId) {
        if (reservationId == null) {
            throw new IllegalArgumentException("Reservation ID cannot be null");
        }

        if (!reservationMap.containsKey(reservationId)) {
            log.warn("Reservation ID {} does not exist", reservationId);
            return String.format("Reservation ID: %s does not exist", reservationId);
        }

        try {
            // Stop and remove the consumer first
            SimpleMessageListenerContainer container = listenerContainers.remove(reservationId);
            if (container != null) {
                container.stop();
            }
            
            // Delete the queue
            rabbitAdmin.deleteQueue(reservationId);
            reservationMap.remove(reservationId);
            
            log.info("Successfully deleted reservation queue: {}", reservationId);
            return "Queue deleted: " + reservationId;
            
        } catch (Exception e) {
            log.error("Failed to delete reservation queue: {}", reservationId, e);
            throw new QueueOperationException("Failed to delete reservation", e);
        }
    }
    //commenting adding consumer
//    private void addConsumerToQueue(String queueName) {
//        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
//        container.setConnectionFactory(connectionFactory);
//        container.setQueueNames(queueName);
//        container.setPrefetchCount(1);
//        container.setConcurrentConsumers(1);
//
//        DynamicConsumer consumer = new DynamicConsumer(queueName, "AutoConsumer");
//        MessageListenerAdapter adapter = new MessageListenerAdapter(consumer, "receive");
//        container.setMessageListener(adapter);
//        container.setAutoStartup(true);
//        container.start();
//
//        listenerContainers.put(queueName, container);
//        log.info("Consumer attached to queue: {}", queueName);
//    }

    public Map<String, String> getActiveReservations() {
        return new ConcurrentHashMap<>(reservationMap);
    }
    
    @PreDestroy
    public void shutdown() {
         log.info("all containers gracefully before application context closes"); 
        listenerContainers.values().forEach(container -> {
            try {
                container.stop();
            } catch (Exception e) {
                log.warn("Error stopping container", e);
            }
        });
        listenerContainers.clear();
    }

    public void destroy() {
        listenerContainers.values().forEach(SimpleMessageListenerContainer::stop);
        listenerContainers.clear();
        log.info("Cleaned up all message listener containers");
    }
}

class QueueOperationException extends RuntimeException {
    public QueueOperationException(String message, Throwable cause) {
        super(message, cause);
    }
}