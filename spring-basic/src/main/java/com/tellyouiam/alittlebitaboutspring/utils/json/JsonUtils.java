package com.tellyouiam.alittlebitaboutspring.utils.json;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class JsonUtils {
	private static final Logger logger = LoggerFactory.getLogger(JsonUtils.class);
	
	private static final Map<String, String> data = new HashMap<String, String>() {
		{
			data.put("_EMAIL_", "logbasex@gmail.com");
			data.put("firstName", "Ho");
			data.put("lastName", "Anh");
			data.put("ownerName", "");
			data.put("inviteCode", "inviteCode");
			data.put("bcc", "bcc@test.mail");
			data.put("template", "");
			data.put("subject", "Hello logbasex");
			data.put("senderName", "");
			data.put("signatureName", "");
			data.put("trainerEmail", "senderEmail");
			data.put("footerLink", "footerLink");
		}
	};
	
	private static String toJsonString() {
		String json = null;
		try {
			json = new ObjectMapper().writeValueAsString(JsonUtils.data);
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		return json;
	}
	
	public static void main(String[] args) {
		String data = toJsonString();
		System.out.println(data);
	}
}
