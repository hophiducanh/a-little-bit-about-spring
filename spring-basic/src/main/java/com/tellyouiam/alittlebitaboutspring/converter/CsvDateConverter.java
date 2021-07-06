package com.tellyouiam.alittlebitaboutspring.converter;

import com.opencsv.bean.AbstractBeanField;
import com.tellyouiam.alittlebitaboutspring.utils.datetime.DateTimeHelper;

public class CsvDateConverter extends AbstractBeanField {
	@Override
	protected String convert(String value) {
		return DateTimeHelper.formatDateString(value, "dd-MMM-yy", "dd-MM-yyyy");
	}
}
