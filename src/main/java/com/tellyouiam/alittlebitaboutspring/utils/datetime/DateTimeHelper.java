package com.tellyouiam.alittlebitaboutspring.utils.datetime;

import org.apache.commons.validator.GenericValidator;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class DateTimeHelper {
	public static String formatDateString(String reqDate, String currentFormat, String outputFormat) {
		//https://www.baeldung.com/java-string-valid-date
		assert GenericValidator.isDate(reqDate, currentFormat, true);
		DateTimeFormatter currentFormatter = DateTimeFormatter.ofPattern(currentFormat);
		LocalDate localDate = LocalDate.from(currentFormatter.parse(reqDate));
		DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern(outputFormat);
		return outputFormatter.format(localDate);
	}
	
	public static void main(String[] args) {
		System.out.println(formatDateString("01-Aug-19", "dd-MMM-yy", "dd-MM-yyyy"));
	}
}
