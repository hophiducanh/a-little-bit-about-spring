package com.tellyouiam.alittlebitaboutspring.utils.converter;

import com.opencsv.bean.AbstractBeanField;
import com.tellyouiam.alittlebitaboutspring.utils.string.OnboardHelper;

public class CsvStringConverter extends AbstractBeanField {
	@Override
	protected Object convert(String value) {
		return OnboardHelper.getCsvCellValue(value);
	}
}
