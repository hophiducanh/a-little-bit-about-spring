package com.tellyouiam.alittlebitaboutspring.library;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.Date;

import static java.time.temporal.ChronoField.DAY_OF_MONTH;
import static java.time.temporal.ChronoField.MONTH_OF_YEAR;
import static java.time.temporal.ChronoField.YEAR;

public class Main {
	public static void main(String[] args) {
		DateTimeFormatter formatter = new DateTimeFormatterBuilder()
				.appendValue(DAY_OF_MONTH, 2)
				.appendLiteral(' ')
				.appendText(MONTH_OF_YEAR)
				.appendLiteral(' ')
				.appendValue(YEAR, 4)
				.toFormatter();
		
		
		LocalDate currentDate = LocalDate.now();
		String date = currentDate.format(formatter);
		System.out.println(date);
		System.out.println(currentDate);
	}
}
