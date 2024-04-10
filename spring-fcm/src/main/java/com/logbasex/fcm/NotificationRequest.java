package com.logbasex.fcm;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NotificationRequest {
	private String title;
	private String body;
	private String token;
}
