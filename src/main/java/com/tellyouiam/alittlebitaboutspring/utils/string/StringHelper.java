package com.tellyouiam.alittlebitaboutspring.utils.string;

import org.apache.commons.collections4.MultiValuedMap;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;

import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.*;
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
			return new SimpleDateFormat(DATE_TIME_FORMAT_IN_CSV).format((Date) value);
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
		return Objects.nonNull(map) ? map.values().toArray(new String[1])[0] : StringUtils.EMPTY;
	}

	public static List<String> customSplitSpecific(String s) {
		List<String> words = new ArrayList<>();
		boolean notInsideComma = true;
		int start = 0;
		for (int i = 0; i < s.length(); i++) {
			if (s.charAt(i) == ',' && notInsideComma) {
				words.add(s.substring(start, i));
				start = i + 1;
			} else if (s.charAt(i) == '"')
				notInsideComma = !notInsideComma;
		}
		words.add(s.substring(start));
		return words;
	}
	
	public static Map<Object, Object> getRequestParams(String url) {
		
		try {
			
			Map<Object, Object> parameters = new HashMap<Object, Object>();
			
			// String url =
			// "http://www.example.com/something.html?one=1&two=2&three=3&three=3a";
			List<NameValuePair> params = URLEncodedUtils.parse(new URI(url), "UTF-8");
			
			for (NameValuePair param : params) {
				
				String key = param.getName();
				if (!org.springframework.util.StringUtils.isEmpty(key)) {
					String value = param.getValue();
					parameters.put(key, value);
				}
			}
			return parameters;
		} catch (Exception e) {
			return null;
		}
	}
	
	public static String extract(String source, String regex) {
		String result = null;
		Pattern p = Pattern.compile(regex);
		Matcher m = p.matcher(source);
		if (m.find()) {
			result = m.group(1);
			result = result.replace("\u00a0", " ").trim();
		}
		return result;
	}
}
