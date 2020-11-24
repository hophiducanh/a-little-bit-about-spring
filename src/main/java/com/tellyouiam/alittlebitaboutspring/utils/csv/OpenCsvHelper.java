package com.tellyouiam.alittlebitaboutspring.utils.csv;

import com.opencsv.bean.CsvToBeanBuilder;

import java.io.Reader;
import java.util.List;

public class OpenCsvHelper {
	
	public static <T> List<T> mapCsvRecordToBeans(Class<T> mapToClass, Reader reader) {
		return new CsvToBeanBuilder<T>(reader)
				.withType(mapToClass)
				.withIgnoreLeadingWhiteSpace(true)
				.withFilter(OpenCsvHelper::allowNotEmptyLine)
				.build()
				.parse();
	}
	
	public static boolean allowNotEmptyLine(String[] strings) {
		for (String one : strings) {
			if (one != null && one.length() > 0) {
				return true;
			}
		}
		return false;
	}
}
