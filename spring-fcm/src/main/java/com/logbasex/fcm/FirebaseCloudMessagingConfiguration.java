package com.logbasex.fcm;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.FirebaseMessaging;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;

@Configuration
public class FirebaseCloudMessagingConfiguration {

	@Bean
	public FirebaseMessaging firebaseMessaging() throws IOException {
		ClassPathResource classPathResource = new ClassPathResource("fcm/spring-fcm-firebase-adminsdk.json");

		GoogleCredentials googleCredentials = GoogleCredentials.fromStream(classPathResource.getInputStream());
		FirebaseOptions firebaseOptions = FirebaseOptions.builder()
				.setCredentials(googleCredentials)
				.build();
		FirebaseApp app = FirebaseApp.initializeApp(firebaseOptions, "fandelo-one");
		return FirebaseMessaging.getInstance(app);
	}
}
