package com.logbasex.rabbitmq.service;

import com.logbasex.rabbitmq.config.RabbitMqProperties;
import com.logbasex.rabbitmq.dto.RabbitMqPermissionRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.stereotype.Service;
import retrofit2.Call;
import retrofit2.Response;

import javax.annotation.PostConstruct;
import java.time.Duration;

@Log4j2
@Service
@RequiredArgsConstructor
public class RabbitMqAdminClient {
	private final RabbitMqProperties rabbitMqProperties;
	private final RabbitAdmin rabbitAdmin;
	private RabbitMqAdminApi rabbitMqAdminApi;

	@PostConstruct
	public void init() {
		rabbitMqAdminApi = RetrofitService.createService(
				RabbitMqAdminApi.class,
				new RabbitMqAdminApiInterceptor(rabbitMqProperties.getUsername(), rabbitMqProperties.getPassword()),
				rabbitMqProperties.getApiEndpoint(),
				Duration.ofSeconds(30),
				Duration.ofSeconds(30)
		);

		createVirtualHost();
		setPermissions();
		createTopicExchange();
	}

	private void createTopicExchange() {
		TopicExchange topicExchange = new TopicExchange("logbasex");
		rabbitAdmin.declareExchange(topicExchange);
	}

	public void createVirtualHost() {
		try {
			Call<Void> call = rabbitMqAdminApi.createVhost(rabbitMqProperties.getVirtualHost());
			Response<Void> response = call.execute();

			if (!response.isSuccessful()) {
				log.info("Failed to create vhost: {}", response.errorBody());
			}
		} catch (Exception e) {
			log.error("An error was occurred while create virtual host: {}", e.getMessage(), e);
		}
	}

	public void setPermissions() {
		RabbitMqPermissionRequest permissionRequest = new RabbitMqPermissionRequest();
		permissionRequest.setConfigure(".*");
		permissionRequest.setRead(".*");
		permissionRequest.setWrite(".*");

		try {
			Call<Void> call = rabbitMqAdminApi.setPermissions(rabbitMqProperties.getVirtualHost(), rabbitMqProperties.getUsername(), permissionRequest);
			Response<Void> response = call.execute();

			if (!response.isSuccessful()) {
				log.info("Failed to set permissions: {}", response.errorBody());
			}
		} catch (Exception e) {
			log.error("An error was occurred while set permission: {}", e.getMessage(), e);
		}
	}
}
