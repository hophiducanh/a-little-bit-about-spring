package com.tellyouiam.alittlebitaboutspring.filter;

import com.opencsv.bean.CsvToBeanFilter;

public class ValidLineFilter implements CsvToBeanFilter {
	@Override
	public boolean allowLine(String[] line) {
//		return !String.join(",", line).contains("{}");
		return true;
	}
}
