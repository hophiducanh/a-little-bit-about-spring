package com.tellyouiam.alittlebitaboutspring.utils.string;

import org.springframework.util.StringUtils;

public class OnboardHelper {
	
	private static final String NULL_VALUE_AS_STRING = "NULL";
	private static final String MULTIPLE_SPACES_PATTERN = "[\\s]{2,}";
	private static final String SEMICOLON_WITH_SPACE_PATTERN = "([\\s]?);([\\s]?)";
	
	public static String[] readCsvLine(String line) {
		return line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1);
	}

	public static String getCsvCellValueAtIndex(String[] r, int index) {
		return index != -1 ? readCsvRow(r, index) : org.apache.commons.lang3.StringUtils.EMPTY;
	}

	public static String readCsvRow(String[] r, int index) {

		String stringValue = r[index];
		if (StringUtils.isEmpty(stringValue))
			return stringValue;
		
		// if text is like 'Null' >> use blank
		if (stringValue.equalsIgnoreCase(NULL_VALUE_AS_STRING))
			return "";
		
		// remove quotes
		stringValue = stringValue.replace("\"", "").trim();
		
		// trim around ";"
		stringValue = stringValue.replaceAll(OnboardHelper.SEMICOLON_WITH_SPACE_PATTERN, ";");
		
		// trim multiple spaces
		stringValue = stringValue.replaceAll(OnboardHelper.MULTIPLE_SPACES_PATTERN, " ");
		
		// do not allow value end with ";" or "."
		if (stringValue.endsWith(";") || stringValue.endsWith(".")) {
			stringValue = stringValue.substring(0, stringValue.length() - 1);
		}
		
		return stringValue;
	}
	
	public static String getCsvCellValue(String stringValue) {
		if (StringUtils.isEmpty(stringValue))
			return stringValue;
		
		// if text is like 'Null' >> use blank
		if (stringValue.equalsIgnoreCase(NULL_VALUE_AS_STRING))
			return "";
		
		// remove quotes
		stringValue = stringValue.replace("\"", "").trim();
		
		// trim around ";"
		stringValue = stringValue.replaceAll(OnboardHelper.SEMICOLON_WITH_SPACE_PATTERN, ";");
		
		// trim multiple spaces
		stringValue = stringValue.replaceAll(OnboardHelper.MULTIPLE_SPACES_PATTERN, " ");
		
		// do not allow value end with ";" or "."
		if (stringValue.endsWith(";") || stringValue.endsWith(".")) {
			stringValue = stringValue.substring(0, stringValue.length() - 1);
		}
		
		return stringValue;
	}
	
	public static String getPostcode(String value) {
		if (StringUtils.isEmpty(value))
			return null;
		else {
			if (Integer.parseInt(value) == 0)
				return "";
			else if (value.matches("\\d+"))
				return value;
			else {
				System.out.println("********** Postcode is not numerical :{}" + value);
				
				// if is not number (Australia type) then must contain at least a number
				if (value.matches(".*\\d+.*")) {
					System.out.println("Postcode contain numerical => passed:{}" + value);
					return value;
				}
				return null;
			}
		}
	}
}
