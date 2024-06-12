package com.logbasex.rabbitmq.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RabbitMqPermissionRequest {
	private String configure;
	private String write;
	private String read;
}
