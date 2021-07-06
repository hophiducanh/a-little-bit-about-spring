package com.tellyouiam.alittlebitaboutspring.utils.datetime.validator;

import java.time.format.DateTimeFormatter;

public class DateValidators implements DateValidator {
	
	private final DateTimeFormatter dateTimeFormatter;
	
	public DateValidators(DateTimeFormatter dateTimeFormatter) {
		this.dateTimeFormatter = dateTimeFormatter;
	}
	
	@Override
	public boolean isValid(String dateStr) {
		try {
			dateTimeFormatter.parse(dateStr);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return true;
	}
}
