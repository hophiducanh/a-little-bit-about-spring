package com.tellyouiam.alittlebitaboutspring.converter;

import com.opencsv.bean.AbstractCsvConverter;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.ResolverStyle;
import java.time.format.SignStyle;

import static java.time.temporal.ChronoField.DAY_OF_MONTH;
import static java.time.temporal.ChronoField.MONTH_OF_YEAR;
import static java.time.temporal.ChronoField.YEAR;

public class LocalDateConverter extends AbstractCsvConverter {
	
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
				.toFormatter()
				.withResolverStyle(ResolverStyle.STRICT);
	}

	private static final DateTimeFormatter AUSTRALIA_CUSTOM_DATE_FORMAT;
	static {
		AUSTRALIA_CUSTOM_DATE_FORMAT = new DateTimeFormatterBuilder()
				.appendValue(DAY_OF_MONTH, 1,2, SignStyle.NEVER)
				.appendLiteral('/')
				.appendValue(MONTH_OF_YEAR, 1, 2, SignStyle.NEVER)
				.appendLiteral('/')
				.appendValue(YEAR, 2, 4, SignStyle.NEVER)
				.toFormatter()
				.withResolverStyle(ResolverStyle.STRICT);
	}
	
	@Override
	public Object convertToRead(String dateStr) {
		return LocalDate.parse(dateStr, AUSTRALIA_CUSTOM_DATE_FORMAT).format(AUSTRALIA_FORMAL_DATE_FORMAT);
	}
}
