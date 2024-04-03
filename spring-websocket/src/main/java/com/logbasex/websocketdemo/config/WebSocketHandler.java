package com.logbasex.websocketdemo.config;

import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;

public class WebSocketHandler implements org.springframework.web.socket.WebSocketHandler {

	@Override
	public void afterConnectionEstablished(WebSocketSession session) {
		System.out.println("Connection established with session: " + session.getId());
	}

	@Override
	public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws IOException {
		System.out.println("Received message: " + message.getPayload());
		session.sendMessage(new TextMessage("Echo: " + message.getPayload()));
	}

	@Override
	public void handleTransportError(WebSocketSession session, Throwable exception) {

	}

	@Override
	public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) {
		System.out.println("Connection closed. Session: " + session.getId());
	}

	@Override
	public boolean supportsPartialMessages() {
		return false;
	}
}
