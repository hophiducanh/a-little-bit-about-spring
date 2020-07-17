package com.tellyouiam.basic.datetime.datetimeformatterannotaion;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;

import static java.time.format.DateTimeFormatter.ISO_LOCAL_DATE;
import static java.time.format.DateTimeFormatter.ISO_LOCAL_TIME;
import static org.apache.commons.lang3.StringUtils.SPACE;

public class Main {
	
	private static final DateTimeFormatter LOCAL_DATE_TIME_FORMATTER = new DateTimeFormatterBuilder()
			.append(ISO_LOCAL_DATE)
			.appendLiteral(SPACE)
			.append(ISO_LOCAL_TIME)
			.toFormatter();
	
	public static void main(String[] args) {
		Request request = new Request();
		request.setLocalDateTime(LocalDateTime.parse("2020-07-18 11:45:00", LOCAL_DATE_TIME_FORMATTER));
		request.setDateTime(LocalDateTime.parse("2020-07-18 11:45:00", LOCAL_DATE_TIME_FORMATTER));
		System.out.println("request = " + request);
	}
}
