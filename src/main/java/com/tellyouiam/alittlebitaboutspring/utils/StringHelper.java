package com.tellyouiam.alittlebitaboutspring.utils;

import org.apache.commons.collections4.MultiValuedMap;
import org.apache.commons.lang3.StringUtils;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringHelper {
	
	private static final String DATE_TIME_FORMAT_IN_CSV = "dd/MM/yyyy h:m:s a";
	private static final String EMAIL_PATTERN = "^[_A-Za-z0-9\\-+&]+(\\.[_A-Za-z0-9\\-+&]+)*@[A-Za-z0-9\\-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
	
	public static String csvValue(Object value) {
		if (value == null) {
			return "";
		} else if (value instanceof String) {
			return String.format("\"%s\"", ((String) value).replace('"', '\'').replace("\ufe0f", ""));
		} else if (value instanceof Date) {
			return new SimpleDateFormat(DATE_TIME_FORMAT_IN_CSV).format((Date) value).toString();
		}
		
		return value.toString();
	}
	
	public static List<String> convertStringBuilderToList(StringBuilder resource) {
		return Arrays.asList(resource.toString().split("\n"));
	}
	
	public static boolean isValidEmail(String email) {
		if (StringUtils.isEmpty(email))
			return false;
		Pattern pattern = Pattern.compile(EMAIL_PATTERN);
		Matcher matcher = pattern.matcher(email);
		return matcher.matches();
	}
	
	public static int countMatches(Matcher matcher) {
		int counter = 0;
		while (matcher.find())
			counter++;
		return counter;
	}

	public static Double getMultiMapSingleDoubleValue(MultiValuedMap<String,Double> map) {
		Double mapValue = map.values().toArray(new Double[1])[0];
		return mapValue;
	}

	public static Double getMultiMapSingleValue(MultiValuedMap<String, Double> map) {
		Double mapValue = map.values().toArray(new Double[1])[0];
		return mapValue;
	}
	
	public static String getMultiMapSingleStringValue(MultiValuedMap<String,String> map) {
		return map.values().toArray(new String[1])[0];
	}
}
