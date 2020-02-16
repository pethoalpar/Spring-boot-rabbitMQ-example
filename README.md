# Spring boot rabbitMQ example

## Add maven dependency

```xml
<dependency>
  <groupId>org.springframework.boot</groupId>
  <artifactId>spring-boot-starter-amqp</artifactId>
</dependency>
<dependency>
  <groupId>com.fasterxml.jackson.core</groupId>
  <artifactId>jackson-databind</artifactId>
</dependency>
```    

## Configure properties

```yml
spring:   
  rabbitmq:
    host: 192.168.100.91
    port: 5672
    username: guest
    password: guest
    listener:
      simple:
        retry:
          enabled: true
          initial-interval: 3s
          max-attempts: 10
          max-interval: 30s
          multiplier: 2
```    

## Enable rabbit and send message

```java
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
```

## Configure the rabbit

```java
@Configuration
public class RabbitConfig implements RabbitListenerConfigurer {

	@Bean
	public RabbitTemplate rabbitTemplate(final ConnectionFactory connectionFactory) {
		final RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
		rabbitTemplate.setMessageConverter(producerJackson2MessageConverter());
		return rabbitTemplate;
	}

	@Bean
	public Jackson2JsonMessageConverter producerJackson2MessageConverter() {
		return new Jackson2JsonMessageConverter();
	}

	@Bean
	public MappingJackson2MessageConverter consumerJackson2MessageConverter() {
		return new MappingJackson2MessageConverter();
	}

	@Bean
	public DefaultMessageHandlerMethodFactory messageHandlerMethodFactory() {
		DefaultMessageHandlerMethodFactory factory = new DefaultMessageHandlerMethodFactory();
		factory.setMessageConverter(consumerJackson2MessageConverter());
		return factory;
	}

	@Override
	public void configureRabbitListeners(final RabbitListenerEndpointRegistrar registrar) {
		registrar.setMessageHandlerMethodFactory(messageHandlerMethodFactory());
	}
}
```

## Declare the queues

```java
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
```

## Declare the exchange

```java
@Configuration
public class SenderExchangeConfig {

	@Bean(name = "senderExchange")
	public FanoutExchange addSenderExchange() {
		return new FanoutExchange("exchange", true, false);
	}

}
```

## Make a listener

```java
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
```

## Add a message model

```java
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class RabbitMessage {

	private String uuid;
	private String title;
	private Date timestamp;
	private String message;

}
```
