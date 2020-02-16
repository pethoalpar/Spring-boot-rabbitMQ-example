package com.pethoalpar.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import lombok.extern.log4j.Log4j2;

/**
 * @author alpar.petho
 *
 */
@Configuration
@Log4j2
public class QueueConfig {

	@Bean(name = "queue1")
	public Queue getQueue() {
		if (log.isInfoEnabled()) {
			log.info("Init queue1");
		}
		return new Queue("queue1", true, false, false);
	}

	@Bean("queue1Binding")
	Binding binding(@Qualifier("queue1") Queue queue, @Qualifier("senderExchange") FanoutExchange exchange) {
		return BindingBuilder.bind(queue).to(exchange);
	}

	@Bean(name = "queue2")
	public Queue getQueue2() {
		if (log.isInfoEnabled()) {
			log.info("Init queue2");
		}
		return new Queue("queue2", true, false, false);
	}

	@Bean("queue2Binding")
	Binding binding2(@Qualifier("queue2") Queue queue, @Qualifier("senderExchange") FanoutExchange exchange) {
		return BindingBuilder.bind(queue).to(exchange);
	}

	@Bean(name = "queue3")
	public Queue getQueue3() {
		if (log.isInfoEnabled()) {
			log.info("Init queue3");
		}
		return new Queue("queue3", true, false, false);
	}

}
