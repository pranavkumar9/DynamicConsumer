package com.pks.RMQ_main_project.consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ManagerQueueConsumer {
	
	private static final Logger log = LoggerFactory.getLogger(ManagerQueueConsumer.class);
	
	public void receive(String msg) {
	    log.info("String Message received by managerQueue: {}", msg);
	}
//	 public void receive(Object message) {
//	        log.info("MANAGER QUEUE received message: {}", message.toString());
//	        // Process message as needed
//	    }
	
	public void receive(Object message) {
	    if (message instanceof byte[]) {
	        String jsonMessage = new String((byte[]) message);
	        log.info("MANAGER QUEUE received JSON message: {}", jsonMessage);
	        
	        // Optional: Parse JSON if needed
	        try {
	            ObjectMapper objectMapper = new ObjectMapper();
	            JsonNode jsonNode = objectMapper.readTree(jsonMessage);
	            log.info("Parsed JSON: {}", jsonNode);
	        } catch (Exception e) {
	            log.error("Failed to parse JSON message", e);
	        }
	    } else {
	        log.warn("Unexpected message type: {}", message.getClass().getName());
	    }
	}

}
