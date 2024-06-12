package com.logbasex.rabbitmq.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Log4j2
@Service
@RequiredArgsConstructor
public class RabbitMqWebsocketServiceImpl implements RabbitMqWebsocketService {

	private final RabbitTemplate rabbitTemplate;

	@Override
	public void sendMessage(String exchange, String routingKey, Object object) {
		try {
			rabbitTemplate.convertAndSend(exchange, routingKey, object);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}
}
