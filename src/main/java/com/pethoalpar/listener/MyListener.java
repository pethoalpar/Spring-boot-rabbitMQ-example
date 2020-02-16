package com.pethoalpar.listener;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import com.pethoalpar.model.RabbitMessage;

import lombok.extern.log4j.Log4j2;

@Log4j2
@Service
public class MyListener {

	@RabbitListener(queues = "queue1")
	public void receive(RabbitMessage message) throws Exception {
		log.info("Message received!");
		log.info("queue1:" + message.getMessage());
	}

	@RabbitListener(queues = "queue2")
	public void receive2(RabbitMessage message) throws Exception {
		log.info("Message received!");
		log.info("queue2:" + message.getMessage());
	}

	@RabbitListener(queues = "queue3")
	public void receive3(RabbitMessage message) throws Exception {
		log.info("Message received!");
		log.info("queue3:" + message.getMessage());
	}
}
