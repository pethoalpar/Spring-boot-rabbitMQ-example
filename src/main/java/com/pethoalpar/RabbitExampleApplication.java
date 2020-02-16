package com.pethoalpar;

import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.core.MessageDeliveryMode;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pethoalpar.model.RabbitMessage;

import lombok.extern.log4j.Log4j2;

@SpringBootApplication
@EnableRabbit
@EnableAutoConfiguration
@ComponentScan
@EnableScheduling
@Log4j2
public class RabbitExampleApplication {

	public static void main(String[] args) {
		SpringApplication.run(RabbitExampleApplication.class, args);
	}

	@Autowired
	private FanoutExchange exchange1;

	@Autowired
	@Qualifier("queue3")
	private Queue queue;

	@Autowired
	private RabbitTemplate template;

	@Scheduled(fixedRate = 3000)
	protected void schedule() {
		log.info("Sending message on rabbit");
		sendMessageQueue(queue.getName());
		sendMessageExchange(exchange1.getName());
	}

	private void sendMessageQueue(String queueName) {
		RabbitMessage message = new RabbitMessage();
		message.setMessage("Message on " + queueName);
		message.setTitle("Title");

		ObjectMapper mapper = new ObjectMapper();
		try {
			byte[] messageBytes = mapper.writeValueAsBytes(message);
			Message amqpMessage = MessageBuilder.withBody(messageBytes)
					.setContentType(MessageProperties.CONTENT_TYPE_JSON).build();
			this.template.convertAndSend(queueName, amqpMessage, m -> {
				m.getMessageProperties().setPriority(3);
				m.getMessageProperties().setDeliveryMode(MessageDeliveryMode.PERSISTENT);
				m.getMessageProperties().setContentType(MessageProperties.CONTENT_TYPE_JSON);
				m.getMessageProperties().setExpiration(String.valueOf(24 * 60 * 60 * 1000));
				return m;
			});
			this.template.setMandatory(true);
		} catch (JsonProcessingException e) {
			log.error("Json parse error on send message queue");
		}
	}

	private void sendMessageExchange(String queueName) {
		RabbitMessage message = new RabbitMessage();
		message.setMessage("Message on " + queueName);
		message.setTitle("Title");

		ObjectMapper mapper = new ObjectMapper();
		try {
			byte[] messageBytes = mapper.writeValueAsBytes(message);
			Message amqpMessage = MessageBuilder.withBody(messageBytes)
					.setContentType(MessageProperties.CONTENT_TYPE_JSON).build();
			this.template.convertAndSend(queueName, queueName, amqpMessage, m -> {
				m.getMessageProperties().setPriority(3);
				m.getMessageProperties().setDeliveryMode(MessageDeliveryMode.PERSISTENT);
				m.getMessageProperties().setContentType(MessageProperties.CONTENT_TYPE_JSON);
				m.getMessageProperties().setExpiration(String.valueOf(24 * 60 * 60 * 1000));
				return m;
			});
			this.template.setMandatory(true);
		} catch (JsonProcessingException e) {
			log.error("Json parse error on send message queue");
		}
	}

}
