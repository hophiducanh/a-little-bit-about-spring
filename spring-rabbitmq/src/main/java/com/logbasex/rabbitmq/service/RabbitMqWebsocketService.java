package com.logbasex.rabbitmq.service;

public interface RabbitMqWebsocketService {
	void sendMessage(String exchange, String routingKey, final Object object);
}
