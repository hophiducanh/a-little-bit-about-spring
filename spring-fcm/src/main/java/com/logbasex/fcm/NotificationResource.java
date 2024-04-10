package com.logbasex.fcm;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Log4j2
@RestController
@RequiredArgsConstructor
public class NotificationResource {

	private final FirebaseCloudMessagingService firebaseCloudMessagingSv;

	/**
	 * curl --location --request POST 'http://localhost:8183/push' \
	 * --header 'Content-Type: application/json' \
	 * --data-raw '{
	 *     "title": "Hello",
	 *     "body": "hello",
	 *     "token": "d5PTo67wmiB0fWkhPwPr4x:APA91bGzCNeTsHf2958xXkdvud0c0GTJHT2FxLK3-Q0fK1FUGH1a0t__FkcDXFHVNI7y0jpsBIRR9zHHCoPXMMpJEN9_62aQ6Wwmxd76KcidC_hBYTu2fRm4DUvZtAHBDeQIflPiQt5G"
	 * }'
	 */
	@PostMapping(value = "/push")
	public Object push(@RequestBody NotificationRequest request) {
		NotificationRequest notificationRequest = new NotificationRequest();
		notificationRequest.setTitle(request.getTitle());
		notificationRequest.setBody(request.getBody());
		notificationRequest.setToken(request.getToken());
		firebaseCloudMessagingSv.pushNotification(notificationRequest);
		return ResponseEntity.ok(true);
	}
}
