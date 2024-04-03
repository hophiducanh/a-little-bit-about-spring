package com.logbasex.websocketdemo.controller;

import com.logbasex.websocketdemo.model.ChatMessage;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;

@Controller
public class ChatController {

	/**
	 * ^@MessageMapping("/chat.sendMessage"): This annotation indicates that the method sendMessage should be called
	 * when a message is sent to the WebSocket destination /chat.sendMessage.
	 * This is similar to how @RequestMapping works for HTTP requests, but it's for messages coming in from a WebSocket.
	 * </p>
	 * ^@SendTo("/topic/public"): This annotation specifies that the return value of the method should be sent
	 * to the given destination, in this case, /topic/public.
	 * This means that after sendMessage is called and processes the message, the ChatMessage object it returns
	 * will be sent to all clients subscribed to the /topic/public broker channel.
	 */
	@MessageMapping("/chat.sendMessage")
	@SendTo("/topic/public")
	public ChatMessage sendMessage(@Payload ChatMessage chatMessage) {
		return chatMessage;
	}
	
	@MessageMapping("/chat.addUser")
	@SendTo("/topic/public")
	public ChatMessage addUser(@Payload ChatMessage chatMessage,
	                           SimpMessageHeaderAccessor headerAccessor) {
		// Add username in web socket session
		headerAccessor.getSessionAttributes().put("username", chatMessage.getSender());
		return chatMessage;
	}
}
