package com.logbasex.websocketdemo.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebsocketConfig implements WebSocketMessageBrokerConfigurer {
	/**
	 * STOMP sub-protocol
	 * WebSocket is the transport medium, STOMP is a standardized method of communication over a WebSocket. It's the same as HTTP is to TCP.
	 * <p>
	 * While WebSocket defines a protocol for bidirectional communication between client and server, it does not put any condition on the message to be exchanged.
	 * This is left open for parties in the communication to agree as part of sub-protocol negotiation.
	 * <p>
	 * It’s not convenient to develop a sub-protocol for non-trivial applications. Fortunately, there are many
	 * popular sub-protocols like STOMP available for use.
	 * STOMP stands for Simple Text Oriented Messaging Protocol and works over WebSocket.
	 * Spring Boot has first class support for STOMP, which we’ll make use of in our tutorial.
	 */
	@Override
	public void registerStompEndpoints(StompEndpointRegistry registry) {
		//https://stackoverflow.com/questions/52731686/websocket-vs-sockjs-object
		//https://www.slideshare.net/AhmedurRahmanShovon/websockets-and-sockjs-real-time-chatting
		//Enable STOMP support by register STOMP endpoint /ws
		registry.addEndpoint("/ws")
				//when client is set up outside this project.
				.setAllowedOriginPatterns("*")
//				.setAllowedOrigins("http://127.0.0.1:46809")
				.withSockJS();
	}
	
	@Override
	public void configureMessageBroker(MessageBrokerRegistry registry) {
		registry.setApplicationDestinationPrefixes("/app");
		registry.enableSimpleBroker("/topic");
	}
}
