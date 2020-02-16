package com.pethoalpar.sender;

import org.springframework.amqp.core.FanoutExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author alpar.petho
 *
 */
@Configuration
public class SenderExchangeConfig {

	@Bean(name = "senderExchange")
	public FanoutExchange addSenderExchange() {
		return new FanoutExchange("exchange", true, false);
	}

}
