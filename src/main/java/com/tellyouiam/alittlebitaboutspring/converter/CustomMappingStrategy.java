package com.tellyouiam.alittlebitaboutspring.converter;

import com.opencsv.bean.BeanField;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.HeaderColumnNameTranslateMappingStrategy;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;
import org.apache.commons.lang3.StringUtils;

public class CustomMappingStrategy<T> extends HeaderColumnNameTranslateMappingStrategy<T> {
	
	@Override
	public String[] generateHeader(T bean) throws CsvRequiredFieldEmptyException {
		final int numColumns = findMaxFieldIndex();
		if (!isAnnotationDriven() || numColumns == -1) {
			return super.generateHeader(bean);
		}
		
		String[] header = new String[numColumns + 1];
		
		BeanField beanField;
		for (int i = 0; i <= numColumns; i++) {
			beanField = findField(i);
			String columnHeaderName = extractHeaderName(beanField);
			header[i] = columnHeaderName;
		}
		return header;
	}
	
	private String extractHeaderName(final BeanField beanField) {
		if (beanField == null || beanField.getField() == null || beanField.getField().getDeclaredAnnotationsByType(CsvBindByName.class).length == 0) {
			return StringUtils.EMPTY;
		}
		
		final CsvBindByName bindByNameAnnotation = beanField.getField().getDeclaredAnnotationsByType(CsvBindByName.class)[0];
		return bindByNameAnnotation.column();
	}
}