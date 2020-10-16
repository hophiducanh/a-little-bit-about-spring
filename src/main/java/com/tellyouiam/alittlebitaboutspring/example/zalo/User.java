package com.tellyouiam.alittlebitaboutspring.example.zalo;

import com.samczsun.skype4j.Skype;
import com.samczsun.skype4j.SkypeBuilder;
import com.samczsun.skype4j.exceptions.ConnectionException;
import com.samczsun.skype4j.exceptions.InvalidCredentialsException;
import com.samczsun.skype4j.exceptions.NotParticipatingException;

import java.io.IOException;

public class User {
	public static void main(String[] args) throws IOException, InterruptedException, ConnectionException, NotParticipatingException, InvalidCredentialsException {
		Skype skype = new SkypeBuilder("contact.hoducanh@gmail.com","Ducanh851997").withAllResources().build();
		skype.login();
		System.out.println(skype.getUsername());
	}
}
