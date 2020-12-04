package com.tellyouiam.alittlebitaboutspring.service.note.utils;

import com.tellyouiam.alittlebitaboutspring.exception.CustomException;
import com.tellyouiam.alittlebitaboutspring.utils.error.ErrorInfo;
import com.tellyouiam.alittlebitaboutspring.utils.string.OnboardHelper;
import com.tellyouiam.alittlebitaboutspring.utils.string.StringHelper;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

import static com.tellyouiam.alittlebitaboutspring.service.note.consts.NoteConst.AMERICAN_CUSTOM_DATE_FORMAT;
import static com.tellyouiam.alittlebitaboutspring.service.note.consts.NoteConst.AUSTRALIA_CUSTOM_DATE_FORMAT;
import static com.tellyouiam.alittlebitaboutspring.service.note.consts.NoteConst.CSV_HORSE_COUNT_PATTERN;
import static com.tellyouiam.alittlebitaboutspring.service.note.consts.NoteConst.UNIX_OUTPUT_FILE_PATH;
import static com.tellyouiam.alittlebitaboutspring.service.note.consts.NoteConst.WINDOW_OUTPUT_FILE_PATH;
import static com.tellyouiam.alittlebitaboutspring.utils.string.OnboardHelper.getCsvCellValueAtIndex;
import static com.tellyouiam.alittlebitaboutspring.utils.string.OnboardHelper.readCsvLine;
import static com.tellyouiam.alittlebitaboutspring.utils.string.StringHelper.isValidEmail;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.apache.commons.lang3.StringUtils.isNotEmpty;
import static org.apache.commons.lang3.StringUtils.upperCase;
import static org.springframework.util.CollectionUtils.isEmpty;

public class NoteHelper {
	
	private static final Logger logger = LoggerFactory.getLogger(NoteHelper.class);
	
	public static List<String> getCsvData(MultipartFile multipart) throws IOException {
		InputStream is = multipart.getInputStream();
		BufferedReader br = new BufferedReader(new InputStreamReader(is, UTF_8));
		return getCsvData(br, false);
	}
	
	public static List<String> getCsvData(BufferedReader bufReader, boolean ignoreHeader) throws IOException {
		List<String> data = new ArrayList<>();
		String line;
		int count = 0;
		while ((line = bufReader.readLine()) != null) {
			count++;
			if (ignoreHeader && count == 1)
				continue;
			data.add(line);
		}
		return data;
	}
	
	public static String[][] get2DArrayFromString(String value) {
		List<List<String>> nestedListData = Arrays.stream(value.split("\n"))
				.map(StringHelper::customSplitSpecific)
				.collect(toList());
		
		return nestedListData.stream()
				.map(l -> l.toArray(new String[0]))
				.toArray(String[][]::new);
	}
	
	public static List<String> getListFrom2DArrString(String[][] value) {
		List<String> result = new ArrayList<>();
		for (String[] strings : value) {
			String row = String.join(",", strings);
			result.add(row);
		}
		return result;
	}
	
	public static String[][] readCSVTo2DArray(String path, boolean ignoreHeader) throws FileNotFoundException, IOException {
		try (FileReader fr = new FileReader(path);
		     BufferedReader br = new BufferedReader(fr)) {
			Collection<String[]> lines = new ArrayList<>();
			int count = 0;
			for (String line = br.readLine(); line != null; line = br.readLine()) {
				if (line.length() > 0) {
					count++;
					if (ignoreHeader && count == 1) {
						continue;
					}
					
					lines.add(readCsvLine(line));
				}
			}
			return lines.toArray(new String[lines.size()][]);
		}
	}
	
	public static boolean stringContainsIgnoreCase(String container, String what, String delimiter) {
		return Stream.of(container.split(delimiter)).anyMatch(i -> i.trim().equalsIgnoreCase(what));
	}
	
	//https://stackoverflow.com/questions/86780/how-to-check-if-a-string-contains-another-string-in-a-case-insensitive-manner-in
	public static boolean containsIgnoreCase(String container, String what) {
		final int length = what.length();
		
		if (length == 0)
			return true; // Empty string is contained
		
		final char firstLo = Character.toLowerCase(what.charAt(0));
		final char firstUp = Character.toUpperCase(what.charAt(0));
		
		for (int i = container.length() - length; i >= 0; i--) {
			// Quick check before calling the more expensive regionMatches() method:
			final char ch = container.charAt(i);
			if (ch != firstLo && ch != firstUp)
				continue;
			
			//https://www.w3resource.com/java-tutorial/string/string_regionmatches.php
			if (container.regionMatches(true, i, what, 0, length))
				return true;
		}
		
		return false;
	}
	
	public static int checkColumnIndex(String[] arr, String... valuesToCheck) {
		int index;
		for (String element : arr) {
			for (String value : valuesToCheck) {
				String formattedElement = element.replace("\"", "").trim().toLowerCase();
				if (formattedElement.equalsIgnoreCase(value.toLowerCase())) {
					index = Arrays.asList(arr).indexOf(element);
					return index;
				}
			}
		}
		return -1;
	}
	
	public static boolean isRecognizedAsValidDate(String dateStr) {
		return isDMYFormat(dateStr) || isMDYFormat(dateStr);
	}
	
	public static boolean isDMYFormat(String date) {
		boolean isParsable = true;
		try {
			LocalDate.parse(date, AUSTRALIA_CUSTOM_DATE_FORMAT);
		} catch (DateTimeParseException e) {
			isParsable = false;
		}
		return isParsable;
	}
	
	public static boolean isMDYFormat(String date) {
		boolean isParsable = true;
		try {
			LocalDate.parse(date, AMERICAN_CUSTOM_DATE_FORMAT);
		} catch (DateTimeParseException e) {
			isParsable = false;
		}
		return isParsable;
	}
	
	public static boolean isAustraliaFormatV2(List<String> dates) {
		boolean isAustraliaFormat = false;
		
		// MM/DD/YYYY format
		List<String> mdyFormatList = new ArrayList<>();
		
		// DD/MM/YYYY format
		List<String> ausFormatList = new ArrayList<>();
		
		for (String rawDate : dates) {
			
			if (StringUtils.isEmpty(rawDate)) continue;
			
			if (!rawDate.matches("^\\d{1,2}/\\d{1,2}/\\d{1,4}$")) continue;
			
			if (isNotEmpty(rawDate)) {
				
				//Process for case: 15/08/2013 15:30
				String date = rawDate.split("\\p{Z}")[0];
				
				if (isDMYFormat(date)) {
					ausFormatList.add(date);
				} else if (isMDYFormat(date)) {
					mdyFormatList.add(date);
				} else {
					logger.info("UNKNOWN TYPE OF DATE");
				}
			}
		}
		
		// if file contains only one date like: 03/27/2019 >> MM/DD/YYYY format.
		// if all date value in the file have format like: D/M/YYYY format (E.g: 5/6/2020) >> recheck in racingAustralia.horse
		if (isEmpty(mdyFormatList) && !isEmpty(ausFormatList)) {
			isAustraliaFormat = true;
			logger.info("Type of DATE is DD/MM/YYY format **OR** M/D/Y format >>>>>>>>> Please check.");
			
		} else if (!isEmpty(mdyFormatList)) {
			logger.info("Type of DATE is MM/DD/YYY format");
			
		} else {
			logger.info("Type of DATE is UNDEFINED");
		}
		
		return isAustraliaFormat;
	}
	
	public static boolean isAustraliaFormat(List<String> csvData, int dateIndex, String fileType) {
		boolean isAustraliaFormat = false;
		
		// MM/DD/YYYY format
		List<String> mdyFormatList = new ArrayList<>();
		
		// DD/MM/YYYY format
		List<String> ausFormatList = new ArrayList<>();
		
		for (String line : csvData) {
			
			if (StringUtils.isEmpty(line)) continue;
			
			//ignore ,,,,,,,,,,,,,, line.
			if (line.matches("(?m)^([,]+)$")) continue;
			
			//ignore header.
			if (line.matches(CSV_HORSE_COUNT_PATTERN)) continue;
			
			String[] r = readCsvLine(line);
			String rawDateTime = getCsvCellValueAtIndex(r, dateIndex);
			
			if (isNotEmpty(rawDateTime)) {
				
				//Process for case: 15/08/2013 15:30
				String date = rawDateTime.split("\\p{Z}")[0];
				
				if (isDMYFormat(date)) {
					ausFormatList.add(date);
				} else if (isMDYFormat(date)) {
					mdyFormatList.add(date);
				} else {
					logger.info("UNKNOWN TYPE OF DATE IN {} FILE: {} at line : {}", upperCase(fileType), rawDateTime, line);
				}
			}
		}
		
		// if file contains only one date like: 03/27/2019 >> MM/DD/YYYY format.
		// if all date value in the file have format like: D/M/YYYY format (E.g: 5/6/2020) >> recheck in racingAustralia.horse
		if (isEmpty(mdyFormatList) && !isEmpty(ausFormatList)) {
			isAustraliaFormat = true;
//			logger.info("Type of DATE in {} file is DD/MM/YYY format **OR** M/D/Y format >>>>>>>>> Please check.", upperCase(fileType));
			
		} else if (!isEmpty(mdyFormatList)) {
			logger.info("Type of DATE in {} file is MM/DD/YYY format", upperCase(fileType));
			
		} else {
			logger.info("Type of DATE in {} file is UNDEFINED", upperCase(fileType));
		}
		
		return isAustraliaFormat;
	}
	
	public static String getValidEmailStr(String emailsStr, String line) throws CustomException {
		if (StringUtils.isEmpty(emailsStr)) return EMPTY;
		emailsStr = emailsStr.replaceAll(",", ";");
		String[] emailList = emailsStr.split(";");
		
		for (String email : emailList) {
			if (!isValidEmail(email.trim())) {
				logger.error("*********************Email is invalid: {} at line: {}. Please check!", email, line);
				throw new CustomException(new ErrorInfo("Invalid Email"));
			}
		}
		
		return emailsStr;
	}
	
	public static int getMaxRowLength(List<String> csvData) {
		String[][] data = csvData.stream().map(OnboardHelper::readCsvLine).toArray(String[][]::new);
		return Arrays.stream(data).max(Comparator.comparingInt(ArrayUtils::getLength))
				.orElseThrow(IllegalAccessError::new).length;
	}
	
	public static long countDistinctColElement(List<String> colValues) {
		return colValues.stream().distinct().count();
	}
	
	public static boolean isCsvColHasOnlyHeader(List<String> colValues) {
		return colValues.stream().distinct().count() == 2;
	}
	
	//distinct CSV col has data except header cell, empty cell
	public static boolean isCsvColHasRealData(List<String> colValues) {
		return colValues.stream().distinct().count() > 2;
	}
	
	public static String getOutputFolderPath() {
		String os = System.getProperty("os.name").toLowerCase();
		
		if (os.contains("win")) {
			return WINDOW_OUTPUT_FILE_PATH;
		} else if (os.contains("nix") || os.contains("nux") || os.contains("aix")) {
			return UNIX_OUTPUT_FILE_PATH;
		}
		return null;
	}
	
	public static String getOutputFolder(String dirName) {
		String initFolderPath = getOutputFolderPath();
		Path outputDirPath = Paths.get(requireNonNull(initFolderPath), dirName, "submit");
		
		Path path = null;
		boolean dirExists = Files.exists(outputDirPath);
		if (!dirExists) {
			try {
				path = Files.createDirectories(outputDirPath);
			} catch (IOException io) {
				logger.error("Error occur when create the folder at: {}", outputDirPath.toAbsolutePath().toString());
			}
		}
		return dirExists ? outputDirPath.toAbsolutePath().toString() : requireNonNull(path).toString();
	}
}
