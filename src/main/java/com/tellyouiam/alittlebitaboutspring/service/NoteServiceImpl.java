	package com.tellyouiam.alittlebitaboutspring.service;
	
	import com.tellyouiam.alittlebitaboutspring.utils.CollectionsHelper;
	import com.tellyouiam.alittlebitaboutspring.utils.CustomException;
	import com.tellyouiam.alittlebitaboutspring.utils.ErrorInfo;
	import com.tellyouiam.alittlebitaboutspring.utils.OnboardHelper;
	import com.tellyouiam.alittlebitaboutspring.utils.StringHelper;
	import org.apache.commons.lang3.StringUtils;
	import org.slf4j.Logger;
	import org.slf4j.LoggerFactory;
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
	import java.text.ParseException;
	import java.text.SimpleDateFormat;
	import java.time.LocalDate;
	import java.time.LocalDateTime;
	import java.time.ZoneId;
	import java.time.format.DateTimeFormatter;
	import java.time.format.DateTimeFormatterBuilder;
	import java.util.ArrayList;
	import java.util.Arrays;
	import java.util.Collection;
	import java.util.Date;
	import java.util.HashMap;
	import java.util.HashSet;
	import java.util.LinkedHashMap;
	import java.util.List;
	import java.util.Map;
	import java.util.Objects;
	import java.util.Set;
	import java.util.regex.Matcher;
	import java.util.regex.Pattern;
	import java.util.stream.Collector;
	import java.util.stream.Collectors;
	
	@Service
	public class NoteServiceImpl implements NoteService {
		
		private static final Logger logger = LoggerFactory.getLogger(NoteServiceImpl.class);
		
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
			//java7 >> try with statement
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
		
		private static boolean isRecognizedAsValidDate(String dateStr) {
			Matcher dateMatcher = Pattern.compile(IS_INSTANCEOF_DATE_PATTERN).matcher(dateStr);
			return dateMatcher.matches();
		}
		
		private static String[][] readCSVTo2DArray(String path, boolean ignoreHeader) throws FileNotFoundException, IOException {
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
						
						lines.add(OnboardHelper.readCsvLine(line));
					}
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
					return "";
				}
			}
		}
		
		private String getOutputFolder(String dirName) {
			String initFolderPath = getOutputFolderPath();
			Path outputDirPath = Paths.get(Objects.requireNonNull(initFolderPath), dirName, "submit");
			
			Path path = null;
			boolean dirExists = Files.exists(outputDirPath);
			if (!dirExists) {
				try {
					path = Files.createDirectories(outputDirPath);
				} catch (IOException io) {
					logger.error("Error occur when create the folder at: {}", outputDirPath.toAbsolutePath().toString());
				}
			}
			return dirExists ? outputDirPath.toAbsolutePath().toString() : Objects.requireNonNull(path).toString();
		}
		
		private static final String HORSE_RECORDS_PATTERN = "([\\d]+)\\sRecords"; //like: 162 records
		@Override
		public Object automateImportOwner(MultipartFile ownerFile, String dirName) throws CustomException {
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
					
					String allLines = String.join("", csvData);
					Matcher recordsMatcher = Pattern.compile(HORSE_RECORDS_PATTERN).matcher(allLines);
					int horseRecords = 0;
					if (recordsMatcher.find()) {
						horseRecords = Integer.parseInt(recordsMatcher.group(1));
					}
					
					int matcherCount = 0;
					while (recordsMatcher.find()) {
						matcherCount++;
					}
					if (matcherCount > 1) {
						throw new CustomException(new ErrorInfo("CSV data seem pretty weird. Please check!"));
					}
					csvData = csvData.stream().skip(1).collect(Collectors.toList());
					int count = 0;
					for (String line : csvData) {
						if (StringUtils.isEmpty(line)) continue;
						
						String[] r = OnboardHelper.readCsvLine(line);
						
						//rows will be ignored like:
						//,,,,
						//162 Records,,,,
						
						StringBuilder ignoreRowBuilder = new StringBuilder();
						for (String s : r) {
							ignoreRowBuilder.append(s);
						}
						if (StringUtils.isEmpty(ignoreRowBuilder.toString())) continue;
						
						if (StringUtils.isEmpty(ignoreRowBuilder.toString().replaceAll(HORSE_RECORDS_PATTERN, ""))) {
							logger.info("Ignored Horse Records Line: {}", ignoreRowBuilder.toString());
							continue;
						}
						
						count++;
						
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
					
					if (horseRecords != count) {
						logger.info("Data records found: {}, Data records count: {}", horseRecords, count);
						throw new CustomException(new ErrorInfo("Data records doesn't match!"));
					}
				}
				
				String path = getOutputFolder(dirName) + File.separator + "formatted-owner.csv";
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
		
		public Object importHorseFromMiStable(MultipartFile horseFile, String dirName) {
			
			try {
				String path = getOutputFolder(dirName) + "formatted-horse.csv";
				
				List<String> csvData = this.getCsvData(horseFile);
				csvData = csvData.stream().filter(org.apache.commons.lang3.StringUtils::isNotEmpty).collect(Collectors.toList());
				StringBuilder builder = new StringBuilder();
				
				StringBuilder addedDateBuilder = new StringBuilder();
				StringBuilder activeStatusBuilder = new StringBuilder();
				StringBuilder currentLocationBuilder = new StringBuilder();
				
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
					int nameIndex = check(header, "Horse Name", "Name", "Horse");
					int foaledIndex = check(header, "DOB", "foaled");
					int sireIndex = check(header, "Sire");
					int damIndex = check(header, "Dam");
					int colorIndex = check(header, "Color");
					int sexIndex = check(header, "Gender", "Sex");
					int avatarIndex = check(header, "Avatar");
					int addedDateIndex = check(header, "AddedDate");
					int activeStatusIndex = check(header, "Active Status", "ActiveStatus");
					int horseLocationIndex = check(header, "Property");
					int horseStatusIndex = check(header, "Current Status", "CurrentStatus");
					int typeIndex = check(header, "Type");
					int categoryIndex = check(header, "Category");
					int bonusSchemeIndex = check(header, "Bonus Scheme", "BonusScheme", "Schemes");
					int nickNameIndex = check(header, "Nick Name", "NickName");
					
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
						
						String rawFoaled = OnboardHelper.readCsvRow(r, foaledIndex);
						String foaled = StringUtils.EMPTY;
						
						if (StringUtils.isNotEmpty(rawFoaled)) {
							
							if (rawFoaled.matches(IS_DATE_MONTH_YEAR_FORMAT_PATTERN)) {
								foaled = rawFoaled;
							} else if (rawFoaled.matches(IS_MONTH_DATE_YEAR_FORMAT_PATTERN)) {
								DateTimeFormatter expectedFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
								DateTimeFormatter rawFormatter = new DateTimeFormatterBuilder()
										.appendOptional(DateTimeFormatter.ofPattern("MM/dd/yyyy"))
										.appendOptional(DateTimeFormatter.ofPattern("M/dd/yyyy"))
										.appendOptional(DateTimeFormatter.ofPattern("MM/d/yyyy"))
										.appendOptional(DateTimeFormatter.ofPattern("M/d/yyyy"))
										.toFormatter();
								
								foaled = LocalDate.parse(rawFoaled, rawFormatter).format(expectedFormatter);
							} else {
								logger.info("UNKNOWN TYPE OF DATE: {} in line : {}", foaled, line);
							}
						}
						
						String sire = OnboardHelper.readCsvRow(r, sireIndex);
						String dam = OnboardHelper.readCsvRow(r, damIndex);
						String color = OnboardHelper.readCsvRow(r, colorIndex);
						String sex = OnboardHelper.readCsvRow(r, sexIndex);
						
						String avatar = OnboardHelper.readCsvRow(r, avatarIndex);
						
						String addedDate = OnboardHelper.readCsvRow(r, addedDateIndex);
						addedDateBuilder.append(addedDate);
						
						String activeStatus = OnboardHelper.readCsvRow(r, activeStatusIndex);
						addedDateBuilder.append(activeStatus);
						
						String currentLocation = OnboardHelper.readCsvRow(r, horseLocationIndex);
						addedDateBuilder.append(currentLocation);
						
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
					
					//Address case addedDate, activeStatus and current location in horse file are empty.
					// We will face an error if we keep this data intact.
					if(StringUtils.isAllEmpty(addedDateBuilder, activeStatusBuilder, currentLocationBuilder)) {
						logger.warn("All of AddedDate && ActiveStatus && CurrentLocation can't be empty. At least addedDate required.");
						
						List<String> formattedData = StringHelper.convertStringBuilderToList(builder);
						StringBuilder dataBuilder = new StringBuilder();
						
						String currentDate = LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
						if (!CollectionUtils.isEmpty(formattedData)) {
							String[] formattedHeader = OnboardHelper.readCsvLine(formattedData.get(0));
							
							//Get addedDate index from header
							int addedDateOrdinal = check(formattedHeader, "AddedDate");
							
							//Append a header at first line of StringBuilder data to write to file.
							dataBuilder.append(formattedData.get(0)).append("\n");
							
							//process data ignore header
							for (String line : formattedData.stream().skip(1).collect(Collectors.toList())) {
								
								String[] row = OnboardHelper.readCsvLine(line);
								
								for (int i = 0; i < row.length; i++) {
									
									//replace empty addedDate with current date.
									if(i == addedDateOrdinal) {
										row[addedDateOrdinal] = currentDate;
										dataBuilder.append(row[i]).append(",");
										continue;
									}
									dataBuilder.append(row[i]).append(",");
								}
								dataBuilder.append("\n");
							}
						}
						
						if (dataBuilder.toString().contains(currentDate)) {
							logger.info("******************** Successfully generated addedDate with dd/MM/yyyy format : {}", currentDate);
						} else {
							logger.error("******************** Error created when trying to attach generated addedDate to output file.");
						}
						
						try {
							File file = new File(path);
							
							FileOutputStream os = new FileOutputStream(file);
							os.write(dataBuilder.toString().getBytes());
							os.flush();
							os.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
						return dataBuilder;
					}
				}
				
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
	
		@SuppressWarnings("unchecked")
		public Object automateImportHorse(MultipartFile horseFile, MultipartFile ownershipFile, String dirName) throws CustomException {
			if(Objects.isNull(ownershipFile)) {
				return this.importHorseFromMiStable(horseFile, dirName);
			}
			
			Map<String, Object> ownerShipResult = (Map<String, Object>) this.prepareOwnership(ownershipFile, dirName);
			Map<Object, Object> result = new HashMap<>();
			
			String exportedDateStr = String.valueOf(ownerShipResult.get("ExportedDate"));
			Date exportedDate = null;
			SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
			try {
				exportedDate = formatter.parse(exportedDateStr);
			} catch (ParseException e) {
				e.printStackTrace();
			}
			
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
					int nameIndex = check(header, "Horse Name", "Name", "Horse");
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
					
					String rowHeader = String.format("%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s\n",
							"ExternalId", "Name", "Foaled", "Sire", "Dam", "Color",
							"Sex", "Avatar", "AddedDate", "ActiveStatus/Status",
							"CurrentLocation/HorseLocation", "CurrentStatus/HorseStatus",
							"Type", "Category", "BonusScheme", "NickName"
					);
					
					builder.append(rowHeader);
					
					csvData = csvData.stream().skip(1).collect(Collectors.toList());
					
					Map<String, String> horseMap = new LinkedHashMap<>();
					Map<String, String> horseOwnershipMap = (Map<String, String>) ownerShipResult.get("HorseDataMap");
					
					for (String line : csvData) {
						
						if (StringUtils.isEmpty(line)) {
							logger.info("***************************Empty CSV Data Line: {}", line);
							continue;
						}
						String[] r = OnboardHelper.readCsvLine(line);
						
						String externalId = OnboardHelper.readCsvRow(r, externalIdIndex);
						String name = OnboardHelper.readCsvRow(r, nameIndex);
						
						if (StringUtils.isEmpty(name)) {
							logger.info("**************************Empty Horse Name: {}", name);
							continue;
						}
						
						String rawFoaled = OnboardHelper.readCsvRow(r, foaledIndex);
						String foaled = StringUtils.EMPTY;
						
						if (StringUtils.isNotEmpty(rawFoaled)) {
							
							if (rawFoaled.matches(IS_DATE_MONTH_YEAR_FORMAT_PATTERN)) {
								foaled = rawFoaled;
							} else if (rawFoaled.matches(IS_MONTH_DATE_YEAR_FORMAT_PATTERN)) {
								DateTimeFormatter expectedFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
								DateTimeFormatter rawFormatter = new DateTimeFormatterBuilder()
										.appendOptional(DateTimeFormatter.ofPattern("MM/dd/yyyy"))
										.appendOptional(DateTimeFormatter.ofPattern("M/dd/yyyy"))
										.appendOptional(DateTimeFormatter.ofPattern("MM/d/yyyy"))
										.appendOptional(DateTimeFormatter.ofPattern("M/d/yyyy"))
										.toFormatter();
								
								foaled = LocalDate.parse(rawFoaled, rawFormatter).format(expectedFormatter);
							} else {
								logger.info("UNKNOWN TYPE OF DATE: {} in line : {}", foaled, line);
							}
						}
						
						String sire = OnboardHelper.readCsvRow(r, sireIndex);
						String dam = OnboardHelper.readCsvRow(r, damIndex);
						
						if (StringUtils.isEmpty(name) && StringUtils.isEmpty(sire) && StringUtils.isEmpty(dam)
								&& StringUtils.isEmpty(rawFoaled)) continue;
						
						String color = OnboardHelper.readCsvRow(r, colorIndex);
						String sex = OnboardHelper.readCsvRow(r, sexIndex);
						String avatar = OnboardHelper.readCsvRow(r, avatarIndex);
						
						String dayHere = OnboardHelper.readCsvRow(r, daysHereIndex);
						
						long minusDays = StringUtils.isNotEmpty(dayHere) ? Long.parseLong(dayHere) : 0;
						LocalDateTime exportedLocalDate = LocalDateTime.ofInstant(Objects.requireNonNull(exportedDate).toInstant(), ZoneId.systemDefault());
						LocalDateTime dateAtFirstLocation = exportedLocalDate.minusDays(minusDays);
						Date addedDate = Date.from(dateAtFirstLocation.atZone(ZoneId.systemDefault()).toInstant());
						
						SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
						String addedDateStr = dateFormat.format(addedDate);
						
						if (!addedDateStr.matches(IS_DATE_MONTH_YEAR_FORMAT_PATTERN)) {
							logger.info("UNKNOWN TYPE OF DATE: {} in line: {}", addedDateStr, addedDateStr);
						}
						
						horseMap.put(name, addedDateStr);
						
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
								StringHelper.csvValue(addedDateStr),
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
					
					//compare horse data from horse file and ownerShip file to make sure horse data are exact or not.
					Map<String, String> fromHorseFile = CollectionsHelper.getDiffMap(horseMap, horseOwnershipMap, false);
					Set<String> keyHorse = fromHorseFile.keySet();
					Map<String, String> fromOwnerShipFile = horseOwnershipMap.entrySet().stream()
							.filter(x -> keyHorse.contains(x.getKey()))
							.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
					
					result.put("Diff From Horse File", fromHorseFile);
					result.put("Diff From OwnerShip File", fromOwnerShipFile);
				}
				
				String path = getOutputFolder(dirName) + File.separator + "formatted-horse.csv";
				try {
					File file = new File(path);
					FileOutputStream os = new FileOutputStream(file);
					os.write(builder.toString().getBytes());
				} catch (IOException e) {
					e.printStackTrace();
				}
				
			} catch (IOException e) {
				e.printStackTrace();
			}
			return result;
		}
		
		private static <T> Collector<T, ?, T> toSingleton() throws CustomException {
			try {
				return Collectors.collectingAndThen(
						Collectors.toList(),
						list -> {
					if (list.size() != 1) {
						throw new IllegalStateException();
					}
					return list.get(0);
				}
				);
			} catch (RuntimeException e) {
				if (e.getCause() instanceof IllegalStateException) {
					throw new CustomException(
							new ErrorInfo("Can't detect owner file name. CSV data seemingly a little weird. Please check!"));
				}
				throw e;
			}
		}
		
		private static String getOutputFolderPath() {
			String os = System.getProperty("os.name").toLowerCase();
			
			if (os.contains("win")) {
				return WINDOW_OUTPUT_FILE_PATH;
			} else if (os.contains("nix") || os.contains("nux") || os.contains("aix")) {
				return UNIX_OUTPUT_FILE_PATH;
			}
			return null;
		}
		
		private static final String REMOVE_BANK_LINES_PATTERN = "(?m)^[,]*$\n";
		private static final String REMOVE_LINE_BREAK_PATTERN = "\nCT";
		private static final String REMOVE_INVALID_SHARES_PATTERN = "Int.Party";
		private static final String CORRECT_HORSE_NAME_PATTERN = "(?m)^([^,].*)\\s\\(\\s.*";
		private static final String CORRECT_SHARE_COLUMN_POSITION_PATTERN = "(?m)^,(([\\d]{1,3})(\\.)([\\d]{1,2})%)";
		private static final String SHARE_COLUMN_POSITION_TRYING_PATTERN = "(?m)^(([\\d]{1,3})(\\.)([\\d]{1,2})%)";
		private static final String TRIM_HORSE_NAME_PATTERN = "(?m)^\\s";
		private static final String MOVE_HORSE_TO_CORRECT_LINE_PATTERN = "(?m)^([^,].*)\\n,(?=([\\d]{1,3})?(\\.)?([\\d]{1,2})?%)";
		private static final String REMOVE_UNNECESSARY_HEADER_FOOTER_PATTERN = "(?m)^(?!,Share)(.*)((?<!([YN]))(?<!(Y,|N,)))$\\n";
		private static final String IS_INSTANCEOF_DATE_PATTERN = "([0-9]{0,2}([/\\-.])[0-9]{0,2}([/\\-.])[0-9]{0,4})";
		private static final String EXTRACT_DEPARTED_DATE_OF_HORSE_PATTERN =
				"(?m)^([^,].*)\\s\\(\\s.*([\\s]+)([0-9]{0,2}([/\\-.])[0-9]{0,2}([/\\-.])[0-9]{0,4})";
		private static final String EXTRACT_OWNERSHIP_EXPORTED_DATE_PATTERN =
				"(?m)(Printed[:\\s]+)([0-9]{0,2}([/\\-.])[0-9]{0,2}([/\\-.])[0-9]{0,4})";
		
		private static final String IS_DATE_MONTH_YEAR_FORMAT_PATTERN = "^(?:(?:31([/\\-.])(?:0?[13578]|1[02]))\\1|" +
				"(?:(?:29|30)([/\\-.])(?:0?[13-9]|1[0-2])\\2))(?:(?:1[6-9]|[2-9]\\d)?\\d{2})$|" +
				"^(?:29([/\\-.])0?2\\3(?:(?:1[6-9]|[2-9]\\d)?(?:0[48]|[2468][048]|[13579][26])|(?:(?:16|[2468][048]|[3579][26])00)))$|" +
				"^(?:0?[1-9]|1\\d|2[0-8])([/\\-.])(?:(?:0?[1-9])|(?:1[0-2]))\\4(?:(?:1[6-9]|[2-9]\\d)?\\d{2})$";
		
		private static final String IS_MONTH_DATE_YEAR_FORMAT_PATTERN = "^(?:(?:(?:0?[13578]|1[02])([/\\-.])31)\\1|" +
				"(?:(?:0?[13-9]|1[0-2])([/\\-.])(?:29|30)\\2))(?:(?:1[6-9]|[2-9]\\d)?\\d{2})$|" +
				"^(?:0?2([/\\-.])29\\3(?:(?:1[6-9]|[2-9]\\d)?(?:0[48]|[2468][048]|[13579][26])|(?:(?:16|[2468][048]|[3579][26])00)))$|" +
				"^(?:(?:0?[1-9])|(?:1[0-2]))([/\\-.])(?:0?[1-9]|1\\d|2[0-8])\\4(?:(?:1[6-9]|[2-9]\\d)?\\d{2})$";
		
		private static final String MIXING_COMMS_FINANCE_EMAIL_PATTERN = "\"?Accs:\\s((.+) (?=(Comms)))Comms:\\s((.+)(\\.[a-zA-Z;]+)(?=,))\"?";
		
		private static final String FILE_BEGINNING_PATTERN = "(?m)^(,Share %)";
		private static final String FILE_BEGINNING_TRYING_PATTERN = "(?m)^(Share %)";
		private static final String EXTRACT_FILE_OWNER_NAME_PATTERN = "(?m)^(Horses)(.*)$(?=\\n)";
		private static final int IGNORED_NON_DATA_LINE_THRESHOLD = 6;
		
		private static final String REMOVE_BLANK_FOOTER_PATTERN = "(?m)^[,]*$";
		
		private static final String WINDOW_OUTPUT_FILE_PATH = "C:\\Users\\conta\\OneDrive\\Desktop\\data\\";
		private static final String UNIX_OUTPUT_FILE_PATH = "/home/logbasex/Desktop/data/";
		private static final String CT_IN_DISPLAY_NAME_PATTERN = "CT:";
		
		@Override
		public Object prepareOwnership(MultipartFile ownershipFile, String dirName) throws CustomException {
			Map<String, Object> result = new HashMap<>();
			
			try {
				List<String> csvData = this.getCsvData(ownershipFile);
				String allLines = String.join("\n", csvData);
				
				//get file exportedDate.
				// Pattern : ,,,,,,,,,,,,,,Printed: 21/10/2019  3:41:46PM,,,,Page -1 of 1,,,,
				String exportedDate = null;
				Pattern exportedDatePattern = Pattern.compile(EXTRACT_OWNERSHIP_EXPORTED_DATE_PATTERN, Pattern.CASE_INSENSITIVE);
				Matcher exportedDateMatcher = exportedDatePattern.matcher(allLines);
				
				int exportedDateCount = 0;
				
				while (exportedDateMatcher.find()) {
					exportedDateCount++;
				}
				if (exportedDateCount > 1) throw new CustomException(new ErrorInfo("CSV data seems a little weird. Please check!"));
				
				 // find() method starts at the beginning of this matcher's region, or, if
				 // a previous invocation of the method was successful and the matcher has
				 // not since been reset, at the first character not matched by the previous
				 // match.
				 //
				exportedDateMatcher.reset();
				
				String ignoredDataFooter = null;
				if (exportedDateMatcher.find()) {
					//get date use group(2) of regex.
					exportedDate = exportedDateMatcher.group(2).trim();
					if (!exportedDate.matches(IS_DATE_MONTH_YEAR_FORMAT_PATTERN)) {
						throw new CustomException(new ErrorInfo("The exported date was not recognized as a valid Australia format: {}", exportedDate));
					}
					
					int startedIndex = exportedDateMatcher.start();
					int commaLastIndex = allLines.lastIndexOf(",");
					ignoredDataFooter = allLines.substring(startedIndex, commaLastIndex);
					logger.info("*************************Ignored data in footer file: {}", ignoredDataFooter);
					
					allLines = allLines.substring(0, startedIndex);
					
				} else {
					throw new CustomException(new ErrorInfo("Can't find exported date. Please check or add exported date!"));
				}
				
				// Line has departedDate likely to extract:
				//Azurite (IRE) ( Azamour (IRE) - High Lite (GB)) 9yo Bay Gelding     Michael Hickmott Bloodstock - In
				//training Michael Hickmott Bloodstock 1/08/2019 >> 1/08/2019
				//This is required for make sure horse data after format csv are exact.
				
				Matcher departedDateMatcher = Pattern.compile(EXTRACT_DEPARTED_DATE_OF_HORSE_PATTERN).matcher(allLines);
				
				Map<String, String> horseDataMap = new LinkedHashMap<>();
				List<String> horseDataList = new ArrayList<>();
				
				while (departedDateMatcher.find()) {
					String horseName = departedDateMatcher.group(1).trim();
					String horseDepartedDate = departedDateMatcher.group(3).trim();
					
					if(StringUtils.isEmpty(horseName)) {
						continue;
					}
					
					if(StringUtils.isEmpty(horseDepartedDate)) {
						logger.info("Horse without departed date: {}", horseName);
					}
					
					if (!horseDepartedDate.matches(IS_DATE_MONTH_YEAR_FORMAT_PATTERN)) {
						throw new CustomException(new ErrorInfo("The departed date was not recognized as a valid Australia format: {}", horseDepartedDate));
					}
					
					DateTimeFormatter expectedFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
					DateTimeFormatter formatter = new DateTimeFormatterBuilder()
							.appendOptional(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
							.appendOptional(DateTimeFormatter.ofPattern("dd/M/yyyy"))
							.appendOptional(DateTimeFormatter.ofPattern("d/MM/yyyy"))
							.appendOptional(DateTimeFormatter.ofPattern("d/M/yyyy"))
							.toFormatter();
					
					String horseDate = LocalDate.parse(horseDepartedDate, formatter).format(expectedFormatter);
					horseDataMap.put(horseName, horseDate);
					horseDataList.add(horseName + ": " + horseDepartedDate);
				}
				
				result.put("HorseDataMap", horseDataMap);
				result.put("HorseDataList", horseDataList);
				result.put("ExportedDate", exportedDate);
				Matcher blankLinesMatcher = Pattern.compile(REMOVE_BANK_LINES_PATTERN).matcher(allLines);
				if (blankLinesMatcher.find()) {
					allLines = allLines.replaceAll(REMOVE_BANK_LINES_PATTERN, StringUtils.EMPTY);
				} else {
					throw new CustomException(ErrorInfo.CANNOT_FORMAT_OWNERSHIP_FILE_USING_REGEX_ERROR);
				}
				
				//optional
				String lineHasFileOwnerName;
				Matcher extractFileOwnerName = Pattern.compile(EXTRACT_FILE_OWNER_NAME_PATTERN).matcher(allLines);
				if (extractFileOwnerName.find()) {
					
					logger.info("*******************Lines possible have owner file name:\n {}", extractFileOwnerName.group());
					
					lineHasFileOwnerName = extractFileOwnerName.group(2);
					
					if(StringUtils.isEmpty(lineHasFileOwnerName)) {
						List<String> lineHasFileOwnerNameElements = Arrays.asList(OnboardHelper.readCsvLine(lineHasFileOwnerName));
						
						String fileOwnerName = lineHasFileOwnerNameElements.stream().filter(StringUtils::isNotEmpty)
								.collect(toSingleton());
						
						logger.info("*********************File owner name is : {}", fileOwnerName);
					}
					
				} else {
					logger.info("*******************Can't detect lines contain owner file name in given file.");
				}
				
				
				Matcher linesBreakMatcher = Pattern.compile(REMOVE_LINE_BREAK_PATTERN).matcher(allLines);
				if (linesBreakMatcher.find()) {
					allLines = allLines.replaceAll(REMOVE_LINE_BREAK_PATTERN, " CT");
				} else {
					logger.warn("Cannot apply regex: {}... for ownership file", REMOVE_LINE_BREAK_PATTERN);
				}
				
				//optional
				Matcher invalidSharesMatcher = Pattern.compile(REMOVE_INVALID_SHARES_PATTERN, Pattern.CASE_INSENSITIVE).matcher(allLines);
				if (invalidSharesMatcher.find()) {
					allLines = allLines.replaceAll(REMOVE_INVALID_SHARES_PATTERN, "0.00%");
				} else {
					logger.warn("Cannot apply regex: {}... for ownership file", REMOVE_INVALID_SHARES_PATTERN);
				}
				
				Matcher correctHorseNameMatcher = Pattern.compile(CORRECT_HORSE_NAME_PATTERN).matcher(allLines);
				int horseCount = 0;
				while (correctHorseNameMatcher.find()) {
					horseCount++;
				}
				result.put("HorseCount", horseCount);
				
				//ignore extra data from line contains horse name >> horse name
				//Ambidexter/Elancer 16 ( Ambidexter - Elancer) 3yo Brown Colt     Michael Hickmott Bloodstock - In training Michael Hickmott Bloodstock 24/12/2019 >> Ambidexter/Elancer 16
				
				Matcher correctShareColumnPosition = Pattern.compile(CORRECT_SHARE_COLUMN_POSITION_PATTERN).matcher(allLines);
				Matcher shareColumnPositionTrying = Pattern.compile(SHARE_COLUMN_POSITION_TRYING_PATTERN).matcher(allLines);
				if (horseCount > 0) {
					//special case: POB-345: Archer park (missing leading comma: normal case these lines don't contain horse name start with ,%share, >> POB-345 start with %share,)
					if (correctShareColumnPosition.find()) {
						allLines = allLines.replaceAll(CORRECT_HORSE_NAME_PATTERN, "$1");
					} else if (shareColumnPositionTrying.find()) {
						allLines = allLines.replaceAll(CORRECT_HORSE_NAME_PATTERN, "$1").replaceAll(SHARE_COLUMN_POSITION_TRYING_PATTERN, ",$1");
					} else {
						logger.warn("Data seemingly weird. Please check!");
					}
				} else {
					throw new CustomException(ErrorInfo.CANNOT_FORMAT_OWNERSHIP_FILE_USING_REGEX_ERROR);
				}
				
				Matcher trimHorseNameMatcher = Pattern.compile(TRIM_HORSE_NAME_PATTERN).matcher(allLines);
				if (trimHorseNameMatcher.find()) {
					allLines = allLines.replaceAll(TRIM_HORSE_NAME_PATTERN, "");
				} else {
					logger.info("Cannot apply regex: {}... for ownership file", TRIM_HORSE_NAME_PATTERN);
				}
				
				//Bring horse name and horse data into the same line.
				Matcher correctHorseLinePattern = Pattern.compile(MOVE_HORSE_TO_CORRECT_LINE_PATTERN).matcher(allLines);
				if (correctHorseLinePattern.find()) {
					allLines = allLines.replaceAll(MOVE_HORSE_TO_CORRECT_LINE_PATTERN, "$1,");
				} else {
					throw new CustomException(ErrorInfo.CANNOT_FORMAT_OWNERSHIP_FILE_USING_REGEX_ERROR);
				}
				
				//remove unnecessary line like:
				// ,,With Share Ownership Information ,,,,,,,,,,,,,,,,,,,,
				String ignoredDataHeader = null;
				Matcher beginningFileMatcher = Pattern.compile(FILE_BEGINNING_PATTERN).matcher(allLines);
				Matcher fileBeginningTryingMatcher = Pattern.compile(FILE_BEGINNING_TRYING_PATTERN).matcher(allLines);
				if (beginningFileMatcher.find()) {
					int startedIndex = beginningFileMatcher.start();
					ignoredDataHeader = allLines.substring(0, startedIndex);
					allLines = allLines.substring(startedIndex);
					
				} else if (fileBeginningTryingMatcher.find()) {
					allLines = allLines.replaceAll(FILE_BEGINNING_TRYING_PATTERN, ",$1");
					int startedIndex = fileBeginningTryingMatcher.start();
					ignoredDataHeader = allLines.substring(0, startedIndex);
					allLines = allLines.substring(startedIndex);
					
				} else {
					throw new CustomException(new ErrorInfo("File start with an incorrect prefix! Please check!"));
				}
				
				String ignoredData = ignoredDataHeader + ignoredDataFooter;
				int ignoredDataLines = StringUtils.countMatches(ignoredData, "\n");
				
				logger.info("******************************IGNORED DATA**********************************\n {}", ignoredData);
				//normally unnecessary lines to ignored between 5 and 10.;
				if (ignoredDataLines > IGNORED_NON_DATA_LINE_THRESHOLD) {
					throw new CustomException(new ErrorInfo("CSV data seems a little weird. Please check!"));
				}

				//last blank header can caused ArrayIndexOutOfBoundsException if you don't remove these lines.
				// E.g: line[23] if blank line is: ,,,,,,,,,,,,,,
				Matcher removeBlankFooter = Pattern.compile(REMOVE_BLANK_FOOTER_PATTERN).matcher(allLines);
				if(removeBlankFooter.find()) {
					allLines = allLines.replaceAll(REMOVE_BLANK_FOOTER_PATTERN, StringUtils.EMPTY);
				}
				
				String path = getOutputFolder(dirName);
				
				FileOutputStream fos = null;
				try {
					File file = new File(path, "prepared-ownership.csv");
					path = file.getAbsolutePath();
					fos = new FileOutputStream(file);
					fos.write(allLines.getBytes());
					fos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				
				String[][] data = readCSVTo2DArray(path, false);
				
				//all possible index of cell has value.
				List<Integer> rowHasValueIndex = new ArrayList<>();
				
				//all possible index.
				Set<Integer> setAllIndexes = new HashSet<>();
				
				//all possible index of empty cell.
				Set<Integer> isEmptyIndexes = new HashSet<>();
				
				// CSV data after using initial regex missing these header name: HorseName, AddedDate, GST
				// Can't use regex for file to find column header name addedDate and GST, better we have to find all manually.
				// We need all column data has right header name above to process in the next step.
				int dateIndex = -1;
				int gstIndex = -1;
				
				//find all cells has empty columns.
				for (int i = 0; i < data.length; i++) {
					for (int j = 0; j < data[i].length; j++) {
						setAllIndexes.add(j);
						
						if (data[i][j].equalsIgnoreCase(StringUtils.EMPTY)) {
							isEmptyIndexes.add(j);
						}
						
						//append date header
						if (isRecognizedAsValidDate(data[i][j])) {
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
				for (String[] row : data) {
					gstString.append(row[gstIndex]);
				}
				String distinctGST = gstString.toString().chars().distinct().mapToObj(c -> String.valueOf((char) c)).collect(Collectors.joining());
				if (distinctGST.matches("(YN)|(NY)|N|Y")) {
					data[0][gstIndex] = "GST";
				}
				
				//Default horse column.
				data[0][0] = "Horse";
				
				//remains columns always has data in all cells.
				setAllIndexes.removeAll(isEmptyIndexes);
				
				//find all columns with at least one cell have data, except columns always has data in all cells.
				for (Integer index : isEmptyIndexes) {
					StringBuilder isEmptyString = new StringBuilder();
					
					for (String[] row : data) {
						isEmptyString.append(row[index]);
					}
					
					if (!isEmptyString.toString().equals(StringUtils.EMPTY)) {
						rowHasValueIndex.add(index);
					}
				}
				
				//Index of non-empty columns.
				setAllIndexes.addAll(rowHasValueIndex);
				
				List<Integer> allIndexes = new ArrayList<>(setAllIndexes);
				
				try {
					StringBuilder arrayBuilder = generateCsvDataToBuild(data);
					
					BufferedWriter writer = new BufferedWriter(new FileWriter(path));
					writer.write(arrayBuilder.toString());
					writer.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				
				//read with header
				List<String> csvDataWithBankColumns = this.getCsvDataFromPath(path, false);
				
				//write csv data after format original csv file >> ignored completely empty column.
				StringBuilder builder = new StringBuilder();
				for (String line : csvDataWithBankColumns) {
					String[] r = OnboardHelper.readCsvLine(line);
					
					StringBuilder rowBuilder = new StringBuilder();
					
					//write all column has data based on columns index.
					for (Integer index : allIndexes) {
						rowBuilder.append(r[index]).append(",");
					}
					rowBuilder.append("\n");
					builder.append(rowBuilder);
				}
				
				//write data and continue reading data for the next processing step.
				try {
					File file = new File(path);
					
					FileOutputStream os = new FileOutputStream(file);
					os.write(builder.toString().getBytes());
					os.flush();
					os.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				
				String[][] blankHorseNameData = readCSVTo2DArray(path, false);
				
				//fill empty horse cells with previous cell data.
				for (int i = 1; i < blankHorseNameData.length;) {
					if (StringUtils.isNotEmpty(blankHorseNameData[i][0])) {
						for (int j = i + 1; j < blankHorseNameData.length; j++) {
							if (StringUtils.isNotEmpty(blankHorseNameData[j][0])) {
								i = j;
								continue;
							}
							blankHorseNameData[j][0] = blankHorseNameData[i][0];
						}
					}
					i++;
				}
				
				try {
					StringBuilder arrayBuilder = generateCsvDataToBuild(blankHorseNameData);
					BufferedWriter writer = new BufferedWriter(new FileWriter(path));
					writer.write(arrayBuilder.toString());
					writer.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				
				Path filePath = Paths.get(path);
				String name = "pre-format.csv";
				byte[] content = null;
				try {
					content = Files.readAllBytes(filePath);
				} catch (final IOException e) {
					e.printStackTrace();
				}
				
				//content only store raw data without right format >> we need to call automateImportOwnerShip() method to process.
				MultipartFile ownershipMultipart = new MockMultipartFile(name, content);
				
				StackTraceElement[] stackTraceElements  = Thread.currentThread().getStackTrace();
				List<String> callerMethods = Arrays.stream(stackTraceElements).map(StackTraceElement::getMethodName).collect(Collectors.toList());
				
				Object finalResult = automateImportOwnerShip(ownershipMultipart, dirName);
				
				//if only automateImport function call >> return result.
				if (!callerMethods.contains("automateImportHorse")) {
					return finalResult;
				}
				
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			return result;
		}
		
		@Override
		public Object automateImportOwnerShip(MultipartFile ownershipFile, String dirOutputPath) {
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
							"Country", "GST", "Shares", "FromDate"
					);
					
					builder.append(rowHeader);
					
					//ignore process file header
					csvData = csvData.stream().skip(1).collect(Collectors.toList());
					
					String csvDataStr = String.join("\n", csvData);
					Matcher mixingEmailTypeMatcher = Pattern.compile(MIXING_COMMS_FINANCE_EMAIL_PATTERN, Pattern.CASE_INSENSITIVE).matcher(csvDataStr);
					int expectedMixingEmailCount = 0;
					int patternMixingEmailCount = 0;
					
					if (mixingEmailTypeMatcher.find()) {
						expectedMixingEmailCount = StringUtils.countMatches(csvDataStr, "Accs:");
					} else {
						logger.info("Data don't match kind of this REGEX: {}", MIXING_COMMS_FINANCE_EMAIL_PATTERN);
					}
					mixingEmailTypeMatcher.reset();
					
					final List<String> organizationNames = Arrays.asList(
							"Company",
							"Racing",
							"Pty Ltd",
							"Racing Pty Ltd",
							"Breeding",
							"stud",
							"group",
							"bred",
							"breds",
							"tbreds",
							"Thoroughbred",
							"Thoroughbreds",
							"synd",
							"syndicate",
							"syndicates",
							"syndication",
							"syndications",
							"Bloodstock",
							"farm",
							"Horse Transport",
							"Club"
							);
					
					
					for (String line : csvData) {
						String[] r = OnboardHelper.readCsvLine(line);
						
						String horseId = OnboardHelper.readCsvRow(r, horseIdIndex);
						String horseName = OnboardHelper.readCsvRow(r, horseNameIndex);
						String ownerId = OnboardHelper.readCsvRow(r, ownerIdIndex);
						String commsEmail = OnboardHelper.readCsvRow(r, commsEmailIndex);
						String financeEmail = OnboardHelper.readCsvRow(r, financeEmailIndex);
						
						//shortest email ever contains at least 3 char: x@y
						//https://stackoverflow.com/questions/1423195/what-is-the-actual-minimum-length-of-an-email-address-as-defined-by-the-ietf
						if (StringUtils.isNotEmpty(commsEmail) && commsEmail.trim().length() < 3) {
							commsEmail = StringUtils.EMPTY;
							logger.warn("Found weird email: {} at line: {}", commsEmail, line);
						}
						
						if (StringUtils.isNotEmpty(financeEmail) && financeEmail.trim().length() < 3) {
							financeEmail = StringUtils.EMPTY;
							logger.warn("Found weird email: {} at line: {}", financeEmail, line);
						}
						
			  	        /*
			  	         ### **Process case email cell like: Accs: accounts@marshallofbrisbane.com.au Comms:
			  	         monopoly@bigpond.net.au**
			  	         - [1] Extract Comms to communication email cell.
			  	         - [2] Extract Accs to financial email cell.
			  	        */
						
						if (mixingEmailTypeMatcher.find()) {
							patternMixingEmailCount++;
							
							String tryingCommsEmail = mixingEmailTypeMatcher.group(4).trim();
							String tryingFinanceEmail = mixingEmailTypeMatcher.group(2).trim();
							
							List<String> multiCommsEmail = Arrays.asList(tryingCommsEmail.split(";"));
							List<String> multiFinanceEmail = Arrays.asList(tryingFinanceEmail.split(";"));
							
							commsEmail = getValidEmail(commsEmail, tryingCommsEmail, multiCommsEmail, line);
							
							if (StringUtils.isEmpty(financeEmail)) {
								financeEmail = getValidEmail(financeEmail, tryingFinanceEmail, multiFinanceEmail, line);
							}
						}
						
						String firstName = OnboardHelper.readCsvRow(r, firstNameIndex);
						String lastName = OnboardHelper.readCsvRow(r, lastNameIndex);
						String displayName = OnboardHelper.readCsvRow(r, displayNameIndex);
						
						//We have displayName like "Edmonds Racing CT: Toby Edmonds, Logbasex"
						//We wanna extract this name to firstName, lastName, displayName:
						//Any thing before CT is displayName, after is firstName, if after CT contains comma delimiter (,) >> lastName
						Matcher ctMatcher = Pattern.compile(CT_IN_DISPLAY_NAME_PATTERN, Pattern.CASE_INSENSITIVE).matcher(displayName);
						if (ctMatcher.find()) {
							if (StringUtils.isEmpty(firstName) && StringUtils.isEmpty(lastName)) {
								int ctStartedIndex = ctMatcher.start();
								int ctEndIndex = ctMatcher.end();
								
								logger.info("TRAINER/SYNDICATOR has CT in displayName: {}", displayName);
								//E.g: Edmonds Racing
								displayName = displayName.substring(0, ctStartedIndex).trim();
								
								//E.g: Toby Edmonds, Logbasex
								String firstAndLastNameStr = displayName.substring(ctEndIndex);
								
								boolean isOrganizationName = organizationNames.stream().anyMatch(name -> firstAndLastNameStr.toLowerCase().contains(name.toLowerCase()));
								if (isOrganizationName) {
									lastName = firstAndLastNameStr;
									
								} else {
//									StringUtils.normalizeSpace()
									String[] firstAndLastNameArr = firstAndLastNameStr.split("\\p{Z}");
									if (firstAndLastNameArr.length > 1) {
										lastName = Arrays.stream(firstAndLastNameArr).reduce((first, second) -> second).orElse("");
										String finalLastName = lastName;
										firstName = Arrays.stream(firstAndLastNameArr).filter(i -> !i.toLowerCase().equals(finalLastName)).collect(Collectors.joining(" "));
									}
									
								}
								
								logger.info("Successfully EXTRACTED firstName***: {}, lastName**: {}, displayName***: {}", firstName, lastName, displayName);
							}
						}
						
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
						
						String rawAddedDate = OnboardHelper.readCsvRow(r, addedDateIndex);
						String addedDate = StringUtils.EMPTY;
						
						//convert addedDate read from CSV to Australia date time format.
						if (StringUtils.isNotEmpty(rawAddedDate)) {
							if (rawAddedDate.matches(IS_DATE_MONTH_YEAR_FORMAT_PATTERN)) {
								addedDate = rawAddedDate;
							} else if (rawAddedDate.matches(IS_MONTH_DATE_YEAR_FORMAT_PATTERN)) {
								DateTimeFormatter expectedFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
								DateTimeFormatter rawFormatter = new DateTimeFormatterBuilder()
										.appendOptional(DateTimeFormatter.ofPattern("MM/dd/yyyy"))
										.appendOptional(DateTimeFormatter.ofPattern("M/dd/yyyy"))
										.appendOptional(DateTimeFormatter.ofPattern("MM/d/yyyy"))
										.appendOptional(DateTimeFormatter.ofPattern("M/d/yyyy"))
										.toFormatter();
								
								addedDate = LocalDate.parse(rawAddedDate, rawFormatter).format(expectedFormatter);
							} else {
								logger.info("UNKNOWN TYPE OF DATE: {} in line : {}", rawAddedDate, line);
							}
						}
						
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
					
					mixingEmailTypeMatcher.reset();
					if (mixingEmailTypeMatcher.find()) {
						if (expectedMixingEmailCount != patternMixingEmailCount) {
							logger.info("Found mixing email was faced with weird data. Please check!");
						}
					}
				}
				
				//have two type of ownership file >> one from mistable with input is csv file, one is another with input is xlsx file.
				String path = getOutputFolder(dirOutputPath);
				try {
					File file = new File(Objects.requireNonNull(path), "formatted-ownership.csv");
					
					FileOutputStream os = new FileOutputStream(file);
					os.write(builder.toString().getBytes());
				} catch (IOException e) {
					e.printStackTrace();
				}
				
				return builder;
			} catch (IOException | CustomException e) {
				e.printStackTrace();
			}
			return null;
		}
		
		private String getValidEmail(String finalEmailCellValue, String regexExtractedEmail, List<String> multiEmailsCell, String line) throws CustomException {
			if (!CollectionUtils.isEmpty(multiEmailsCell)) {
				for (String email : multiEmailsCell) {
					if (!StringHelper.isValidEmail(email.trim())) {
						logger.error("*********************Email is invalid: {} at line: {}. Please check!", email, line);
						throw new CustomException(new ErrorInfo("Invalid Email"));
					}
				}
				finalEmailCellValue = regexExtractedEmail;
			}
			return finalEmailCellValue;
		}
		
		private StringBuilder generateCsvDataToBuild(String[][] source) {
			StringBuilder arrayBuilder = new StringBuilder();
			for (String[] row : source) {
				for (String cell : row) {
					arrayBuilder.append(cell).append(",");
				}
				//Fix missing comma (,) at the end of line >> cause ArrayIndexOutOfBound bug: waynemcummins@hotmail.com,,,,,,,,,,,,,N
				arrayBuilder.append(",");
				arrayBuilder.append("\n");//append new line at the end of the row
			}
			return arrayBuilder;
		}
		
	}