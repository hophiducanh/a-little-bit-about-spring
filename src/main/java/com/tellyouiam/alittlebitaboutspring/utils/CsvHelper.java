package com.tellyouiam.alittlebitaboutspring.utils;

import com.opencsv.CSVReader;
import com.tellyouiam.alittlebitaboutspring.exception.CustomException;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.List;
import java.util.Objects;

public class CsvHelper {
	private static final Logger logger = LoggerFactory.getLogger(CsvHelper.class);
	
	private static final String IS_DATE_MONTH_YEAR_FORMAT_PATTERN = "^(?:(?:31([/\\-.])(?:0?[13578]|1[02]))\\1|" +
			"(?:(?:29|30)([/\\-.])(?:0?[13-9]|1[0-2])\\2))(?:(?:1[6-9]|[2-9]\\d)?\\d{2})$|" +
			"^(?:29([/\\-.])0?2\\3(?:(?:1[6-9]|[2-9]\\d)?(?:0[48]|[2468][048]|[13579][26])|(?:(?:16|[2468][048]|[3579][26])00)))$|" +
			"^(?:0?[1-9]|1\\d|2[0-8])([/\\-.])(?:(?:0?[1-9])|(?:1[0-2]))\\4(?:(?:1[6-9]|[2-9]\\d)?\\d{2})$";
	
	private static final String IS_MONTH_DATE_YEAR_FORMAT_PATTERN = "^(?:(?:(?:0?[13578]|1[02])([/\\-.])31)\\1|" +
			"(?:(?:0?[13-9]|1[0-2])([/\\-.])(?:29|30)\\2))(?:(?:1[6-9]|[2-9]\\d)?\\d{2})$|" +
			"^(?:0?2([/\\-.])29\\3(?:(?:1[6-9]|[2-9]\\d)?(?:0[48]|[2468][048]|[13579][26])|(?:(?:16|[2468][048]|[3579][26])00)))$|" +
			"^(?:(?:0?[1-9])|(?:1[0-2]))([/\\-.])(?:0?[1-9]|1\\d|2[0-8])\\4(?:(?:1[6-9]|[2-9]\\d)?\\d{2})$";
	
	private static final DateTimeFormatter AUSTRALIA_ISO_DATE = new DateTimeFormatterBuilder()
			.appendOptional(DateTimeFormatter.ofPattern("MM/dd/yyyy"))
			.appendOptional(DateTimeFormatter.ofPattern("M/dd/yyyy"))
			.appendOptional(DateTimeFormatter.ofPattern("MM/d/yyyy"))
			.appendOptional(DateTimeFormatter.ofPattern("M/d/yyyy"))
			.toFormatter();
	
	public static String checkStringIsValidAusDate(String rawString, String line) {
		String foaled = StringUtils.EMPTY;
		
		if (StringUtils.isNotEmpty(rawString)) {
			
			if (rawString.matches(IS_DATE_MONTH_YEAR_FORMAT_PATTERN)) {
				foaled = rawString;
			} else if (rawString.matches(IS_MONTH_DATE_YEAR_FORMAT_PATTERN)) {
				DateTimeFormatter expectedFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
				
				foaled = LocalDate.parse(rawString, AUSTRALIA_ISO_DATE).format(expectedFormatter);
			} else {
				logger.info("UNKNOWN TYPE OF DATE: {} in line : {}", foaled, line);
			}
		}
		return foaled;
	}
	
	
	public static String validateInputFile (List<String> ownerFile) throws CustomException {
		return validateInputFile(ownerFile, null);
	}
	
	public static String validateInputFile(List<String> ownerFile, List<String> ownershipFile) throws CustomException {
		
		if (Objects.isNull(ownerFile) && Objects.isNull(ownershipFile)) {
			throw new CustomException(new ErrorInfo("Owner Data or Ownership Data is required!"));
		}
		
		StringBuilder errorSb = new StringBuilder();
		
		int mobileIndex = 7;
		int phoneIndex = 8;
		int faxIndex = 9;
		
		String fileOwnerErrors = CsvHelper.validateFile(ownerFile, mobileIndex, phoneIndex, faxIndex);
		
		if (!org.springframework.util.StringUtils.isEmpty(fileOwnerErrors)) {
			errorSb.append("Error in file owner:\n");
			errorSb.append(fileOwnerErrors);
			errorSb.append("\n");
		}
		
		// file ownership >> contain more 2 columns at first: horseId & horseName >> add
		// 2 to index
		mobileIndex += 2;
		phoneIndex += 2;
		faxIndex += 2;
		
		String fileOwnershipErrors = CsvHelper.validateFile(ownershipFile, mobileIndex, phoneIndex, faxIndex);
		
		if (StringUtils.isNotEmpty(fileOwnershipErrors)) {
			errorSb.append("Error in file ownership:\n");
			errorSb.append(fileOwnershipErrors);
		}
		
		return errorSb.toString();
	}
	
	private static String validateFile(List<String> data, int mobileIndex, int phoneIndex, int faxIndex) throws CustomException {
		StringBuilder sb = new StringBuilder();
		if (CollectionUtils.isEmpty(data)) return StringUtils.EMPTY;
		
		if (!CollectionUtils.isEmpty(data)) {
			StringBuilder lineBuilder = null;
			
			for (String item : data) {
				lineBuilder = new StringBuilder();
				String[] r = OnboardHelper.readCsvLine(item);
				
				String mobile = OnboardHelper.getCsvCellValue(r, mobileIndex);
				String phone = OnboardHelper.getCsvCellValue(r, phoneIndex);
				String fax = OnboardHelper.getCsvCellValue(r, faxIndex);
				
				if (isValidPhoneMobileFaxNumber(mobile))
					lineBuilder.append(String.format("Invalid mobile:%s\n", mobile));
				
				if (isValidPhoneMobileFaxNumber(phone))
					lineBuilder.append(String.format("Invalid phone:%s\n", phone));
				
				if (isValidPhoneMobileFaxNumber(fax))
					lineBuilder.append(String.format("Invalid fax:%s\n", fax));
				
				
				if (!org.springframework.util.StringUtils.isEmpty(lineBuilder.toString())) {
					sb.append(String.format("**********	Invalid data at line:%s\n", item));
					sb.append(lineBuilder.toString());
					sb.append("\n");
				}
			}
		}
			return sb.toString();
	}
	
	// phone number can be:
	// 06 12 34 56 78
	// 0612345678
	// 06.12.34.56.78
	// 06-12-34-56-78
	// +33612345678
	// +33 (0)6 12 34 56 78
	// 0011852 9202 0321
	private static boolean isValidPhoneMobileFaxNumber(String number) throws CustomException {
		if (StringUtils.isNotEmpty(number)) {
			return !number.matches("^[\\d+()\\- .]+$");
		}
		return false;
	}
	
	private static String readMobilePhoneNumber(String number) {
		number = number.trim();
		if (StringUtils.isNotEmpty(number)) {
			if (!number.matches("[\\d+()\\-\\s.]+")) {
				number = number.replaceAll("[^\\d+()\\-\\s.]+", "");
			}
		}
		return number;
	}
	
	public List<String[]> getCsvData(String filePath) {
		List<String[]> data = null;
		try {
			data = new CSVReader(
					new FileReader("C:\\Users\\conta\\OneDrive\\Desktop\\data\\POB-410-Aquagait\\POB-410 submit\\owner-submit.csv")).readAll();
			System.out.println(data);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return data;
	}
}
