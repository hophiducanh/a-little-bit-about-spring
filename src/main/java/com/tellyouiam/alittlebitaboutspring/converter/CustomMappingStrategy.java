package com.tellyouiam.alittlebitaboutspring.converter;

import com.opencsv.bean.ColumnPositionMappingStrategy;

public class CustomMappingStrategy<T> extends ColumnPositionMappingStrategy<T> {
	private static final String[] HEADER = new String[]{"OwnerName", "Current", "Over30", "Over60", "Over90"};;
	
	@Override
	public String[] generateHeader(T bean) {
		return HEADER;
	}
}