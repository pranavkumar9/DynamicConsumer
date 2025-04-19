package com.pks.RMQ_main_project.config;

import org.springframework.amqp.rabbit.listener.MessageListenerContainer;

public interface Consumer {
	
	public void recieve(String msg);
	MessageListenerContainer createMessageListenerContainer( String consumerName);

}
