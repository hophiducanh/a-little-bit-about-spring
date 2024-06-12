package com.logbasex.rabbitmq.service;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import okhttp3.Credentials;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.util.StringUtils;

import java.io.IOException;

@Getter
@Setter
@Log4j2
@NoArgsConstructor
@AllArgsConstructor
public class RabbitMqAdminApiInterceptor implements Interceptor {

	private String username;
	private String password;

	@Override
	public Response intercept( Chain chain) throws IOException {
		Request request = chain.request();

		if (StringUtils.hasText(username) && StringUtils.hasText(password)) {
			request = request.newBuilder()
					.addHeader("Authorization", Credentials.basic(username, password))
					.build();
		}

		return chain.proceed(request);
	}
}
