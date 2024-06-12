package com.logbasex.rabbitmq.resource;

import com.logbasex.rabbitmq.service.RabbitMqWebsocketService;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.context.annotation.DependsOn;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;

@RestController
@RequiredArgsConstructor
@RequestMapping(
		value = "/rabbitmq",
		produces = MediaType.APPLICATION_JSON_VALUE
)
@DependsOn("rabbitMqAdminClient")
public class RabbitMqWebsocketResource {

	private final RabbitAdmin rabbitAdmin;
	private final RabbitMqWebsocketService rabbitMqWebsocketSv;

	@PostConstruct
	public void initQueue() {
		TopicExchange topicExchange = new TopicExchange("logbasex");
		Queue queue = new Queue("logbasex_queue");
		rabbitAdmin.declareQueue(queue);
		rabbitAdmin.declareBinding(
				BindingBuilder.bind(queue).to(topicExchange).with("logbasex_router_key")
		);
	}

	// create api to send message to client
	// https://www.rabbitmq.com/docs/web-stomp
	@PostMapping("/send")
	public boolean send() {
		rabbitMqWebsocketSv.sendMessage("logbasex", "logbasex_router_key", "hello");
		return true;
	}
}