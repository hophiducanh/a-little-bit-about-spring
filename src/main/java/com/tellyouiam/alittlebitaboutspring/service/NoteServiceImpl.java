package com.tellyouiam.alittlebitaboutspring.service;

import com.tellyouiam.alittlebitaboutspring.utils.OnboardHelper;
import com.tellyouiam.alittlebitaboutspring.utils.StringHelper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class NoteServiceImpl implements NoteService {
	
	
	private int check(String[] arr, String... valuesToCheck) {
		int index;
		for (String element : arr) {
			for (String value : valuesToCheck) {
				String formattedElement = element.replace("\"", "").trim();
				if (formattedElement.equalsIgnoreCase(value)) {
					index = Arrays.asList(arr).indexOf(element);
					if (index != -1) {
						return index;
					}
				}
			}
		}
		return 100;
	}
	
	private List<String> getCsvDataWithHeader(MultipartFile multipart) throws IOException {
		InputStream is = multipart.getInputStream();
		BufferedReader br = new BufferedReader(new InputStreamReader(is));
		List<String> data = new ArrayList<>();
		String line;
		while ((line = br.readLine()) != null) {
			data.add(line);
		}
		return data;
	}
	
	private List<String> getCsvData(MultipartFile multipart) throws IOException {
		InputStream is = multipart.getInputStream();
		BufferedReader br = new BufferedReader(new InputStreamReader(is));
		return this.getCsvData(br, false);
	}
	
	private List<String> getCsvDataFromPath(String path, boolean ignoreHeader) throws IOException {
		try (
				FileReader fr = new FileReader(path);
				BufferedReader br = new BufferedReader(fr);
		) {
			List<String> data = new ArrayList<>();
			String line = null;
			int count = 0;
			while ((line = br.readLine()) != null) {
				count++;
				if (ignoreHeader && count == 1)
					continue;
				data.add(line);
			}
			return data;
		}
	}
	
	public List<String> getCsvData(BufferedReader bufReader) throws IOException {
		return getCsvData(bufReader, true);
	}
	
	private List<String> getCsvData(BufferedReader bufReader, boolean ignoreHeader) throws IOException {
		List<String> data = new ArrayList<>();
		String line = null;
		int count = 0;
		while ((line = bufReader.readLine()) != null) {
			count++;
			if (ignoreHeader && count == 1)
				continue;
			data.add(line);
		}
		return data;
	}
	
	private static boolean isValid(String dateStr) {
		Matcher dateMatcher = Pattern.compile(IS_INSTANCEOF_DATE).matcher(dateStr);
		if (dateMatcher.matches()) {
			return true;
		}
		return false;
	}
	
	public static String[][] readCSVTo2DArray(String path, boolean ignoreHeader) throws FileNotFoundException, IOException {
		try (FileReader fr = new FileReader(path);
		     BufferedReader br = new BufferedReader(fr)) {
			Collection<String[]> lines = new ArrayList<>();
			int count = 0;
			for (String line = br.readLine(); line != null; line = br.readLine()) {
				count++;
				if (ignoreHeader && count == 1) {
					continue;
				}
				lines.add(OnboardHelper.readCsvLine(line));
			}
			return lines.toArray(new String[lines.size()][]);
		}
	}
	
	private String readMobilePhoneNumber(String phone) {
		if (org.apache.commons.lang3.StringUtils.isEmpty(phone)) {
			return "";
		} else {
			phone = phone.replaceAll("[^\\d]+", "");
			if (phone.matches("^[0-9]{8,}.*")) {
				if (phone.matches("^[1-9]+.*")) {
					phone = String.format("0%s", phone);
				}
				return phone;
			} else {
				System.out.println("Invalid Phone Number: " + phone);
			}
			return "";
		}
	}
	
	
	@Override
	public Object automateImportOwner(MultipartFile ownerFile) {
		try {
			List<String> csvData = this.getCsvData(ownerFile);
			StringBuilder builder = new StringBuilder();
			if (!CollectionUtils.isEmpty(csvData)) {
				
				// ---------- common cols --------------------------------------
				// OWNER_KEY (ID or EMAIL), can leave blank if ID is EMAIL
				// EMAIL
				// FINANCE EMAIL
				// FIRST NAME
				// LAST NAME
				// DISPLAY NAME
				// TYPE
				// MOBILE
				// PHONE
				// FAX
				// ADDRESS
				// SUBURB (CITY)
				// STATE
				// POSTCODE
				// COUNRTY
				// GST = "true/false" or ot "T/F" or "Y/N"
				// -------------------------------------------------------------
				
				String[] header = OnboardHelper.readCsvLine(csvData.get(0));
				
				int ownerIdIndex = check(header, "OwnerID");
				int emailIndex = check(header, "Email");
				int financeEmailIndex = check(header, "FinanceEmail");
				int firstNameIndex = check(header, "FirstName", "First Name");
				int lastNameIndex = check(header, "LastName", "Last Name");
				int displayNameIndex = check(header, "DisplayName", "Name");
				int typeIndex = check(header, "Type");
				int mobileIndex = check(header, "Mobile", "Mobile Phone");
				int phoneIndex = check(header, "Phone");
				int faxIndex = check(header, "Fax");
				int addressIndex = check(header, "Address");
				int cityIndex = check(header, "City");
				int stateIndex = check(header, "State");
				int postCodeIndex = check(header, "PostCode");
				int countryIndex = check(header, "Country");
				int gstIndex = check(header, "GST");
				
				String rowHeader = String.format("%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s\n",
						"OwnerID", "Email", "FinanceEmail", "FirstName", "LastName", "DisplayName",
						"Type", "Mobile", "Phone", "Fax", "Address", "City", "State", "PostCode",
						"Country", "GST"
				);
				
				builder.append(rowHeader);
				
				csvData = csvData.stream().skip(1).collect(Collectors.toList());
				int count = 0;
				for (String line : csvData) {
					count++;
					String[] r = OnboardHelper.readCsvLine(line);
					
					String ownerId = OnboardHelper.readCsvRow(r, ownerIdIndex);
					String email = OnboardHelper.readCsvRow(r, emailIndex);
					String financeEmail = OnboardHelper.readCsvRow(r, financeEmailIndex);
					String firstName = OnboardHelper.readCsvRow(r, firstNameIndex);
					String lastName = OnboardHelper.readCsvRow(r, lastNameIndex);
					String displayName = OnboardHelper.readCsvRow(r, displayNameIndex);
					String type = OnboardHelper.readCsvRow(r, typeIndex);
					
					String mobile = readMobilePhoneNumber(OnboardHelper.readCsvRow(r, mobileIndex));
					
					String phone = readMobilePhoneNumber(OnboardHelper.readCsvRow(r, phoneIndex));
					
					
					String fax = OnboardHelper.readCsvRow(r, faxIndex);
					String address = OnboardHelper.readCsvRow(r, addressIndex);
					String city = OnboardHelper.readCsvRow(r, cityIndex);
					String state = OnboardHelper.readCsvRow(r, stateIndex);
					String postCode = OnboardHelper.getPostcode(OnboardHelper.readCsvRow(r, postCodeIndex));
					String country = OnboardHelper.readCsvRow(r, countryIndex);
					String gst = OnboardHelper.readCsvRow(r, gstIndex);
					
					String rowBuilder = String.format("%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s\n",
							StringHelper.csvValue(ownerId),
							StringHelper.csvValue(email),
							StringHelper.csvValue(financeEmail),
							StringHelper.csvValue(firstName),
							StringHelper.csvValue(lastName),
							StringHelper.csvValue(displayName),
							StringHelper.csvValue(type),
							StringHelper.csvValue(mobile),
							StringHelper.csvValue(phone),
							StringHelper.csvValue(fax),
							StringHelper.csvValue(address),
							StringHelper.csvValue(city),
							StringHelper.csvValue(state),
							StringHelper.csvValue(postCode),
							StringHelper.csvValue(country),
							StringHelper.csvValue(gst)
					);
					builder.append(rowBuilder);
				}
			}
			
			String path = "C:\\Users\\conta\\OneDrive\\Desktop\\data\\formatted-owner.csv";
			try {
				File file = new File(path);
//				file.setWritable(true);
//				file.setExecutable(true);
//				file.setReadable(true);
				
				FileOutputStream os = new FileOutputStream(file);
				os.write(builder.toString().getBytes());
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			return builder;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public Object automateImportHorse(MultipartFile horseFile) {
		try {
			List<String> csvData = this.getCsvData(horseFile);
			csvData = csvData.stream().filter(org.apache.commons.lang3.StringUtils::isNotEmpty).collect(Collectors.toList());
			StringBuilder builder = new StringBuilder();
			if (!CollectionUtils.isEmpty(csvData)) {
				
				// read from horse file first, standard columns order:
				// EXTERNAL ID, can leave blank if use hash code from name as id
				// NAME
				// FOALED
				// SIRE
				// DAM
				// COLOUR
				// SEX
				// AVATAR
				// ADDED DATE
				// STATUS (active/inactive)
				// CURRENT LOCATION
				// CURRENT STATUS
				// TYPE (Race Horse/ Stallion/ Speller/ Brood Mare/ Yearling)
				// CATEGORY
				// BONUS SCHEME
				// NICK NAME
				
				String[] header = OnboardHelper.readCsvLine(csvData.get(0));
				
				int externalIdIndex = check(header, "ExternalId");
				int nameIndex = check(header, "Horse Name", "Name");
				int foaledIndex = check(header, "DOB", "foaled");
				int sireIndex = check(header, "Sire");
				int damIndex = check(header, "Dam");
				int colorIndex = check(header, "Color");
				int sexIndex = check(header, "Gender", "Sex");
				int avatarIndex = check(header, "Avatar");
//				int addedDateIndex = check(header, "");
				int activeStatusIndex = check(header, "Active Status", "ActiveStatus");
				int horseLocationIndex = check(header, "Property");
				int horseStatusIndex = check(header, "Current Status", "CurrentStatus");
				int typeIndex = check(header, "Type");
				int categoryIndex = check(header, "Category");
				int bonusSchemeIndex = check(header, "Bonus Scheme", "BonusScheme", "Schemes");
				int nickNameIndex = check(header, "Nick Name", "NickName");
				
				int daysHereIndex = check(header, "Days Here", "Days");
				int weeksHereIndex = check(header, "Weeks Here", "Weeks");
				
				String rowHeader = String.format("%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s\n",
						"ExternalId", "Name", "Foaled", "Sire", "Dam", "Color",
						"Sex", "Avatar", "AddedDate", "ActiveStatus/Status",
						"CurrentLocation/HorseLocation", "CurrentStatus/HorseStatus",
						"Type", "Category", "BonusScheme", "NickName"
				);
				
				builder.append(rowHeader);
				
				csvData = csvData.stream().skip(1).collect(Collectors.toList());
				for (String line : csvData) {
					String[] r = OnboardHelper.readCsvLine(line);
					
					String externalId = OnboardHelper.readCsvRow(r, externalIdIndex);
					String name = OnboardHelper.readCsvRow(r, nameIndex);
					
					String foaled = OnboardHelper.readCsvRow(r, foaledIndex);
//					if(!isDMYformat) {
//						DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
//						formatter.setLenient(false);
//						try {
//							Date date= formatter.parse(foaled);
//						} catch (ParseException e) {
//							//If input date is in different format or invalid.
//						}
//					}
					
					String sire = OnboardHelper.readCsvRow(r, sireIndex);
					String dam = OnboardHelper.readCsvRow(r, damIndex);
					String color = OnboardHelper.readCsvRow(r, colorIndex);
					String sex = OnboardHelper.readCsvRow(r, sexIndex);
					
					String avatar = OnboardHelper.readCsvRow(r, avatarIndex);
					
					String dayHere = OnboardHelper.readCsvRow(r, daysHereIndex);
					String weekHere = OnboardHelper.readCsvRow(r, weeksHereIndex);
					
					String addedDate = "";
					if (StringUtils.isEmpty(dayHere) && StringUtils.isEmpty(weekHere)) {
						addedDate = "";
					}
					
					String activeStatus = OnboardHelper.readCsvRow(r, activeStatusIndex);
					
					String currentLocation = OnboardHelper.readCsvRow(r, horseLocationIndex);
					String currentStatus = OnboardHelper.readCsvRow(r, horseStatusIndex);
					String type = OnboardHelper.readCsvRow(r, typeIndex);
					String category = OnboardHelper.readCsvRow(r, categoryIndex);
					String bonusScheme = OnboardHelper.readCsvRow(r, bonusSchemeIndex);
					String nickName = OnboardHelper.readCsvRow(r, nickNameIndex);
					
					String rowBuilder = String.format("%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s\n",
							StringHelper.csvValue(externalId),
							StringHelper.csvValue(name),
							StringHelper.csvValue(foaled),
							StringHelper.csvValue(sire),
							StringHelper.csvValue(dam),
							StringHelper.csvValue(color),
							StringHelper.csvValue(sex),
							StringHelper.csvValue(avatar),
							StringHelper.csvValue(addedDate),
							StringHelper.csvValue(activeStatus),
							StringHelper.csvValue(currentLocation),
							StringHelper.csvValue(currentStatus),
							StringHelper.csvValue(type),
							StringHelper.csvValue(category),
							StringHelper.csvValue(bonusScheme),
							StringHelper.csvValue(nickName)
					);
					builder.append(rowBuilder);
				}
			}
			
			String path = "C:\\Users\\conta\\OneDrive\\Desktop\\data\\formatted-horse.csv";
			try {
				File file = new File(path);
//				file.setWritable(true);
//				file.setExecutable(true);
//				file.setReadable(true);
				
				FileOutputStream os = new FileOutputStream(file);
				os.write(builder.toString().getBytes());
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			return builder;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	@Override
	public Object automateImportOwnerShip(MultipartFile ownershipFile) {
		try {
			List<String> csvData = this.getCsvData(ownershipFile);
			StringBuilder builder = new StringBuilder();
			if (!CollectionUtils.isEmpty(csvData)) {
				
				// ---------- cols of file ownership ---------------------------
				// HORSE KEY (ID or NAME), can leave blank if key is horse name
				// HORSE NAME
				// OWNER_KEY (ID or EMAIL), can leave blank if ID is EMAIL
				// EMAIL
				// FINANCE EMAIL
				// FIRST NAME
				// LAST NAME
				// DISPLAY NAME
				// TYPE
				// MOBILE
				// PHONE
				// FAX
				// ADDRESS
				// SUBURB (CITY)
				// STATE
				// POSTCODE
				// COUNRTY
				// GST = "true/false" or ot "T/F" or "Y/N"
				// BALANCE (SHARE PERCENTAGE)
				// FROM_DATE
				// TO_DATE
				// -------------------------------------------------------------
				
				String[] header = OnboardHelper.readCsvLine(csvData.get(0));
				
				int horseIdIndex = check(header, "Horse Id");
				int horseNameIndex = check(header, "Horse Name", "Horse");
				int ownerIdIndex = check(header, "Owner Id");
				int commsEmailIndex = check(header, "CommsEmail", "Email");
				int financeEmailIndex = check(header, "Finance Email", "FinanceEmail");
				int firstNameIndex = check(header, "FirstName", "First Name");
				int lastNameIndex = check(header, "LastName", "Last Name");
				int displayNameIndex = check(header, "DisplayName", "Name", "Display Name");
				int typeIndex = check(header, "Type");
				int mobileIndex = check(header, "Mobile", "Mobile Phone");
				int phoneIndex = check(header, "Phone");
				int faxIndex = check(header, "Fax");
				int addressIndex = check(header, "Address");
				int cityIndex = check(header, "City");
				int stateIndex = check(header, "State");
				int postCodeIndex = check(header, "PostCode");
				int countryIndex = check(header, "Country");
				int gstIndex = check(header, "GST");
				int shareIndex = check(header, "Shares", "Share", "Ownership", "Share %");
				int addedDateIndex = check(header, "AddedDate", "Added Date");
				
				String rowHeader = String.format("%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s\n",
						"HorseId", "HorseName",
						"OwnerID", "CommsEmail", "FinanceEmail", "FirstName", "LastName", "DisplayName",
						"Type", "Mobile", "Phone", "Fax", "Address", "City", "State", "PostCode",
						"Country", "GST", "Share", "FromDate"
				);
				
				builder.append(rowHeader);
				
				csvData = csvData.stream().skip(1).collect(Collectors.toList());
				for (String line : csvData) {
					String[] r = OnboardHelper.readCsvLine(line);
					
					String horseId = OnboardHelper.readCsvRow(r, horseIdIndex);
					String horseName = OnboardHelper.readCsvRow(r, horseNameIndex);
					String ownerId = OnboardHelper.readCsvRow(r, ownerIdIndex);
					String commsEmail = OnboardHelper.readCsvRow(r, commsEmailIndex);
					String financeEmail = OnboardHelper.readCsvRow(r, financeEmailIndex);
					String firstName = OnboardHelper.readCsvRow(r, firstNameIndex);
					String lastName = OnboardHelper.readCsvRow(r, lastNameIndex);
					String displayName = OnboardHelper.readCsvRow(r, displayNameIndex);
					String type = OnboardHelper.readCsvRow(r, typeIndex);
					String mobile = readMobilePhoneNumber(OnboardHelper.readCsvRow(r, mobileIndex));
					String phone = readMobilePhoneNumber(OnboardHelper.readCsvRow(r, phoneIndex));
					String fax = OnboardHelper.readCsvRow(r, faxIndex);
					String address = OnboardHelper.readCsvRow(r, addressIndex);
					String city = OnboardHelper.readCsvRow(r, cityIndex);
					String state = OnboardHelper.readCsvRow(r, stateIndex);
					String postCode = OnboardHelper.getPostcode(OnboardHelper.readCsvRow(r, postCodeIndex));
					String country = OnboardHelper.readCsvRow(r, countryIndex);
					String gst = OnboardHelper.readCsvRow(r, gstIndex);
					String share = OnboardHelper.readCsvRow(r, shareIndex);
					String addedDate = OnboardHelper.readCsvRow(r, addedDateIndex);
					
					String rowBuilder = String.format("%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s\n",
							StringHelper.csvValue(horseId),
							StringHelper.csvValue(horseName),
							StringHelper.csvValue(ownerId),
							StringHelper.csvValue(commsEmail),
							StringHelper.csvValue(financeEmail),
							StringHelper.csvValue(firstName),
							StringHelper.csvValue(lastName),
							StringHelper.csvValue(displayName),
							StringHelper.csvValue(type),
							StringHelper.csvValue(mobile),
							StringHelper.csvValue(phone),
							StringHelper.csvValue(fax),
							StringHelper.csvValue(address),
							StringHelper.csvValue(city),
							StringHelper.csvValue(state),
							StringHelper.csvValue(postCode),
							StringHelper.csvValue(country),
							StringHelper.csvValue(gst),
							StringHelper.csvValue(share),
							StringHelper.csvValue(addedDate)
					);
					builder.append(rowBuilder);
				}
			}
			
			String path = "C:\\Users\\conta\\OneDrive\\Desktop\\data\\formatted-ownership.csv";
			try {
				File file = new File(path);
				
				FileOutputStream os = new FileOutputStream(file);
				os.write(builder.toString().getBytes());
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			return builder;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	private static final String REMOVE_BANK_LINES_PATTERN = "(?m)^[,]*$\n";
	private static final String REMOVE_LINE_BREAK_PATTERN = "\nCT";
	private static final String REMOVE_INVALID_SHARES_PATTERN = "Int.Party";
	private static final String CORRECT_HORSE_NAME_PATTERN = "(?m)^([^,].*)\\s\\(\\s.*";
	private static final String TRIM_HORSE_NAME_PATTERN = "(?m)^\\s";
	private static final String MOVE_HORSE_TO_CORRECT_LINE_PATTERN = "(?m)^([^,].*)\\n,(?=([\\d]{1,3})?(\\.)?([\\d]{1,2})?%)";
	private static final String REMOVE_UNNECESSARY_HEADER_FOOTER = "(?m)^(?!,Share).*(?<!(Y,|N,))$(\\n)?";
	private static final String IS_INSTANCEOF_DATE = "([0-9]{0,2}/[0-9]{0,2}/[0-9]{0,4})";
	
	@Override
	public Object prepareOwnership(MultipartFile ownershipFile) {
		try {
			List<String> csvData = this.getCsvData(ownershipFile);
			String allLines = String.join("\n", csvData);
			
			Matcher blankLinesMatcher = Pattern.compile(REMOVE_BANK_LINES_PATTERN).matcher(allLines);
			if (blankLinesMatcher.find()) {
				allLines = allLines.replaceAll(REMOVE_BANK_LINES_PATTERN, "");
			}
			
			Matcher linesBreakMatcher = Pattern.compile(REMOVE_LINE_BREAK_PATTERN).matcher(allLines);
			if (linesBreakMatcher.find()) {
				allLines = allLines.replaceAll(REMOVE_LINE_BREAK_PATTERN, " CT");
			}
			
			Matcher invalidSharesMatcher = Pattern.compile(REMOVE_INVALID_SHARES_PATTERN).matcher(allLines);
			if (invalidSharesMatcher.find()) {
				allLines = allLines.replaceAll(REMOVE_INVALID_SHARES_PATTERN, "0%");
			}
			
			Matcher correctHorseNameMatcher = Pattern.compile(CORRECT_HORSE_NAME_PATTERN).matcher(allLines);
			if (correctHorseNameMatcher.find()) {
				allLines = allLines.replaceAll(CORRECT_HORSE_NAME_PATTERN, "$1");
			}
			
			Matcher trimHorseNameMatcher = Pattern.compile(TRIM_HORSE_NAME_PATTERN).matcher(allLines);
			if (trimHorseNameMatcher.find()) {
				allLines = allLines.replaceAll(TRIM_HORSE_NAME_PATTERN, "");
			}
			
			Matcher correctHorseLinePattern = Pattern.compile(MOVE_HORSE_TO_CORRECT_LINE_PATTERN).matcher(allLines);
			if (correctHorseLinePattern.find()) {
				allLines = allLines.replaceAll(MOVE_HORSE_TO_CORRECT_LINE_PATTERN, "$1,");
			}
			
			Matcher removeUnnecessaryData = Pattern.compile(REMOVE_UNNECESSARY_HEADER_FOOTER).matcher(allLines);
			if (removeUnnecessaryData.find()) {
				allLines = allLines.replaceAll(REMOVE_UNNECESSARY_HEADER_FOOTER, "");
			}
			
			String path = "C:\\Users\\conta\\OneDrive\\Desktop\\data\\prepared-ownership.csv";
			
			FileOutputStream fos = null;
			try {
				File file = new File(path);
				fos = new FileOutputStream(file);
				fos.write(allLines.getBytes());
				fos.close();
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				try {
					if (fos != null)
						fos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			
			String[][] data = readCSVTo2DArray(path, false);
			
			List<Integer> rowHasValueIndex = new ArrayList<>();
			Set<Integer> setAllIndexes = new HashSet<>();
			Set<Integer> isEmptyIndexes = new HashSet<>();
			int dateIndex = -1;
			int gstIndex = -1;
			
			for (int i = 0; i < data.length; i++) {
				for (int j = 0; j < data[i].length; j++) {
					setAllIndexes.add(j);
					
					if (data[i][j].equalsIgnoreCase("")) {
						isEmptyIndexes.add(j);
					}
					
					//append date header
					if (isValid(data[i][j])) {
						dateIndex = j;
						data[0][dateIndex] = "Added Date";
					}
					
					if (data[i][j].equals("N") || data[i][j].equals("Y")) {
						gstIndex = j;
					}
				}
			}
			
			//Append Header
			StringBuilder gstString =  new StringBuilder();
			for (int i = 0; i < data.length; i++) {
				gstString.append(data[i][gstIndex]);
			}
			String distinctGST = gstString.toString().chars().distinct().mapToObj(c -> String.valueOf((char) c)).collect(Collectors.joining());
			if (distinctGST.matches("(YN)|(NY)")) {
				data[0][gstIndex] = "GST";
			}
			data[0][0] = "Horse";
			
			setAllIndexes.removeAll(isEmptyIndexes);
			
			for (Integer index : isEmptyIndexes) {
				StringBuilder isEmptyString = new StringBuilder();
				
				for (int l = 0; l < data.length; l++) {
					isEmptyString.append(data[l][index]);
				}
				
				if (!isEmptyString.toString().equals("")) {
					rowHasValueIndex.add(index);
				}
			}
			
			setAllIndexes.addAll(rowHasValueIndex);
			
			List<Integer> allIndexes = new ArrayList<>(setAllIndexes);
			
			try {
				StringBuilder arrayBuilder = new StringBuilder();
				for (int i = 0; i < data.length; i++) {
					for (int j = 0; j < data[i].length; j++) {
						arrayBuilder.append(data[i][j]);
						if (j < data.length - 1) {
							arrayBuilder.append(",");
						}
					}
					arrayBuilder.append("\n");//append new line at the end of the row
				}
				
				BufferedWriter writer = new BufferedWriter(new FileWriter(path));
				writer.write(arrayBuilder.toString());
				writer.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			//read with header
			List<String> csvDataWithBankColumns = this.getCsvDataFromPath(path, false);
			
			StringBuilder builder = new StringBuilder();
			for (String line : csvDataWithBankColumns) {
				String[] r = OnboardHelper.readCsvLine(line);
				
				StringBuilder rowBuilder = new StringBuilder();
				for (Integer index : allIndexes) {
					rowBuilder.append(r[index]).append(",");
				}
				rowBuilder.append("\n");
				builder.append(rowBuilder);
			}
			
			
			try {
				File file = new File(path);
				
				FileOutputStream os = new FileOutputStream(file);
				os.write(builder.toString().getBytes());
				os.flush();
				os.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			Path filePath = Paths.get(path);
			String name = "prepared-ownership";
			byte[] content = null;
			try {
				content = Files.readAllBytes(filePath);
			} catch (final IOException e) {
				e.printStackTrace();
			}
			MultipartFile ownershipMultipart = new MockMultipartFile(name, content);
			automateImportOwnerShip(ownershipMultipart);
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
}