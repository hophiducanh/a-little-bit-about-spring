package com.logbasex.fcm;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Log4j2
@Service
@RequiredArgsConstructor
public class FirebaseCloudMessagingService {

	private final FirebaseMessaging firebaseMessaging;

	/**
	 * <a href="https://firebase.google.com/docs/cloud-messaging/send-message">...</a>
	 */
	public void pushNotification(NotificationRequest request) {
		Map<String, String> firebaseMessageBody = new HashMap<>();
		firebaseMessageBody.put("title", request.getTitle());
		firebaseMessageBody.put("body", request.getBody());

		try {
			Message message = Message.builder()
				.setToken(request.getToken())
				.putAllData(firebaseMessageBody)
				.build();

			firebaseMessaging.send(message);
		} catch (FirebaseMessagingException e) {
			log.error("Firebase error sending: {}", e.getMessage(), e);
		}
	}
}
