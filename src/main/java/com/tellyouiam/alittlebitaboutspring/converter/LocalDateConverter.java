package com.tellyouiam.alittlebitaboutspring.converter;

import com.opencsv.bean.AbstractBeanField;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.SignStyle;

import static java.time.temporal.ChronoField.DAY_OF_MONTH;
import static java.time.temporal.ChronoField.MONTH_OF_YEAR;
import static java.time.temporal.ChronoField.YEAR;

public class LocalDateConverter extends AbstractBeanField<LocalDate> {
	
	private static final DateTimeFormatter AUSTRALIA_FORMAL_DATE_FORMAT;
	static {
		AUSTRALIA_FORMAL_DATE_FORMAT = new DateTimeFormatterBuilder()
				.appendValue(DAY_OF_MONTH, 2)
				.appendLiteral('/')
				.appendValue(MONTH_OF_YEAR, 2)
				.appendLiteral('/')
				.appendValue(YEAR, 4)
				.toFormatter();
	}
	
	private static final DateTimeFormatter AMERICAN_CUSTOM_LOCAL_DATE;
	static {
		AMERICAN_CUSTOM_LOCAL_DATE = new DateTimeFormatterBuilder()
				.appendValue(MONTH_OF_YEAR, 1, 2, SignStyle.NEVER)
				.appendLiteral('/')
				.appendValue(DAY_OF_MONTH, 1,2, SignStyle.NEVER)
				.appendLiteral('/')
				.appendValue(YEAR, 2, 4, SignStyle.NEVER)
				.toFormatter();
	}
	
	private static final String IS_DATE_MONTH_YEAR_FORMAT_PATTERN = "^(?:(?:31([/\\-.])(?:0?[13578]|1[02]))\\1|" +
			"(?:(?:29|30)([/\\-.])(?:0?[13-9]|1[0-2])\\2))(?:(?:1[6-9]|[2-9]\\d)?\\d{2})$|" +
			"^(?:29([/\\-.])0?2\\3(?:(?:1[6-9]|[2-9]\\d)?(?:0[48]|[2468][048]|[13579][26])|(?:(?:16|[2468][048]|[3579][26])00)))$|" +
			"^(?:0?[1-9]|1\\d|2[0-8])([/\\-.])(?:(?:0?[1-9])|(?:1[0-2]))\\4(?:(?:1[6-9]|[2-9]\\d)?\\d{2})$";
	
	private static final String IS_MONTH_DATE_YEAR_FORMAT_PATTERN = "^(?:(?:(?:0?[13578]|1[02])([/\\-.])31)\\1|" +
			"(?:(?:0?[13-9]|1[0-2])([/\\-.])(?:29|30)\\2))(?:(?:1[6-9]|[2-9]\\d)?\\d{2})$|" +
			"^(?:0?2([/\\-.])29\\3(?:(?:1[6-9]|[2-9]\\d)?(?:0[48]|[2468][048]|[13579][26])|(?:(?:16|[2468][048]|[3579][26])00)))$|" +
			"^(?:(?:0?[1-9])|(?:1[0-2]))([/\\-.])(?:0?[1-9]|1\\d|2[0-8])\\4(?:(?:1[6-9]|[2-9]\\d)?\\d{2})$";
	
	@Override
	protected Object convert(String value) {
		
		String ausDateAsStr = LocalDate.parse(value, AMERICAN_CUSTOM_LOCAL_DATE).format(AUSTRALIA_FORMAL_DATE_FORMAT);
		return ausDateAsStr;
	}
}
