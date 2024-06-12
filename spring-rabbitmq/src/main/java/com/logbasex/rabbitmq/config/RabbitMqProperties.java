package com.logbasex.rabbitmq.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "rabbitmq")
@PropertySource({"classpath:rabbitmq.properties"})
public class RabbitMqProperties {
	private String username;
	private String password;
	private String hostname;
	private Integer port;
	private String virtualHost;
	private Api api;

	@Getter
	@Setter
	static class Api {
		private String endpoint;
	}

	public String getApiEndpoint() {
		return this.getApi().getEndpoint();
	}
}
