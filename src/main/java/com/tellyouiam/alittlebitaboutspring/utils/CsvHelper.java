package com.tellyouiam.alittlebitaboutspring.utils;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;

public class CsvHelper {
	private static final Logger logger = LoggerFactory.getLogger(CsvHelper.class);
	
	private static final String IS_DATE_MONTH_YEAR_FORMAT_PATTERN = "^(?:(?:31([/\\-.])(?:0?[13578]|1[02]))\\1|" +
			"(?:(?:29|30)([/\\-.])(?:0?[13-9]|1[0-2])\\2))(?:(?:1[6-9]|[2-9]\\d)?\\d{2})$|" +
			"^(?:29([/\\-.])0?2\\3(?:(?:1[6-9]|[2-9]\\d)?(?:0[48]|[2468][048]|[13579][26])|(?:(?:16|[2468][048]|[3579][26])00)))$|" +
			"^(?:0?[1-9]|1\\d|2[0-8])([/\\-.])(?:(?:0?[1-9])|(?:1[0-2]))\\4(?:(?:1[6-9]|[2-9]\\d)?\\d{2})$";
	
	private static final String IS_MONTH_DATE_YEAR_FORMAT_PATTERN = "^(?:(?:(?:0?[13578]|1[02])([/\\-.])31)\\1|" +
			"(?:(?:0?[13-9]|1[0-2])([/\\-.])(?:29|30)\\2))(?:(?:1[6-9]|[2-9]\\d)?\\d{2})$|" +
			"^(?:0?2([/\\-.])29\\3(?:(?:1[6-9]|[2-9]\\d)?(?:0[48]|[2468][048]|[13579][26])|(?:(?:16|[2468][048]|[3579][26])00)))$|" +
			"^(?:(?:0?[1-9])|(?:1[0-2]))([/\\-.])(?:0?[1-9]|1\\d|2[0-8])\\4(?:(?:1[6-9]|[2-9]\\d)?\\d{2})$";
	
	public static final DateTimeFormatter AUSTRALIA_ISO_DATE = new DateTimeFormatterBuilder()
			.appendOptional(DateTimeFormatter.ofPattern("MM/dd/yyyy"))
			.appendOptional(DateTimeFormatter.ofPattern("M/dd/yyyy"))
			.appendOptional(DateTimeFormatter.ofPattern("MM/d/yyyy"))
			.appendOptional(DateTimeFormatter.ofPattern("M/d/yyyy"))
			.toFormatter();
	
	public static String checkStringIsValidAusDate(String rawString, String line) {
		String foaled = StringUtils.EMPTY;
		
		if (StringUtils.isNotEmpty(rawString)) {
			
			if (rawString.matches(IS_DATE_MONTH_YEAR_FORMAT_PATTERN)) {
				foaled = rawString;
			} else if (rawString.matches(IS_MONTH_DATE_YEAR_FORMAT_PATTERN)) {
				DateTimeFormatter expectedFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
				
				foaled = LocalDate.parse(rawString, AUSTRALIA_ISO_DATE).format(expectedFormatter);
			} else {
				logger.info("UNKNOWN TYPE OF DATE: {} in line : {}", foaled, line);
			}
		}
		return foaled;
	}
}
