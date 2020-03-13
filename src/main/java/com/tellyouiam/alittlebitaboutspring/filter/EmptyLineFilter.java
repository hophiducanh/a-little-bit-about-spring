package com.tellyouiam.alittlebitaboutspring.filter;

import com.opencsv.bean.CsvToBeanFilter;

public class EmptyLineFilter implements CsvToBeanFilter {
	@Override
	public boolean allowLine(String[] line) {
		for (String element : line) {
			if (element != null && element.length() > 0) {
				return true;
			}
		}
		return false;
	}
}
