package com.logbasex.rabbitmq.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RabbitMqWebsocketRequest {
	private String routingKey;
	private String queueName;
	private Object data;
}
