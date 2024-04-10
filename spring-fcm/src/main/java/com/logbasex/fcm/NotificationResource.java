package com.logbasex.fcm;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@Log4j2
@RestController
@RequiredArgsConstructor
public class NotificationResource {

	private final FirebaseCloudMessagingService firebaseCloudMessagingSv;

	@PostMapping(value = "/public/push")
	public Object push(NotificationRequest request) {
		NotificationRequest notificationRequest = new NotificationRequest();
		notificationRequest.setTitle(request.getTitle());
		notificationRequest.setBody(request.getBody());
		notificationRequest.setToken(request.getToken());
		firebaseCloudMessagingSv.pushNotification(notificationRequest);
		return ResponseEntity.ok(true);
	}
}
