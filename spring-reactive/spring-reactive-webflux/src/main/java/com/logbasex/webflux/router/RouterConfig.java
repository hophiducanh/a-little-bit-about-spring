package com.logbasex.webflux.router;

import com.logbasex.webflux.handler.CustomerHandler;
import com.logbasex.webflux.handler.CustomerStreamHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

@Configuration
@RequiredArgsConstructor
public class RouterConfig {
	
	private final CustomerHandler customerHandler;
	private final CustomerStreamHandler customerStreamHandler;
	
	@Bean
	public RouterFunction<ServerResponse> routerFunction() {
		return RouterFunctions
				.route()
				.GET("/router/customers", customerHandler::loadCustomers)
				.GET("/router/customers/stream", customerStreamHandler::loadCustomersStream)
				.GET("/router/customers/{input}", customerHandler::findCustomer)
				.POST("/router/customers", customerHandler::saveCustomer)
				.build();
	}
}
