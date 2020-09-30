	package com.tellyouiam.alittlebitaboutspring.service.note.v1;
	
	import com.fasterxml.jackson.annotation.JsonFormat;
	import com.fasterxml.jackson.annotation.JsonProperty;
	import com.fasterxml.jackson.databind.MapperFeature;
	import com.fasterxml.jackson.databind.ObjectMapper;
	import com.fasterxml.jackson.databind.SerializationFeature;
	import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
	import com.fasterxml.jackson.databind.annotation.JsonSerialize;
	import com.fasterxml.jackson.datatype.jsr310.JSR310Module;
	import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
	import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
	import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
	import com.tellyouiam.alittlebitaboutspring.exception.CustomException;
	import com.tellyouiam.alittlebitaboutspring.service.note.utils.NoteHelper;
	import com.tellyouiam.alittlebitaboutspring.utils.collection.MapHelper;
	import com.tellyouiam.alittlebitaboutspring.utils.string.CsvHelper;
	import com.tellyouiam.alittlebitaboutspring.utils.error.ErrorInfo;
	import com.tellyouiam.alittlebitaboutspring.utils.io.FileHelper;
	import me.xdrop.fuzzywuzzy.FuzzySearch;
	import org.apache.commons.lang3.ArrayUtils;
	import org.apache.commons.lang3.StringUtils;
	import org.apache.commons.lang3.math.NumberUtils;
	import org.apache.commons.text.similarity.LevenshteinDistance;
	import org.apache.http.NameValuePair;
	import org.apache.http.client.utils.URIBuilder;
	import org.slf4j.Logger;
	import org.slf4j.LoggerFactory;
	import org.springframework.http.HttpEntity;
	import org.springframework.http.HttpHeaders;
	import org.springframework.http.HttpMethod;
	import org.springframework.http.MediaType;
	import org.springframework.http.ResponseEntity;
	import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
	import org.springframework.stereotype.Service;
	import org.springframework.web.client.RestTemplate;
	import org.springframework.web.multipart.MultipartFile;
	import org.springframework.web.util.UriComponentsBuilder;
	
	import java.io.File;
	import java.io.FileOutputStream;
	import java.io.IOException;
	import java.net.URISyntaxException;
	import java.nio.charset.Charset;
	import java.nio.charset.StandardCharsets;
	import java.nio.file.Files;
	import java.nio.file.Paths;
	import java.nio.file.StandardOpenOption;
	import java.time.LocalDate;
	import java.time.format.DateTimeFormatter;
	import java.util.*;
	import java.util.function.Predicate;
	import java.util.regex.MatchResult;
	import java.util.regex.Matcher;
	import java.util.regex.Pattern;
	import java.util.stream.Collectors;
	import java.util.stream.Stream;
	
	import static com.tellyouiam.alittlebitaboutspring.service.note.consts.NoteConst.*;
	import static com.tellyouiam.alittlebitaboutspring.service.note.utils.NoteHelper.checkColumnIndex;
	import static com.tellyouiam.alittlebitaboutspring.service.note.utils.NoteHelper.getCsvData;
	import static com.tellyouiam.alittlebitaboutspring.service.note.utils.NoteHelper.getOutputFolder;
	import static com.tellyouiam.alittlebitaboutspring.service.note.utils.NoteHelper.isAustraliaFormat;
	import static com.tellyouiam.alittlebitaboutspring.service.note.utils.NoteHelper.isDMYFormat;
	import static com.tellyouiam.alittlebitaboutspring.service.note.utils.NoteHelper.isRecognizedAsValidDate;
	import static com.tellyouiam.alittlebitaboutspring.utils.string.OnboardHelper.*;
	import static com.tellyouiam.alittlebitaboutspring.utils.string.OnboardHelper.readCsvRow;
	import static com.tellyouiam.alittlebitaboutspring.utils.string.StringHelper.convertStringBuilderToList;
	import static com.tellyouiam.alittlebitaboutspring.utils.string.StringHelper.csvValue;
	import static com.tellyouiam.alittlebitaboutspring.utils.string.StringHelper.customSplitSpecific;
	import static java.util.Collections.max;
	import static java.util.Collections.min;
	import static java.util.Collections.sort;
	import static java.util.stream.Collectors.joining;
	import static java.util.stream.Collectors.toList;
	import static java.util.stream.Collectors.toMap;
	import static org.apache.commons.lang3.StringUtils.EMPTY;
	import static org.apache.commons.lang3.StringUtils.SPACE;
	import static org.apache.commons.lang3.StringUtils.isAllEmpty;
	import static org.apache.commons.lang3.StringUtils.isNotEmpty;
	import static org.apache.commons.lang3.StringUtils.substring;
	import static org.apache.commons.lang3.StringUtils.substringAfterLast;
	import static org.apache.commons.lang3.StringUtils.substringBeforeLast;
	import static org.springframework.util.CollectionUtils.isEmpty;
	
	@Service
	public class NoteServiceImpl implements NoteService {
	
		private static final Logger logger = LoggerFactory.getLogger(NoteServiceImpl.class);
	
		private static final String HORSE_FILE_HEADER = String.format("%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s%n",
				"OwnerId", "Email", "FinanceEmail", "FirstName", "LastName", "DisplayName", "Type",
				"Mobile", "Phone", "Fax", "Address", "City", "State", "PostCode", "Country", "Gst", "Debtor");
		
		private static final LevenshteinDistance distance = new LevenshteinDistance();
		
		@Override
		public Object automateImportOwner(MultipartFile ownerFile, String dirName) throws CustomException {
			try {
				List<String> csvData = getCsvData(ownerFile);
				List<String> preparedData = new ArrayList<>();
				StringBuilder builder = new StringBuilder();
				StringBuilder finalBuilder = new StringBuilder();
				finalBuilder.append(HORSE_FILE_HEADER);
				
				if (!isEmpty(csvData)) {
					
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
					
					String[] header = readCsvLine(csvData.get(0));
					
					int ownerIdIndex = checkColumnIndex(header, "OwnerID");
					int emailIndex = checkColumnIndex(header, "Email");
					int financeEmailIndex = checkColumnIndex(header, "FinanceEmail");
					int firstNameIndex = checkColumnIndex(header, "FirstName", "First Name");
					int lastNameIndex = checkColumnIndex(header, "LastName", "Last Name");
					int displayNameIndex = checkColumnIndex(header, "DisplayName", "Name", "Display Name", "Display", "Who");
					int typeIndex = checkColumnIndex(header, "Type");
					int mobileIndex = checkColumnIndex(header, "Mobile", "Mobile Phone");
					int phoneIndex = checkColumnIndex(header, "Phone");
					int faxIndex = checkColumnIndex(header, "Fax");
					int addressIndex = checkColumnIndex(header, "Address");
					int cityIndex = checkColumnIndex(header, "City");
					int stateIndex = checkColumnIndex(header, "State");
					int postCodeIndex = checkColumnIndex(header, "PostCode");
					int countryIndex = checkColumnIndex(header, "Country");
					int gstIndex = checkColumnIndex(header, "GST");
					int debtorIndex = checkColumnIndex(header, "Debtor", "Debtor Number");
					
					builder.append(HORSE_FILE_HEADER);
					
					csvData = csvData.stream().skip(1).collect(toList());
					Predicate<String> isEmptyRowCsv = row -> (row.matches("^(,+)$"));
					csvData = csvData.stream()
							.filter(StringUtils::isNotEmpty)
							.filter(isEmptyRowCsv.negate())
							.collect(toList());
					
					for (String line : csvData) {
						if (StringUtils.isEmpty(line)) continue;
						
						String[] r = readCsvLine(line);
						
						//rows will be ignored like:
						//,,,,
						//162 Records,,,,
						
						StringBuilder ignoreRowBuilder = new StringBuilder();
						for (String s : r) {
							ignoreRowBuilder.append(s);
						}
						if (StringUtils.isEmpty(ignoreRowBuilder.toString())) continue;
						
						if (StringUtils.isEmpty(ignoreRowBuilder.toString().replaceAll(HORSE_RECORDS_PATTERN, ""))) {
							logger.info("\n*******************Ignored Horse Records Line: {}", ignoreRowBuilder.toString());
							continue;
						}
						
						String ownerId = getCsvCellValueAtIndex(r, ownerIdIndex);
						String email = getCsvCellValueAtIndex(r, emailIndex);
						email = email.replace(",", ";");
						String financeEmail = getCsvCellValueAtIndex(r, financeEmailIndex);
						String firstName = getCsvCellValueAtIndex(r, firstNameIndex);
						String lastName = getCsvCellValueAtIndex(r, lastNameIndex);
						String displayName = getCsvCellValueAtIndex(r, displayNameIndex);
						String type = getCsvCellValueAtIndex(r, typeIndex);
						
						String mobile = getCsvCellValueAtIndex(r, mobileIndex);
						
						String phone = getCsvCellValueAtIndex(r, phoneIndex);
						
						String fax = getCsvCellValueAtIndex(r, faxIndex);
						String address = getCsvCellValueAtIndex(r, addressIndex);
						
						String city = getCsvCellValueAtIndex(r, cityIndex);
						String state = getCsvCellValueAtIndex(r, stateIndex);
						String postCode = getPostcode(getCsvCellValueAtIndex(r, postCodeIndex));
						String country = getCsvCellValueAtIndex(r, countryIndex);
						String gst = getCsvCellValueAtIndex(r, gstIndex);
						String debtor = getCsvCellValueAtIndex(r, debtorIndex);
						
						String rowBuilder = String.format("%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s%n",
								csvValue(ownerId),
								csvValue(email),
								csvValue(financeEmail),
								csvValue(firstName),
								csvValue(lastName),
								csvValue(displayName),
								csvValue(type),
								csvValue(mobile),
								csvValue(phone),
								csvValue(fax),
								csvValue(address),
								csvValue(city),
								csvValue(state),
								csvValue(postCode),
								csvValue(country),
								csvValue(gst),
								csvValue(debtor)
						);
						
						if (StringUtils.isEmpty(
								rowBuilder.replaceAll("[\",\\s]+", "")
						)) continue;
						
						preparedData.add(rowBuilder);
						
						builder.append(rowBuilder);
					}
				}
				
				int ownerIdIndex = 0;
				int emailIndex = 1;
				int financeEmailIndex = 2;
				int firstNameIndex = 3;
				int lastNameIndex = 4;
				int displayNameIndex = 5;
				int typeIndex = 6;
				int mobileIndex = 7;
				int phoneIndex = 8;
				int faxIndex = 9;
				int addressIndex = 10;
				int cityIndex = 11;
				int stateIndex = 12;
				int postCodeIndex = 13;
				int countryIndex = 14;
				int gstIndex = 15;
				int debtorIndex = 16;
				
				for (int i = 0; i < preparedData.size(); i++) {
					String[] current = readCsvLine(preparedData.get(i));
					String[] next1 = ((i + 1) < preparedData.size()) ? readCsvLine(preparedData.get(i + 1)) : null;
					String[] next2 = ((i + 2) < preparedData.size()) ? readCsvLine(preparedData.get(i + 2)) : null;
					String[] next3 = ((i + 3) < preparedData.size()) ? readCsvLine(preparedData.get(i + 3)) : null;
					String[] next4 = ((i + 4) < preparedData.size()) ? readCsvLine(preparedData.get(i + 4)) : null;
					String[] next5 = ((i + 5) < preparedData.size()) ? readCsvLine(preparedData.get(i + 5)) : null;
					String[] next6 = ((i + 6) < preparedData.size()) ? readCsvLine(preparedData.get(i + 6)) : null;
					String[] next7 = ((i + 7) < preparedData.size()) ? readCsvLine(preparedData.get(i + 7)) : null;
					String[] next8 = ((i + 8) < preparedData.size()) ? readCsvLine(preparedData.get(i + 8)) : null;
					String[] next9 = ((i + 9) < preparedData.size()) ? readCsvLine(preparedData.get(i + 9)) : null;
					String[] next10 = ((i + 10) < preparedData.size()) ? readCsvLine(preparedData.get(i + 10)) : null;
					String[] next11 = ((i + 11) < preparedData.size()) ? readCsvLine(preparedData.get(i + 11)) : null;
					String[] next12 = ((i + 12) < preparedData.size()) ? readCsvLine(preparedData.get(i + 12)) : null;
					
					String ownerId = readCsvRow(current, ownerIdIndex);
					String email = readCsvRow(current, emailIndex);
					String financeEmail = readCsvRow(current, financeEmailIndex);
					String firstName = readCsvRow(current, firstNameIndex);
					String lastName = readCsvRow(current, lastNameIndex);
					String displayName = readCsvRow(current, displayNameIndex);
					String type = readCsvRow(current, typeIndex);
					String mobile = readCsvRow(current, mobileIndex);
					String phone = readCsvRow(current, phoneIndex);
					String fax = readCsvRow(current, faxIndex);
					String address = readCsvRow(current, addressIndex);
					String city = readCsvRow(current, cityIndex);
					String state = readCsvRow(current, stateIndex);
					String postCode = readCsvRow(current, postCodeIndex);
					String country = readCsvRow(current, countryIndex);
					String gst = readCsvRow(current, gstIndex);
					String debtor = readCsvRow(current, debtorIndex);
					
					//-----------------------------
					
					
					String address1 = readCsvRow(next1, addressIndex);
					String address2 = readCsvRow(next2, addressIndex);
					String address3 = readCsvRow(next3, addressIndex);
					String address4 = readCsvRow(next4, addressIndex);
					String address5 = readCsvRow(next5, addressIndex);
					String address6 = readCsvRow(next6, addressIndex);
					String address7 = readCsvRow(next7, addressIndex);
					String address8 = readCsvRow(next8, addressIndex);
					String address9 = readCsvRow(next9, addressIndex);
					String address10 = readCsvRow(next10, addressIndex);
					String address11 = readCsvRow(next11, addressIndex);
					String address12 = readCsvRow(next12, addressIndex);
					
					
					String phone1 = readCsvRow(next1, phoneIndex);
					String phone2 = readCsvRow(next2, phoneIndex);
					String phone3 = readCsvRow(next3, phoneIndex);
					String phone4 = readCsvRow(next4, phoneIndex);
					String phone5 = readCsvRow(next5, phoneIndex);
					String phone6 = readCsvRow(next6, phoneIndex);
					String phone7 = readCsvRow(next7, phoneIndex);
					String phone8 = readCsvRow(next8, phoneIndex);
					String phone9 = readCsvRow(next9, phoneIndex);
					String phone10 = readCsvRow(next10, phoneIndex);
					String phone11 = readCsvRow(next11, phoneIndex);
					String phone12 = readCsvRow(next12, phoneIndex);
					
					String mobile1 = readCsvRow(next1, mobileIndex);
					String mobile2 = readCsvRow(next2, mobileIndex);
					String mobile3 = readCsvRow(next3, mobileIndex);
					String mobile4 = readCsvRow(next4, mobileIndex);
					String mobile5 = readCsvRow(next5, mobileIndex);
					String mobile6 = readCsvRow(next6, mobileIndex);
					String mobile7 = readCsvRow(next7, mobileIndex);
					String mobile8 = readCsvRow(next8, mobileIndex);
					String mobile9 = readCsvRow(next9, mobileIndex);
					String mobile10 = readCsvRow(next10, mobileIndex);
					String mobile11 = readCsvRow(next11, mobileIndex);
					String mobile12 = readCsvRow(next12, mobileIndex);
					
					String displayName1 = readCsvRow(next1, displayNameIndex);
					String displayName2 = readCsvRow(next2, displayNameIndex);
					String displayName3 = readCsvRow(next3, displayNameIndex);
					String displayName4 = readCsvRow(next4, displayNameIndex);
					String displayName5 = readCsvRow(next5, displayNameIndex);
					String displayName6 = readCsvRow(next6, displayNameIndex);
					String displayName7 = readCsvRow(next7, displayNameIndex);
					String displayName8 = readCsvRow(next8, displayNameIndex);
					String displayName9 = readCsvRow(next9, displayNameIndex);
					String displayName10 = readCsvRow(next10, displayNameIndex);
					String displayName11 = readCsvRow(next11, displayNameIndex);
					String displayName12 = readCsvRow(next12, displayNameIndex);
					
					String email1 = readCsvRow(next1, emailIndex);
					String email2 = readCsvRow(next2, emailIndex);
					String email3 = readCsvRow(next3, emailIndex);
					String email4 = readCsvRow(next4, emailIndex);
					String email5 = readCsvRow(next5, emailIndex);
					String email6 = readCsvRow(next6, emailIndex);
					String email7 = readCsvRow(next7, emailIndex);
					String email8 = readCsvRow(next8, emailIndex);
					String email9 = readCsvRow(next9, emailIndex);
					String email10 = readCsvRow(next10, emailIndex);
					String email11 = readCsvRow(next11, emailIndex);
					String email12 = readCsvRow(next12, emailIndex);
					
					int emailCount = 1;
					int phoneCount = 1;
					int mobileCount = 1;
					int displayNameCount = 1;
					int addressCount = 1;
					int max;
					
					//count email of only one displayName
					emailCount = getEmailCount(email, email1, email2, email3, email4, email5, email6, email7, email8, email9,
							email10, email11, email12, displayName, displayName1, displayName2, displayName3, displayName4,
							displayName5, displayName6, displayName7, displayName8, displayName9, displayName10, displayName11, displayName12, emailCount);
					
					phoneCount = getPhoneCount(phone, phone1, phone2, phone3, phone4, phone5, phone6, phone7, phone8, phone9,
							phone10, phone11, phone12, displayName, displayName1, displayName2, displayName3, displayName4,
							displayName5, displayName6, displayName7, displayName8, displayName9, displayName10, displayName11, displayName12, phoneCount);
					
					mobileCount = getPhoneCount(mobile, mobile1, mobile2, mobile3, mobile4, mobile5, mobile6, mobile7, mobile8,
							mobile9, mobile10, mobile11, mobile12, displayName, displayName1, displayName2, displayName3, displayName4,
							displayName5, displayName6, displayName7, displayName8, displayName9, displayName10, displayName11, displayName12, mobileCount);
					
					displayNameCount = getCount(displayName, displayName1, displayName2, displayName3, displayName4,
							displayName5, displayName6, displayName7, displayName8, displayNameCount);
					
					addressCount = getAddressCount(address, address1, address2, address3, address4, address5, address6, address7,
							address8, address9, address10, address11, address12, addressCount, emailCount, phoneCount, mobileCount,
							displayName, displayName1, displayName2, displayName3, displayName4, displayName5, displayName6, displayName7,
							displayName8, displayName9, displayName10, displayName11, displayName12);
					
					max = Stream.of(emailCount, phoneCount, mobileCount, displayNameCount, addressCount).max(Integer::compareTo).orElse(0);
					
					if (max == 1) {
						if (isALlEmpty(phone, mobile, email)) {
							if (isNotEmpty(address) && isNotEmpty(displayName)) {
								if (displayName.equals(displayName1) || displayName1.isEmpty()) {
									++ max;
									if (displayName.equals(displayName2) || displayName2.isEmpty()) {
										++ max;
										if (displayName.equals(displayName3) || displayName3.isEmpty()) {
											++ max;
											if (displayName.equals(displayName4) || displayName4.isEmpty()) {
												++ max;
												if (displayName.equals(displayName5) || displayName5.isEmpty()) {
													++ max;
													if (displayName.equals(displayName6) || displayName6.isEmpty()) {
														++ max;
														if (displayName.equals(displayName7) || displayName7.isEmpty()) {
															++ max;
															if (displayName.equals(displayName8) || displayName8.isEmpty()) {
																++ max;
															}
														}
													}
												}
											}
										}
									}
								}
							}
						}
					}
					
					if (i + max <= preparedData.size()) {
						address = mergeDataCell(preparedData, addressIndex, i, max);
						
						List<String> addressList = new ArrayList<>(Arrays.asList(address.split(";")));
						
						TreeSet<String> seen = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
						addressList.removeIf(s -> !seen.add(s));
						address = String.join(";", addressList);
						
						List<String> newAddressList = new ArrayList<>(Arrays.asList(address.split(";")));
						Set<String> finalAddressList = new HashSet<>();
						
						for (String addr : newAddressList) {
							
							if (finalAddressList.stream().anyMatch(u -> FuzzySearch.tokenSetRatio(addr, u) >= 95)) {
								continue;
							} else {
								finalAddressList.add(addr);
							}
							
							for (String addr1 : newAddressList) {
								if (FuzzySearch.tokenSetRatio(addr, addr1) >= 95 && !addr.equals(addr1)) {
									String longer = addr.length() > addr1.length() ? addr : addr1;
									
									String available = finalAddressList.stream().filter(u -> FuzzySearch.tokenSetRatio(longer, u) >= 95 && u.length() <= longer.length())
											.findAny().orElse(null);
									if (available != null) {
										finalAddressList.remove(available);
										finalAddressList.add(longer);
									} else {
										finalAddressList.add(longer);
									}
								}
								
							}
						}
						address = String.join(";", finalAddressList);
						
						email = this.mergeDataCell(preparedData, emailIndex, i, max);
						phone = this.mergeDataCell(preparedData, phoneIndex, i, max);
						mobile = this.mergeDataCell(preparedData, mobileIndex, i, max);
						fax = this.mergeDataCell(preparedData, faxIndex, i, max);
					}
					
					i = i + max - 1;
					
//					if (displayName.isEmpty()) continue;
					String rowBuilder = String.format("%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s%n",
							csvValue(ownerId),
							csvValue(email.contains("logbasex") ? EMPTY : email),
							csvValue(financeEmail),
							csvValue(firstName),
							csvValue(lastName),
							csvValue(displayName),
							csvValue(type),
							csvValue(mobile),
							csvValue(phone),
							csvValue(fax),
							csvValue(address),
							csvValue(city),
							csvValue(state),
							csvValue(postCode),
							csvValue(country),
							csvValue(gst),
							csvValue(debtor)
					);
					finalBuilder.append(rowBuilder);
				}
				
//				String errorDataPath = getOutputFolder(dirName) + File.separator + "owner-input-error.csv";
//				FileHelper.writeDataToFile(errorDataPath, ownerErrorData.getBytes());
				
				String path = getOutputFolder(dirName) + File.separator + "formatted-owner.csv";
				FileHelper.writeDataToFile(path, finalBuilder.toString().getBytes());
				
				return finalBuilder;
			} catch (IOException e) {
				e.printStackTrace();
			}
			return null;
		}
		
		private String mergeDataCell(List<String> preparedData, int emailIndex, int i, int max) {
			return preparedData.subList(i, i + max).stream().map(line -> {
				String[] rowArr = customSplitSpecific(line).toArray(new String[0]);
				return getCsvCellValueAtIndex(rowArr, emailIndex);
			}).distinct().filter(StringUtils::isNotEmpty).collect(Collectors.joining(";"));
		}
		
		private boolean isALlEmpty (String... params) {
			return Stream.of(params).noneMatch(StringUtils::isNotEmpty);
		}
		
		private int getCount(String email, String email1, String email2, String email3, String email4, String email5,
		                     String email6, String email7, String email8, int emailCount) {
			if (isNotEmpty(email)) {
				if (email.equals(email1)) {
					++ emailCount;
					if (email.equals(email2)) {
						++ emailCount;
						if (email.equals(email3)) {
							++ emailCount;
							if (email.equals(email4)) {
								++ emailCount;
								if (email.equals(email5)) {
									++ emailCount;
									if (email.equals(email6)) {
										++ emailCount;
										if (email.equals(email7)) {
											++ emailCount;
											if (email.equals(email8)) {
												++ emailCount;
											}
										}
									}
								}
							}
						}
					}
				}
			}
			return emailCount;
		}
		
		//ardex 2020 june 17: Pang, Barry (need distinct)
		private boolean isNotDupDisplayName(String name, String name1) {
			return Stream.of(name, name1).distinct().filter(StringUtils::isNotEmpty).count() == 1;
		}
		
		private boolean fuzzyMatchingEmail(String email, String email1) {
			if (!email.contains("@") || !email1.contains("@")) return false;
			email = email.substring(0, email.lastIndexOf('@')).replaceAll("[-+.^:,]", "");
			email1 = email1.substring(0, email1.lastIndexOf('@')).replaceAll("[-+.^:,]", "");
			return (email.contains(email1) || email1.contains(email));
		}
		private int getEmailCount(String email,
		                          String email1,
		                          String email2,
		                          String email3,
		                          String email4,
		                          String email5,
		                          String email6,
		                          String email7,
		                          String email8,
		                          String email9,
		                          String email10,
		                          String email11,
		                          String email12,
		                          String displayName,
		                          String displayName1,
		                          String displayName2,
		                          String displayName3,
		                          String displayName4,
		                          String displayName5,
		                          String displayName6,
		                          String displayName7,
		                          String displayName8,
		                          String displayName9,
		                          String displayName10,
		                          String displayName11,
		                          String displayName12,
		                          int emailCount) {
			if (isNotEmpty(email)) {
				if (fuzzyMatchingEmail(email, email1) && isNotDupDisplayName(displayName, displayName1)) {
					++ emailCount;
					if (fuzzyMatchingEmail(email, email2) && isNotDupDisplayName(displayName, displayName2)) {
						++ emailCount;
						if (fuzzyMatchingEmail(email, email3) && isNotDupDisplayName(displayName, displayName3)) {
							++ emailCount;
							if (fuzzyMatchingEmail(email, email4) && isNotDupDisplayName(displayName, displayName4)) {
								++ emailCount;
								if (fuzzyMatchingEmail(email, email5) && isNotDupDisplayName(displayName, displayName5)) {
									++ emailCount;
									if (fuzzyMatchingEmail(email, email6) && isNotDupDisplayName(displayName, displayName6)) {
										++ emailCount;
										if (fuzzyMatchingEmail(email, email7) && isNotDupDisplayName(displayName, displayName7)) {
											++ emailCount;
											if (fuzzyMatchingEmail(email, email8) && isNotDupDisplayName(displayName, displayName8)) {
												++ emailCount;
												if (fuzzyMatchingEmail(email, email9) && isNotDupDisplayName(displayName, displayName9)) {
													++ emailCount;
													if (fuzzyMatchingEmail(email, email10) && isNotDupDisplayName(displayName, displayName10)) {
														++ emailCount;
														if (fuzzyMatchingEmail(email, email11) && isNotDupDisplayName(displayName, displayName11)) {
															++ emailCount;
															if (fuzzyMatchingEmail(email, email12) && isNotDupDisplayName(displayName, displayName12)) {
																++ emailCount;
															}
														}
													}
												}
											}
										}
									}
								}
							}
						}
					}
				}
			}
			return emailCount;
		}
		
		private String formatAddress(String address) {
			return isNotEmpty(address)
					? StringUtils.deleteWhitespace(address).replace("Pty Ltd", "").replace("P/L", "").toLowerCase()
					: EMPTY;
		}
		
		private int getAddressCount(String address, String address1, String address2, String address3, String address4, String address5,
		                            String address6, String address7, String address8, String address9, String address10, String address11, String address12,
		                            int addressCount, int emailCount, int phoneCount, int mobileCount, String displayName,
		                            String displayName1, String displayName2, String displayName3, String displayName4, String displayName5, String displayName6,
		                            String displayName7, String displayName8, String displayName9, String displayName10, String displayName11, String displayName12) {
//			if (isNotEmpty(address)) {
//				address = formatAddress(address);
//				if (distance.apply(address, formatAddress(address1)) < 2) {
//					++ addressCount;
//
//					if (distance.apply(address, formatAddress(address2)) < 2) {
//						++ addressCount;
//
//						if (distance.apply(address, formatAddress(address3)) < 2) {
//							++ addressCount;
//
//							if (distance.apply(address, formatAddress(address4)) < 2) {
//								++ addressCount;
//
//								if (distance.apply(address, formatAddress(address5)) < 2) {
//									++ addressCount;
//
//									if (distance.apply(address, formatAddress(address6)) < 2) {
//										++ addressCount;
//
//										if (distance.apply(address, formatAddress(address7)) < 2) {
//											++ addressCount;
//
//											if (distance.apply(address, formatAddress(address8)) < 2) {
//												++ addressCount;
//											}
//										}
//									}
//								}
//							}
//						}
//					}
//				}
//			}
			
			if (isNotEmpty(address) && isNotEmpty(displayName)){
				if (isSameAddessSameName(address, address1, displayName, displayName1) || this.isOnlyAddressAndDisplayName(address1, emailCount, phoneCount, mobileCount, displayName1)) {
					++ addressCount;
					
					if (isSameAddessSameName(address, address2, displayName, displayName2) || this.isOnlyAddressAndDisplayName(address2, emailCount, phoneCount, mobileCount, displayName2)) {
						++ addressCount;
						
						if (isSameAddessSameName(address, address3, displayName, displayName3)  || this.isOnlyAddressAndDisplayName(address3, emailCount, phoneCount, mobileCount, displayName3)) {
							++ addressCount;
							
							if (isSameAddessSameName(address, address4, displayName, displayName4)  || this.isOnlyAddressAndDisplayName(address4, emailCount, phoneCount, mobileCount, displayName4)) {
								++ addressCount;
								
								if (isSameAddessSameName(address, address5, displayName, displayName5)  || this.isOnlyAddressAndDisplayName(address5, emailCount, phoneCount, mobileCount, displayName5)) {
									++ addressCount;
									
									if (isSameAddessSameName(address, address6, displayName, displayName6)  || this.isOnlyAddressAndDisplayName(address6, emailCount, phoneCount, mobileCount, displayName6)) {
										++ addressCount;
										
										if (isSameAddessSameName(address, address7, displayName, displayName7)  || this.isOnlyAddressAndDisplayName(address7, emailCount, phoneCount, mobileCount, displayName7)) {
											++ addressCount;
											
											if (isSameAddessSameName(address, address8, displayName, displayName8)  || this.isOnlyAddressAndDisplayName(address8, emailCount, phoneCount, mobileCount, displayName8)) {
												++ addressCount;
												
												if (isSameAddessSameName(address, address9, displayName, displayName9)  || this.isOnlyAddressAndDisplayName(address9, emailCount, phoneCount, mobileCount, displayName9)) {
													++ addressCount;
													
													if (isSameAddessSameName(address, address10, displayName, displayName10)  || this.isOnlyAddressAndDisplayName(address10, emailCount, phoneCount, mobileCount, displayName10)) {
														++ addressCount;
														
														if (isSameAddessSameName(address, address11, displayName, displayName11)  || this.isOnlyAddressAndDisplayName(address11, emailCount, phoneCount, mobileCount, displayName11)) {
															++ addressCount;
															
															if (isSameAddessSameName(address, address12, displayName, displayName12)  || this.isOnlyAddressAndDisplayName(address12, emailCount, phoneCount, mobileCount, displayName12)) {
																++ addressCount;
															}
														}
													}
												}
											}
										}
									}
								}
							}
						}
					}
				}
			}
			return addressCount;
		}
		
		private boolean isSameAddessSameName(String address, String address1, String displayName, String displayName1) {
			return FuzzySearch.tokenSetRatio(address, address1) >= 95 && (displayName1.isEmpty() || displayName1.equals(displayName));
		}
		
		private boolean isOnlyAddressAndDisplayName(String address1, int emailCount, int phoneCount, int mobileCount,
		                                            String displayName1) {
			return emailCount == 1 &&  phoneCount == 1 && mobileCount == 1 && !address1.isEmpty() && displayName1.isEmpty();
		}
		
		private Object importHorseFromMiStable(MultipartFile horseFile, String dirName) {
	
			try {
				String path = getOutputFolder(dirName).concat(File.separator).concat("formatted-horse.csv");
	
				List<String> csvData = getCsvData(horseFile);
				csvData = csvData.stream().filter(org.apache.commons.lang3.StringUtils::isNotEmpty).collect(toList());
				StringBuilder builder = new StringBuilder();
	
				StringBuilder addedDateBuilder = new StringBuilder();
				StringBuilder activeStatusBuilder = new StringBuilder();
				StringBuilder currentLocationBuilder = new StringBuilder();
	
				if (!isEmpty(csvData)) {
	
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
	
					String[] header = readCsvLine(csvData.get(0));
	
					int externalIdIndex = checkColumnIndex(header, "ExternalId");
					int nameIndex = checkColumnIndex(header, "Horse Name", "Name", "Horse");
					int foaledIndex = checkColumnIndex(header, "DOB", "foaled");
					int sireIndex = checkColumnIndex(header, "Sire");
					int damIndex = checkColumnIndex(header, "Dam");
					int colorIndex = checkColumnIndex(header, "Color", "Colour");
					int sexIndex = checkColumnIndex(header, "Gender", "Sex");
					int avatarIndex = checkColumnIndex(header, "Avatar");
					int addedDateIndex = checkColumnIndex(header, "AddedDate");
					int activeStatusIndex = checkColumnIndex(header, "Active Status", "ActiveStatus");
					int horseLocationIndex = checkColumnIndex(header, "Property");
					int horseStatusIndex = checkColumnIndex(header, "Current Status", "CurrentStatus");
					int typeIndex = checkColumnIndex(header, "Type");
					int categoryIndex = checkColumnIndex(header, "Category");
					int bonusSchemeIndex = checkColumnIndex(header, "Bonus Scheme", "BonusScheme", "Schemes");
					int nickNameIndex = checkColumnIndex(header, "Nick Name", "NickName");
					int countryIndex = checkColumnIndex(header, "Country");
					int microchipIndex = checkColumnIndex(header, "Microchip");
					int brandIndex = checkColumnIndex(header, "Brand");
	
					String rowHeader = String.format("%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s%n",
							"ExternalId", "Name", "Foaled", "Sire", "Dam", "Colour",
							"Sex", "Avatar", "Added Date", "Status",
							"Property", "Current Status",
							"Type", "Category", "Bonus Scheme", "NickName", "Country", "Microchip", "Brand"
					);
	
					builder.append(rowHeader);
					
					Predicate<String> isEmptyRowCsv = row -> (row.matches("^(,+)$"));
					csvData = csvData.stream().skip(1)
							.filter(StringUtils::isNotEmpty)
							.filter(isEmptyRowCsv.negate())
							.collect(toList());
					
					boolean isAustraliaFormat = isAustraliaFormat(csvData, foaledIndex, "horse");
					for (String line : csvData) {
						String[] r = readCsvLine(line);
	
						String externalId = getCsvCellValueAtIndex(r, externalIdIndex);
						String name = getCsvCellValueAtIndex(r, nameIndex);
	
						String rawFoaled = getCsvCellValueAtIndex(r, foaledIndex);
						String foaled;
	
						if (!isAustraliaFormat && isNotEmpty(rawFoaled)) {
							foaled = LocalDate.parse(rawFoaled, AMERICAN_CUSTOM_DATE_FORMAT).format(AUSTRALIA_FORMAL_DATE_FORMAT);
						} else {
							foaled = rawFoaled;
						}
	
						String sire = getCsvCellValueAtIndex(r, sireIndex);
						String dam = getCsvCellValueAtIndex(r, damIndex);
						if (sire.isEmpty() && dam.isEmpty()) continue;
						String color = getCsvCellValueAtIndex(r, colorIndex);
						String sex = getCsvCellValueAtIndex(r, sexIndex);
	
						String avatar = getCsvCellValueAtIndex(r, avatarIndex);
	
						String addedDate = getCsvCellValueAtIndex(r, addedDateIndex);
						addedDateBuilder.append(addedDate);
	
						String activeStatus = getCsvCellValueAtIndex(r, activeStatusIndex);
						addedDateBuilder.append(activeStatus);
	
						String currentLocation = getCsvCellValueAtIndex(r, horseLocationIndex);
						addedDateBuilder.append(currentLocation);
	
						String currentStatus = getCsvCellValueAtIndex(r, horseStatusIndex);
						String type = getCsvCellValueAtIndex(r, typeIndex);
						String category = getCsvCellValueAtIndex(r, categoryIndex);
						String bonusScheme = getCsvCellValueAtIndex(r, bonusSchemeIndex);
						String nickName = getCsvCellValueAtIndex(r, nickNameIndex);
						String country = getCsvCellValueAtIndex(r, countryIndex);
						String microchip = getCsvCellValueAtIndex(r, microchipIndex);
						String brand = getCsvCellValueAtIndex(r, brandIndex);
	
						String rowBuilder = String.format("%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s\n",
								csvValue(externalId),
								csvValue(name),
								csvValue(foaled),
								csvValue(sire),
								csvValue(dam),
								csvValue(color),
								csvValue(sex),
								csvValue(avatar),
								csvValue(addedDate),
								csvValue(activeStatus),
								csvValue(currentLocation),
								csvValue(currentStatus),
								csvValue(type),
								csvValue(category),
								csvValue(bonusScheme),
								csvValue(nickName),
								csvValue(country),
								csvValue(microchip),
								csvValue(brand)
						);
						builder.append(rowBuilder);
					}
	
					// Address case addedDate, activeStatus and current location in horse file are empty.
					// We will face with an error if we keep this data intact.
					if (isAllEmpty(addedDateBuilder, activeStatusBuilder, currentLocationBuilder)) {
						logger.warn("All of AddedDate && ActiveStatus && CurrentLocation can't be empty. At least addedDate required.");

						List<String> formattedData = convertStringBuilderToList(builder);
						StringBuilder dataBuilder = new StringBuilder();

						String currentDate = LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
						if (!isEmpty(formattedData)) {
							String[] formattedHeader = readCsvLine(formattedData.get(0));

							//Get addedDate index from header
							int addedDateOrdinal = checkColumnIndex(formattedHeader, "AddedDate");

							//Append a header at first line of StringBuilder data to write to file.
							dataBuilder.append(formattedData.get(0)).append("\n");

							//process data ignore header
							for (String line : formattedData.stream().skip(1).collect(toList())) {

								String[] row = readCsvLine(line);

								for (int i = 0; i < row.length; i++) {

									//replace empty addedDate with current date.
									if (i == addedDateOrdinal) {
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
		
		private int getPhoneCount(String phone,
		                          String phone1,
		                          String phone2,
		                          String phone3,
		                          String phone4,
		                          String phone5,
		                          String phone6,
		                          String phone7,
		                          String phone8,
		                          String phone9,
		                          String phone10,
		                          String phone11,
		                          String phone12,
		                          String displayName,
		                          String displayName1,
		                          String displayName2,
		                          String displayName3,
		                          String displayName4,
		                          String displayName5,
		                          String displayName6,
		                          String displayName7,
		                          String displayName8,
		                          String displayName9,
		                          String displayName10,
		                          String displayName11,
		                          String displayName12,
		                          int phoneCount) {
			if (isNotEmpty(phone)) {
				if (isPhoneEquals(phone, phone1) && isNotDupDisplayName(displayName, displayName1)) {
					++ phoneCount;
					if (isPhoneEquals(phone, phone2) && isNotDupDisplayName(displayName, displayName2)) {
						++ phoneCount;
						if (isPhoneEquals(phone, phone3) && isNotDupDisplayName(displayName, displayName3)) {
							++ phoneCount;
							if (isPhoneEquals(phone, phone4) && isNotDupDisplayName(displayName, displayName4)) {
								++ phoneCount;
								if (isPhoneEquals(phone, phone5) && isNotDupDisplayName(displayName, displayName5)) {
									++ phoneCount;
									if (isPhoneEquals(phone, phone6) && isNotDupDisplayName(displayName, displayName6)) {
										++ phoneCount;
										if (isPhoneEquals(phone, phone7) && isNotDupDisplayName(displayName, displayName7)) {
											++ phoneCount;
											if (isPhoneEquals(phone, phone8) && isNotDupDisplayName(displayName, displayName8)) {
												++ phoneCount;
												if (isPhoneEquals(phone, phone9) && isNotDupDisplayName(displayName, displayName9)) {
													++ phoneCount;
													if (isPhoneEquals(phone, phone10) && isNotDupDisplayName(displayName, displayName10)) {
														++ phoneCount;
														if (isPhoneEquals(phone, phone11) && isNotDupDisplayName(displayName, displayName11)) {
															++ phoneCount;
															if (isPhoneEquals(phone, phone12) && isNotDupDisplayName(displayName, displayName12)) {
																++ phoneCount;
															}
														}
													}
												}
											}
										}
									}
								}
							}
						}
					}
				}
			}
			return phoneCount;
		}
		
		private boolean isPhoneEquals(String phone, String phone1) {
			return phone.trim().replaceAll(Pattern.quote("[-+)(s]+"), "").equals(phone1.trim().replaceAll(Pattern.quote("[-+)(s]+"), ""));
		}
		
		@SuppressWarnings("unchecked")
		public Object automateImportHorse(MultipartFile horseFile, List<MultipartFile> ownershipFiles, String dirName) {
			if (isEmpty(ownershipFiles)) {
				return this.importHorseFromMiStable(horseFile, dirName);
			}
	
			//TODO
			Map<Object, Object> ownerShipResult = this.automateImportOwnerShips(ownershipFiles);
			Map<Object, Object> result = new HashMap<>();
	
			List<String> csvExportedDates = (List<String>)ownerShipResult.get("exportedDate");
			String csvExportedDateStr = csvExportedDates.get(0);
	
			try {
				List<String> csvData = getCsvData(horseFile);
				csvData = csvData.stream().filter(StringUtils::isNotEmpty).collect(toList());
	
				StringBuilder builder = new StringBuilder();
				if (!isEmpty(csvData)) {
	
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
					// COUNTRY
					// MICROCHIP
					// BRAND
	
					String[] header = readCsvLine(csvData.get(0));
	
					int externalIdIndex = checkColumnIndex(header, "ExternalId");
					int nameIndex = checkColumnIndex(header, "Horse Name", "Name", "Horse");
					int foaledIndex = checkColumnIndex(header, "DOB", "foaled");
					int sireIndex = checkColumnIndex(header, "Sire");
					int damIndex = checkColumnIndex(header, "Dam");
					int colorIndex = checkColumnIndex(header, "Color");
					int sexIndex = checkColumnIndex(header, "Gender", "Sex");
					int avatarIndex = checkColumnIndex(header, "Avatar");
					int addedDateIndex = checkColumnIndex(header, "AddedDate");
					int activeStatusIndex = checkColumnIndex(header, "Active Status", "ActiveStatus");
					int horseLocationIndex = checkColumnIndex(header, "Property");
					int horseStatusIndex = checkColumnIndex(header, "Current Status", "CurrentStatus");
					int typeIndex = checkColumnIndex(header, "Type");
					int categoryIndex = checkColumnIndex(header, "Category");
					int bonusSchemeIndex = checkColumnIndex(header, "Bonus Scheme", "BonusScheme", "Schemes");
					int nickNameIndex = checkColumnIndex(header, "Nick Name", "NickName");
					int countryIndex = checkColumnIndex(header, "Country");
					int microchipIndex = checkColumnIndex(header, "Microchip");
					int brandIndex = checkColumnIndex(header, "Brand");
					
					int daysHereIndex = checkColumnIndex(header, "Days Here", "Days");
	
					String rowHeader = String.format("%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s%n",
							"ExternalId", "Name", "Foaled", "Sire", "Dam", "Color",
							"Sex", "Avatar", "AddedDate", "ActiveStatus/Status",
							"CurrentLocation/HorseLocation", "CurrentStatus/HorseStatus",
							"Type", "Category", "BonusScheme", "NickName", "Country", "MicroChip", "Brand"
					);
	
					builder.append(rowHeader);
	
					csvData = csvData.stream().skip(1).collect(toList());
	
					Map<String, String> horseMap = new LinkedHashMap<>();
					Map<String, String> horseOwnershipMap = (Map<String, String>) ownerShipResult.get("horseDataMap");
	
					boolean isAustraliaFormat = isAustraliaFormat(csvData, foaledIndex, "horse");
	
					int count = 1;
					for (String line : csvData) {
	
						count++;
	
						if (StringUtils.isEmpty(line)) continue;
	
						if (line.matches("(?m)^([,]+)$")) {
							logger.info("***************************Empty CSV Data at line number: {}", count);
							continue;
						}
	
						if (line.matches(CSV_HORSE_COUNT_PATTERN)) {
							logger.info("***************************Ignored Horse Count Info at line number: {}", count);
							continue;
						}
	
						String[] r = readCsvLine(line);
	
						String externalId = getCsvCellValueAtIndex(r, externalIdIndex);
						String name = getCsvCellValueAtIndex(r, nameIndex);
	
						if (StringUtils.isEmpty(name)) {
							logger.info("**************************Empty Horse Name: {} at line: {}", name, line);
							continue;
						}
	
						String rawFoaled = getCsvCellValueAtIndex(r, foaledIndex);
						rawFoaled = rawFoaled.split("\\p{Z}")[0];
						String foaled;
						if (!isAustraliaFormat && isNotEmpty(rawFoaled)) {
							foaled = LocalDate.parse(rawFoaled, AMERICAN_CUSTOM_DATE_FORMAT).format(AUSTRALIA_FORMAL_DATE_FORMAT);
						} else {
							foaled = rawFoaled;
						}
	
						String sire = getCsvCellValueAtIndex(r, sireIndex);
						String dam = getCsvCellValueAtIndex(r, damIndex);
	
						if (StringUtils.isEmpty(name) && StringUtils.isEmpty(sire) && StringUtils.isEmpty(dam)
								&& StringUtils.isEmpty(rawFoaled)) continue;
	
						String color = getCsvCellValueAtIndex(r, colorIndex);
						String sex = getCsvCellValueAtIndex(r, sexIndex);
						String avatar = getCsvCellValueAtIndex(r, avatarIndex);
						String dayHere = getCsvCellValueAtIndex(r, daysHereIndex);
						String addedDate = getCsvCellValueAtIndex(r, addedDateIndex);
						String activeStatus = getCsvCellValueAtIndex(r, activeStatusIndex);
						String currentLocation = getCsvCellValueAtIndex(r, horseLocationIndex);
						String currentStatus = getCsvCellValueAtIndex(r, horseStatusIndex);
						String type = getCsvCellValueAtIndex(r, typeIndex);
						String category = getCsvCellValueAtIndex(r, categoryIndex);
						String bonusScheme = getCsvCellValueAtIndex(r, bonusSchemeIndex);
						String nickName = getCsvCellValueAtIndex(r, nickNameIndex);
						String country = getCsvCellValueAtIndex(r, countryIndex);
						String microchip = getCsvCellValueAtIndex(r, microchipIndex);
						String brand = getCsvCellValueAtIndex(r, brandIndex);
	
						// If dayHere is empty, get exportedDate of ownership file. Because of:
						// When dayHere is empty, usually departed date in horse line of ownership file also empty too.
						// Maybe we use regex we detect 09/10/2019 as departed date (is wrong):
						// Absorb ( Redoute's Choice - Mother Flame (NZ)) 17yo Bay Mare      Last served by Sioux Nation on 09/10/2019 - Early Scan
						// (Normally in ownership file because ownership file and horse file are exported in the same day).
						// If not in the same day, we have to determine what's horse file exported date is.
	
						if (StringUtils.isEmpty(dayHere)) {
							Set<String> ownershipKeyMap = horseOwnershipMap.keySet();
							boolean isSameHorseName = ownershipKeyMap.stream().anyMatch(name::equalsIgnoreCase);

							if (isSameHorseName) {
								String ownershipAddedDate = horseOwnershipMap.get(name);
								addedDate = csvExportedDateStr;
							} else {
								// Address case addedDate, activeStatus and current location in horse file are empty.
								// We will face an error if we keep this data intact.
								if (StringUtils.isEmpty(addedDate) && StringUtils.isEmpty(activeStatus) && StringUtils.isEmpty(currentLocation)) {
									addedDate = csvExportedDateStr;
								}
							}
						} else {
							long minusDays = Long.parseLong(dayHere);
							LocalDate dateAtNewestLocation = LocalDate.parse(csvExportedDateStr, AUSTRALIA_CUSTOM_DATE_FORMAT).minusDays(minusDays);
							addedDate = dateAtNewestLocation.format(AUSTRALIA_FORMAL_DATE_FORMAT);
						}
	
						horseMap.put(name, addedDate);
	
						String rowBuilder = String.format("%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s%n",
								csvValue(externalId),
								csvValue(name),
								csvValue(foaled),
								csvValue(sire),
								csvValue(dam),
								csvValue(color),
								csvValue(sex),
								csvValue(avatar),
								csvValue(addedDate),
								csvValue(activeStatus),
								csvValue(currentLocation),
								csvValue(currentStatus),
								csvValue(type),
								csvValue(category),
								csvValue(bonusScheme),
								csvValue(nickName),
								csvValue(country),
								csvValue(microchip),
								csvValue(brand)
						);
						builder.append(rowBuilder);
					}
	
					//compare horse data from horse file and ownerShip file to make sure horse data are exact or not.
					Map<String, String> fromHorseFile = MapHelper.getDiffMap(horseMap, horseOwnershipMap, false);
					Set<String> keyHorse = fromHorseFile.keySet();
					Map<String, String> fromOwnerShipFile = horseOwnershipMap.entrySet().stream()
							.filter(x -> keyHorse.contains(x.getKey()))
							.collect(toMap(Map.Entry::getKey, Map.Entry::getValue));

					result.put("Diff From Horse File", new TreeMap<>(fromHorseFile));
					result.put("Diff From OwnerShip File", new TreeMap<>(fromOwnerShipFile));
				}
	
				String path = getOutputFolder(dirName) + File.separator + "formatted-horse.csv";
				Files.write(Paths.get(path), builder.toString().getBytes());
	
			} catch (IOException e) {
				e.printStackTrace();
			}
			return result;
		}
		
		@Override
		public Map<Object, Object> automateImportOwnerShips(List<MultipartFile> ownershipFiles) {
			String ownershipHeader = String.format("%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s%n",
					"HorseId", "HorseName",
					"OwnerID", "CommsEmail", "FinanceEmail", "FirstName", "LastName", "DisplayName",
					"Type", "Mobile", "Phone", "Fax", "Address", "City", "State", "PostCode",
					"Country", "GST", "Debtor", "Shares", "FromDate", "ExportedDate"
			);
		
			String nameHeader = String.format("%s,%s,%s,%s%n", "RawDisplayName", "Extracted DisplayName", "Extracted FirstName", "Extracted LastName");
			
			Map<Object, Object> result = new HashMap<>();
			List<String> exportedDateList = new ArrayList<>();
			
			StringBuilder dataBuilder = new StringBuilder(ownershipHeader);
//			StringBuilder nameBuilder = new StringBuilder(nameHeader);
//			StringBuilder normalNameBuilder = new StringBuilder("\n***********NORMAL NAME***********\n");
//			StringBuilder organizationNameBuilder = new StringBuilder("\n***********ORGANIZATION NAME***********\n");
			
			for (MultipartFile ownershipFile: ownershipFiles) {
				try {
					List<String> csvData = getCsvData(ownershipFile);
					String allLines = String.join("\n", csvData);
			
					// get file exportedDate.
					// Pattern : ,,,,,,,,,,,,,,Printed: 21/10/2019  3:41:46PM,,,,Page -1 of 1,,,,
					String exportedDate = null;
					Matcher exportedDateMatcher = Pattern.compile(NORMAL_OWNERSHIP_EXPORTED_DATE_PATTERN,
							Pattern.CASE_INSENSITIVE)
							.matcher(allLines);
			
					boolean isNormalExportedDate = false;
					while (exportedDateMatcher.find()) {
		
						isNormalExportedDate = true;
						//get date use group(2) of regex.
						exportedDate = exportedDateMatcher.group(2);
		
						// process for case horse file was exported before ownership file was exported.
						// Using date info in ownership file can cause mismatching in data.
						// exportedDate = LocalDate.parse(exportedDate, AUSTRALIA_CUSTOM_DATE_FORMAT).minusDays(1)
						// .format(AUSTRALIA_CUSTOM_DATE_FORMAT);
						if (!isDMYFormat(exportedDate)) {
						   logger.error("The exported date was not recognized as a valid Australia format: {}", exportedDate);
						}
					}
			
					//Ardex exportedDate like : Tuesday, 28 April, 2020
					if (!isNormalExportedDate) {
						Matcher ardexExportedDateMatcher = Pattern.compile(ARDEX_OWNERSHIP_EXPORTED_DATE_PATTERN,
								Pattern.CASE_INSENSITIVE)
								.matcher(allLines);
						while (ardexExportedDateMatcher.find()) {
							exportedDate = ardexExportedDateMatcher.group(2);
							exportedDate =
									LocalDate.parse(exportedDate, ARDEX_DATE_FORMAT).format(AUSTRALIA_FORMAL_DATE_FORMAT);
						}
					}
					//exportedDateList.add(exportedDate);
					exportedDateList.add("17/06/2020");
					
					// Line has departedDate likely to extract:
					//Azurite (IRE) ( Azamour (IRE) - High Lite (GB)) 9yo Bay Gelding     Michael Hickmott Bloodstock - In
					//training Michael Hickmott Bloodstock 1/08/2019 >> 1/08/2019
					//This is required for make sure horse data after format csv are exact.
					
					Matcher departedDateMatcher = Pattern.compile(EXTRACT_DEPARTED_DATE_OF_HORSE_PATTERN).matcher(allLines);
					
					Map<String, String> horseDataMap = new LinkedHashMap<>();
					
//					while (departedDateMatcher.find()) {
//						String horseName = departedDateMatcher.group(1).trim();
//						String horseDepartedDate = departedDateMatcher.group(3).trim();
//
//						if (StringUtils.isEmpty(horseName))
//							continue;
//
//						if (StringUtils.isEmpty(horseDepartedDate))
//							logger.info("Horse without departed date: {}", horseName);
//
//						if (!isDMYFormat(horseDepartedDate)) {
//							throw new CustomException(new ErrorInfo("The departed date was not recognized as a valid Australia format: {}", horseDepartedDate));
//						}
//
//						//process for case: 25/08/19 (usually 25/08/2019)
//						String horseDate = LocalDate.parse(horseDepartedDate, AUSTRALIA_CUSTOM_DATE_FORMAT).format(AUSTRALIA_FORMAL_DATE_FORMAT);
//						horseDataMap.put(horseName, horseDate);
//					}
					
					result.put("horseDataMap", horseDataMap);
					
					Matcher blankLinesMatcher = Pattern.compile(REMOVE_BANK_LINES_PATTERN).matcher(allLines);
					if (blankLinesMatcher.find()) {
						allLines = allLines.replaceAll(REMOVE_BANK_LINES_PATTERN, EMPTY);
					} else {
						throw new CustomException(ErrorInfo.CANNOT_FORMAT_OWNERSHIP_FILE_USING_REGEX_ERROR);
					}
					
					Matcher linesBreakMatcher = Pattern.compile(REMOVE_LINE_BREAK_PATTERN).matcher(allLines);
					if (linesBreakMatcher.find()) {
						allLines = allLines.replaceAll(REMOVE_LINE_BREAK_PATTERN, " CT");
					} else {
						logger.warn("Cannot apply regex: {}... for ownership file", REMOVE_LINE_BREAK_PATTERN);
					}
			
					//optional
					Matcher invalidSharesMatcher = Pattern.compile(REMOVE_INVALID_SHARES_PATTERN,
							Pattern.CASE_INSENSITIVE).matcher(allLines);
					if (invalidSharesMatcher.find()) {
						allLines = allLines.replaceAll(REMOVE_INVALID_SHARES_PATTERN, "0.00%");
					} else {
						logger.warn("Cannot apply regex: {}... for ownership file", REMOVE_INVALID_SHARES_PATTERN);
					}
			
					//Matcher correctHorseNameMatcher = Pattern.compile(CORRECT_HORSE_NAME_PATTERN).matcher(allLines);
					csvData = Arrays.asList(allLines.split("\n"));
					
					int horseCount = 0;
		
					/**
					 * Preprocess data sample:
					 .-----------------------------------------------------------------------------------------------------------------------------------------------.------------.---------------------------------------------------.
					 |                                                                    Share %                                                                    |            |                   Display Name                    |
					 :-----------------------------------------------------------------------------------------------------------------------------------------------+------------+---------------------------------------------------:
					 | Ambidexter/Elancer 16 ( Ambidexter - Elancer) 3yo Brown Colt Michael Hickmott Bloodstock - In training Michael Hickmott Bloodstock 24/12/2019 |            |                                                   |
					 :-----------------------------------------------------------------------------------------------------------------------------------------------+------------+---------------------------------------------------:
					 | 50.00%                                                                                                                                        | 31/03/2018 | "Andrew Maloney Trading Syndicate Andrew Maloney" |
					 :-----------------------------------------------------------------------------------------------------------------------------------------------+------------+---------------------------------------------------:
					 | 25.00%                                                                                                                                        | 31/03/2018 | Cornerstone Stud                                  |
					 '-----------------------------------------------------------------------------------------------------------------------------------------------'------------'---------------------------------------------------'
					 * Expected after processing data:
					 * - Horse name is extracted [required for all case]
					 * - Locate share percentage column is next to horse name column (for case share percentage as above) [rare case].
					 *
					 .-----------------------.---------.------------.-----------------------------------.
					 |                       | Share % |            |           Display Name            |
					 :-----------------------+---------+------------+-----------------------------------:
					 | Ambidexter/Elancer 16 |         |            |                                   |
					 :-----------------------+---------+------------+-----------------------------------:
					 |                       | 50.00%  | 31/03/2018 | "Andrew Maloney Trading Syndicate |
					 :-----------------------+---------+------------+-----------------------------------:
					 |                       | 25.00%  | 31/03/2018 | Cornerstone Stud                  |
					 * */
					
					List<String> correctHorseNameArr =  new ArrayList<>();
					for (String line : csvData) {
						
						Matcher correctHorseNameMatcher = Pattern.compile(CORRECT_HORSE_NAME_PATTERN).matcher(line);
						Matcher tryingShareColumnPosition =
								Pattern.compile(TRYING_SHARE_COLUMN_POSITION_PATTERN).matcher(line);
						
						if (correctHorseNameMatcher.find()) {
							horseCount++;
							
							List<Integer> leftIndexes = new ArrayList<>();
							List<Integer> rightIndexes = new ArrayList<>();
							
							//replace the quote in line that contains horse name, keep it intact can cause mismatch column's data.
							line = line.replace("\"",  "");
							for (MatchResult match : allMatches(Pattern.compile("(\\((?:[^)(]+|\\((?:[^)(]+|\\([^)(]*\\))*\\))*\\))"), line)) {
								int start = match.start();
								int end = match.end();
								leftIndexes.add(start);
								rightIndexes.add(end);
							}
	
							//Manhattan Muse (AUS)/Dream Ahead (USA) 18 ( Dream Ahead (USA) - Manhattan Muse (AUS))
							//We need extract Manhattan Muse (AUS)/Dream Ahead (USA) 18.
							int startSireDamInfoIndex = max(leftIndexes);
							int endSireDamInfoIndex = max(rightIndexes);
							int secondLeftBracketIndex = startSireDamInfoIndex;
							if (leftIndexes.size() > 1) {
								secondLeftBracketIndex = leftIndexes.stream().sorted().skip(1).findFirst().get();
							}
							int secondRightBracketIndex = endSireDamInfoIndex;
							if (rightIndexes.size() > 1) {
								secondRightBracketIndex = rightIndexes.stream().sorted(Comparator.reverseOrder()).skip(1).findFirst().get();
							}
							int thirdLeftBracketIndex = secondLeftBracketIndex;
							if (leftIndexes.size() > 2) {
								thirdLeftBracketIndex = leftIndexes.stream().sorted().skip(2).findFirst().get();
							}
							int thirdRightBracketIndex = secondRightBracketIndex;
							if (rightIndexes.size() > 2) {
								thirdRightBracketIndex = rightIndexes.stream().sorted(Comparator.reverseOrder()).skip(2).findFirst().get();
							}
							if (leftIndexes.size() > 0 && substring(line, min(leftIndexes), min(rightIndexes)).contains("-")) {
								startSireDamInfoIndex = min(leftIndexes);
								endSireDamInfoIndex = max(rightIndexes);
							}
							
							if (leftIndexes.size() > 1 &&substring(line, secondLeftBracketIndex, secondRightBracketIndex).contains("-")) {
								startSireDamInfoIndex = secondLeftBracketIndex;
								endSireDamInfoIndex = secondRightBracketIndex;
							}
							
							if (leftIndexes.size() > 2 && substring(line, thirdLeftBracketIndex, thirdRightBracketIndex).contains("-")) {
								startSireDamInfoIndex = thirdLeftBracketIndex;
								endSireDamInfoIndex = thirdRightBracketIndex;
							}
							
							String sireDamPart = substring(line, startSireDamInfoIndex, endSireDamInfoIndex);
							String namePart = substringBeforeLast(line, sireDamPart);
							String additionalInfoPart = substringAfterLast(line, sireDamPart);
							
							if (!additionalInfoPart.contains("yo")) {
								logger.warn("Wired data");
							}
							line = namePart.trim();
							correctHorseNameArr.add(line);
						} else if (tryingShareColumnPosition.find()) {
							
							//we read ownership CSV data by header column name.
							line = line.replaceAll(TRYING_SHARE_COLUMN_POSITION_PATTERN, ",$1");
							correctHorseNameArr.add(line);
						} else if (line.contains("Share %,") && !line.contains(",Share %")) {
		
							//we read ownership CSV data by header column name.
							line = line.replace("Share %", ",Share %");
							correctHorseNameArr.add(line);
						} else {
							correctHorseNameArr.add(line);
						}
					}
					
					allLines = String.join("\n", correctHorseNameArr);
					
					result.put("HorseCount", horseCount);
			
					//ignore extra data from line contains horse name >> horse name
					//Ambidexter/Elancer 16 ( Ambidexter - Elancer) 3yo Brown Colt     Michael Hickmott Bloodstock - In
					// training Michael Hickmott Bloodstock 24/12/2019 >> Ambidexter/Elancer 16
	
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
					Matcher unnecessaryDataMatcher = Pattern.compile(REMOVE_UNNECESSARY_DATA).matcher(allLines);
					if (unnecessaryDataMatcher.find()) {
						allLines = allLines.replaceAll(REMOVE_UNNECESSARY_DATA, "");
					} else {
						logger.warn("Data seemingly weird. Please check!");
					}

					unnecessaryDataMatcher.reset();
					StringBuilder ignoredData = new StringBuilder();
					int gossipDataCount = 0;
					while (unnecessaryDataMatcher.find()) {
						gossipDataCount++;
						ignoredData.append(unnecessaryDataMatcher.group());
					}

					logger.info("******************************IGNORED DATA**********************************\n {}",
							ignoredData);
					//normally unnecessary lines to ignored between 5 and 10.;
					if (gossipDataCount > IGNORED_NON_DATA_LINE_THRESHOLD) {
						logger.error("CSV data seems a little weird. Please check!");
					}
			
					String[][] data = NoteHelper.get2DArrayFromString(allLines);
					
					//Trying to prevent arrayIndexOutOfBoundException. Unify the row's length in the two-dimensional array.
					int rowLength = Arrays.stream(data).max(Comparator.comparingInt(ArrayUtils::getLength)).get().length;
					for (int i = 0; i < data.length; i++) {
						if (data[i].length < rowLength) {
							for (int j = 0; j <= rowLength - data[i].length; j++) {
								data[i] = ArrayUtils.add(data[i], "");
							}
						}
					}
					
					//all possible index of cell has value.
					List<Integer> rowHasValueIndex = new ArrayList<>();
			
					//all possible index.
					Set<Integer> setAllIndexes = new HashSet<>();
			
					//all possible index of empty cell.
					Set<Integer> isEmptyIndexes = new HashSet<>();
			
					// CSV data after using initial regex usually missing these header name: HorseName, AddedDate, GST
					// Can't use regex for file to find column header name addedDate and GST, better we have to find all
					// manually.
					// We need all column data has right header name above to process in the next step.
					int dateIndex = -1;
					int gstIndex = -1;
			
					//find all cells has empty columns.
					for (int i = 0; i < data.length; i++) {
						for (int j = 0; j < data[i].length; j++) {
					
							setAllIndexes.add(j);
					
							if (data[i][j].equalsIgnoreCase(EMPTY)) {
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
					StringBuilder gstString = new StringBuilder();
					for (String[] row : data) {
						try {
							gstString.append(row[gstIndex]);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
			
					//TODO Pattern match
					String distinctGST =
							gstString.toString().chars().distinct().mapToObj(c -> String.valueOf((char) c)).collect(joining());
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
				
						if (!isEmptyString.toString().equals(EMPTY)) {
							rowHasValueIndex.add(index);
						}
					}
			
					//Index of non-empty columns.
					setAllIndexes.addAll(rowHasValueIndex);
			
					List<Integer> allIndexes = new ArrayList<>(setAllIndexes);
			
					List<String> csvDataWithBankColumns = NoteHelper.getListFrom2DArrString(data);
			
					//write csv data after format original csv file >> ignored completely empty column.
					StringBuilder builder = new StringBuilder();
					for (String line : csvDataWithBankColumns) {
						String[] r = readCsvLine(line);
				
						StringBuilder rowBuilder = new StringBuilder();
				
						//write all column has data based on columns index.
						for (Integer index : allIndexes) {
							rowBuilder.append(r[index]).append(",");
						}
						rowBuilder.append("\n");
						builder.append(rowBuilder);
					}
			
					String[][] blankHorseNameData = NoteHelper.get2DArrayFromString(builder.toString());
			
					//fill empty horse name cells as same as previous cell data.
					//ignore reading header
					for (int i = 1; i < blankHorseNameData.length; ) {
						if (isNotEmpty(blankHorseNameData[i][0])) {
							
							for (int j = i + 1; j < blankHorseNameData.length; j++) {
								if (isNotEmpty(blankHorseNameData[j][0])) {
									i = j;
									continue;
								}
								blankHorseNameData[j][0] = blankHorseNameData[i][0];
							}
						}
						++i;
					}
					
					//check if total share percentage of a horse is 100%
					for (int i = blankHorseNameData.length - 1; i > 0;) {
						
						String currentHorseName = blankHorseNameData[i][0];
						String previousHorseName = blankHorseNameData[i - 1][0];
						
						double sharePercentCount = 0.00;
						do {
							String shareStr = blankHorseNameData[i - 1][1].trim();
							double sharePercent = 0.00;
							
							if (StringUtils.isEmpty(shareStr)) {
								sharePercent = 0.00;
							} else {
								shareStr = shareStr.replace("%", "");
								
								if(NumberUtils.isParsable(shareStr)) {
									sharePercent = Double.parseDouble(shareStr);
								}
							}
				
							sharePercentCount += sharePercent;
							
							i--;
						} while (i > 0 && currentHorseName.equals(previousHorseName));
		
						if (sharePercentCount != 100.00) {
							logger.error("Horse with inadequate share percentage: {}", currentHorseName);
						}
					}
			
					List<String> csvDataList = NoteHelper.getListFrom2DArrString(blankHorseNameData);
			
					if (!isEmpty(csvDataList)) {
				
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
						// DEBTOR
						// -------------------------
						// BALANCE (SHARE PERCENTAGE)
						// FROM_DATE
						// TO_DATE
						// EXPORTED_DATE
						// -------------------------------------------------------------
				
						String[] header = readCsvLine(csvDataList.get(0));
				
						int horseIdIndex = checkColumnIndex(header, "Horse Id");
						int horseNameIndex = checkColumnIndex(header, "Horse Name", "Horse");
						int ownerIdIndex = checkColumnIndex(header, "Owner Id");
						int commsEmailIndex = checkColumnIndex(header, "CommsEmail", "Email");
						int financeEmailIndex = checkColumnIndex(header, "Finance Email", "FinanceEmail");
						int firstNameIndex = checkColumnIndex(header, "FirstName", "First Name");
						int lastNameIndex = checkColumnIndex(header, "LastName", "Last Name");
						int displayNameIndex = checkColumnIndex(header, "DisplayName", "Name", "Display Name");
						int typeIndex = checkColumnIndex(header, "Type");
						int mobileIndex = checkColumnIndex(header, "Mobile", "Mobile Phone");
						int phoneIndex = checkColumnIndex(header, "Phone");
						int faxIndex = checkColumnIndex(header, "Fax");
						int addressIndex = checkColumnIndex(header, "Address");
						int cityIndex = checkColumnIndex(header, "City");
						int stateIndex = checkColumnIndex(header, "State");
						int postCodeIndex = checkColumnIndex(header, "PostCode");
						int countryIndex = checkColumnIndex(header, "Country");
						int realGstIndex = checkColumnIndex(header, "GST");
						int debtorIndex = checkColumnIndex(header, "Debtor");
						int shareIndex = checkColumnIndex(header, "Shares", "Share", "Ownership", "Share %");
						//fromDate
						int addedDateIndex = checkColumnIndex(header, "AddedDate", "Added Date");
				
						//process file without header
						csvDataList = csvDataList.stream().skip(1).collect(toList());
				
						boolean isAustraliaFormat = isAustraliaFormat(csvDataList, addedDateIndex, "ownership");
				
						for (String line : csvDataList) {
							String[] r = readCsvLine(line);
					
							String horseId = getCsvCellValueAtIndex(r, horseIdIndex);
							String horseName = getCsvCellValueAtIndex(r, horseNameIndex);
							String ownerId = getCsvCellValueAtIndex(r, ownerIdIndex);
							String commsEmail = getCsvCellValueAtIndex(r, commsEmailIndex);
							String financeEmail = getCsvCellValueAtIndex(r, financeEmailIndex);
	
							/*
							 ### **Process case email cell like:
							 Accs: accounts@marshallofbrisbane.com.au Comms:monopoly@bigpond.net.au
							 - [1] Extract Comms to communication email cell.
							 - [2] Extract Accs to financial email cell.
							*/
							Matcher mixingEmailTypeMatcher = Pattern.compile(MIXING_COMMS_FINANCE_EMAIL_PATTERN,
									Pattern.CASE_INSENSITIVE).matcher(line);
							if (mixingEmailTypeMatcher.find()) {
						
								String tryingCommsEmail = mixingEmailTypeMatcher.group(4).trim();
								String tryingFinanceEmail = mixingEmailTypeMatcher.group(2).trim();
								commsEmail = NoteHelper.getValidEmailStr(tryingCommsEmail, line);
						
								if (StringUtils.isEmpty(financeEmail)) {
									financeEmail = NoteHelper.getValidEmailStr(tryingFinanceEmail, line);
								}
							} else {
								commsEmail = NoteHelper.getValidEmailStr(commsEmail, line);
								financeEmail = NoteHelper.getValidEmailStr(financeEmail, line);
							}
					
							String firstName = getCsvCellValueAtIndex(r, firstNameIndex);
							String lastName = getCsvCellValueAtIndex(r, lastNameIndex);
							String displayName = getCsvCellValueAtIndex(r, displayNameIndex);
					
							//TODO : temporarily not required.
							//We have displayName like "Edmonds Racing CT: Toby Edmonds, Logbasex"
							//We wanna extract this name to firstName, lastName, displayName:
							//Any thing before CT is displayName, after is firstName, if after CT contains comma
							// delimiter (,) >> lastName
//							Map<String, String> ownershipNameMap = this.correctOwnershipName(firstName, lastName, displayName,
//									normalNameBuilder, organizationNameBuilder);
//
//							firstName = ownershipNameMap.get("firstName");
//							lastName = ownershipNameMap.get("lastName");
//							displayName = ownershipNameMap.get("displayName");
					
							String type = getCsvCellValueAtIndex(r, typeIndex);
							String mobile = getCsvCellValueAtIndex(r, mobileIndex);
							String phone = getCsvCellValueAtIndex(r, phoneIndex);
							String fax = getCsvCellValueAtIndex(r, faxIndex);
							String address = getCsvCellValueAtIndex(r, addressIndex);
							String city = getCsvCellValueAtIndex(r, cityIndex);
							String state = getCsvCellValueAtIndex(r, stateIndex);
							String postCode = getPostcode(getCsvCellValueAtIndex(r, postCodeIndex));
							String country = getCsvCellValueAtIndex(r, countryIndex);
							String gst = getCsvCellValueAtIndex(r, realGstIndex);
							String share = getCsvCellValueAtIndex(r, shareIndex);
							String debtor = getCsvCellValueAtIndex(r, debtorIndex);
					
							String rawAddedDate = getCsvCellValueAtIndex(r, addedDateIndex);
							//remove all whitespace include unicode character
							rawAddedDate = rawAddedDate.split("\\p{Z}")[0];
							
							//convert addedDate read from CSV to Australia date time format.
							String addedDate = (!isAustraliaFormat && isNotEmpty(rawAddedDate))
									? LocalDate.parse(rawAddedDate, AMERICAN_CUSTOM_DATE_FORMAT).format(AUSTRALIA_FORMAL_DATE_FORMAT)
									: rawAddedDate;
					
							String rowBuilder = String.format("%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s," +
											"%s,%s,%s,%s%n",
									csvValue(horseId),
									csvValue(horseName),
									csvValue(ownerId),
									csvValue(commsEmail),
									csvValue(financeEmail),
									csvValue(firstName),
									csvValue(lastName),
									csvValue(displayName),
									csvValue(type),
									csvValue(mobile),
									csvValue(phone),
									csvValue(fax),
									csvValue(address),
									csvValue(city),
									csvValue(state),
									csvValue(postCode),
									csvValue(country),
									csvValue(gst),
									csvValue(debtor),
									csvValue(share),
									csvValue(addedDate),
									csvValue(exportedDate)
							);
							
							if (StringUtils.isEmpty(
									rowBuilder.replaceAll("[\",\\s]+", "")
							)) continue;
							
							dataBuilder.append(rowBuilder);
						}
					}
			
				} catch (IOException | CustomException e) {
					e.printStackTrace();
				}
			}
			
//			result.put("extractedName", nameBuilder.append(normalNameBuilder).append(organizationNameBuilder));
			result.put("csvData", dataBuilder);
			result.put("exportedDate", exportedDateList);
			return result;
		}
		
		@Override
		public void reformatName(MultipartFile file, String dirName) throws IOException {
			String nameHeader = String.format("%s,%s,%s,%s%n", "RawDisplayName", "Extracted DisplayName", "Extracted " + "FirstName", "Extracted LastName");
			
			StringBuilder nameBuilder = new StringBuilder(nameHeader);
			StringBuilder normalNameBuilder = new StringBuilder("\n***********NORMAL NAME***********\n");
			StringBuilder organizationNameBuilder = new StringBuilder("\n***********ORGANIZATION NAME***********\n");
			
			try {
				List<String> csvData = getCsvData(file);
				if (!isEmpty(csvData)) {
					String[] header = readCsvLine(csvData.get(0));
					
					int firstNameIndex = checkColumnIndex(header, "FirstName", "First Name");
					int lastNameIndex = checkColumnIndex(header, "LastName", "Last Name");
					int displayNameIndex = checkColumnIndex(header, "DisplayName", "Name", "Display Name", "DISPLAY_NAME");
					//process file without header
					csvData = csvData.stream().skip(1).collect(toList());
					
					for (String line : csvData) {
						String[] r = readCsvLine(line);
						
						String firstName = getCsvCellValueAtIndex(r, firstNameIndex);
						String lastName = getCsvCellValueAtIndex(r, lastNameIndex);
						String displayName = getCsvCellValueAtIndex(r, displayNameIndex);
						
						//We have displayName like "Edmonds Racing CT: Toby Edmonds, Logbasex"
						//We wanna extract this name to firstName, lastName, displayName:
						//Any thing before CT is displayName, after is firstName, if after CT contains comma
						// delimiter (,) >> lastName
						this.correctOwnershipNameWithoutCT(firstName, lastName, displayName,
								normalNameBuilder, organizationNameBuilder);
					}
				}
				
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			nameBuilder.append(normalNameBuilder).append(organizationNameBuilder);
			String path = getOutputFolder(dirName) + File.separator + "formatted-name.csv";
			Files.write(Paths.get(path), nameBuilder.toString().getBytes());
		}
		
		@Override
		public Map<Object, Object> reformatOwnership(MultipartFile file, String dirName) {
			String ownershipHeader = String.format("%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s%n",
					"HorseId", "HorseName",
					"OwnerID", "CommsEmail", "FinanceEmail", "FirstName", "LastName", "DisplayName",
					"Type", "Mobile", "Phone", "Fax", "Address", "City", "State", "PostCode",
					"Country", "GST", "Debtor", "Shares", "FromDate", "ToDate"
			);
			
			StringBuilder dataBuilder = new StringBuilder(ownershipHeader);
			Map<Object, Object> result = new HashMap<>();
			
			try {
				List<String> csvData = getCsvData(file);
				if (!isEmpty(csvData)) {
					
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
					// DEBTOR
					// -------------------------
					// BALANCE (SHARE PERCENTAGE)
					// FROM_DATE
					// TO_DATE
					// EXPORTED_DATE
					// -------------------------------------------------------------
					
					String[] header = readCsvLine(csvData.get(0));
					
					int horseIdIndex = checkColumnIndex(header, "Horse Id");
					int horseNameIndex = checkColumnIndex(header, "Horse Name", "Horse", "Horses");
					int ownerIdIndex = checkColumnIndex(header, "Owner Id");
					int commsEmailIndex = checkColumnIndex(header, "CommsEmail", "Email");
					int financeEmailIndex = checkColumnIndex(header, "Finance Email", "FinanceEmail","Finance_Email");
					int firstNameIndex = checkColumnIndex(header, "FirstName", "First Name");
					int lastNameIndex = checkColumnIndex(header, "LastName", "Last Name");
					int displayNameIndex = checkColumnIndex(header, "DisplayName", "Name", "Display Name");
					int typeIndex = checkColumnIndex(header, "Type");
					int mobileIndex = checkColumnIndex(header, "Mobile", "Mobile Phone");
					int phoneIndex = checkColumnIndex(header, "Phone");
					int faxIndex = checkColumnIndex(header, "Fax");
					int addressIndex = checkColumnIndex(header, "Address");
					int cityIndex = checkColumnIndex(header, "City","Suburb");
					int stateIndex = checkColumnIndex(header, "State");
					int postCodeIndex = checkColumnIndex(header, "PostCode");
					int countryIndex = checkColumnIndex(header, "Country");
					int realGstIndex = checkColumnIndex(header, "GST", "GST Reg.");
					int debtorIndex = checkColumnIndex(header, "Debtor");
					int shareIndex = checkColumnIndex(header, "Shares", "Share", "Ownership", "Share %","percentage");
					//fromDate
					int fromDateIndex = checkColumnIndex(header, "Purchased Date", "Date","fromDate");
					
					//process file without header
					csvData = csvData.stream().skip(1).collect(toList());
					
					boolean isAustraliaFormat = isAustraliaFormat(csvData, fromDateIndex, "ownership");
					
					for (String line : csvData) {
						String[] r = readCsvLine(line);
						
						String horseId = getCsvCellValueAtIndex(r, horseIdIndex);
						String horseName = getCsvCellValueAtIndex(r, horseNameIndex);
						String ownerId = getCsvCellValueAtIndex(r, ownerIdIndex);
						String commsEmail = getCsvCellValueAtIndex(r, commsEmailIndex);
						String financeEmail = getCsvCellValueAtIndex(r, financeEmailIndex);
						String firstName = getCsvCellValueAtIndex(r, firstNameIndex);
						String lastName = getCsvCellValueAtIndex(r, lastNameIndex);
						String displayName = getCsvCellValueAtIndex(r, displayNameIndex);
						String type = getCsvCellValueAtIndex(r, typeIndex);
						String mobile = getCsvCellValueAtIndex(r, mobileIndex);
						String phone = getCsvCellValueAtIndex(r, phoneIndex);
						String fax = getCsvCellValueAtIndex(r, faxIndex);
						String address = getCsvCellValueAtIndex(r, addressIndex);
						String city = getCsvCellValueAtIndex(r, cityIndex);
						String state = getCsvCellValueAtIndex(r, stateIndex);
						String postCode = getPostcode(getCsvCellValueAtIndex(r, postCodeIndex));
						String country = getCsvCellValueAtIndex(r, countryIndex);
						String gst = getCsvCellValueAtIndex(r, realGstIndex);
						String share = getCsvCellValueAtIndex(r, shareIndex);
						share = share.replaceAll(REMOVE_INVALID_SHARES_PATTERN, "0.00%");
						String debtor = getCsvCellValueAtIndex(r, debtorIndex);
						
						String rawAddedDate = getCsvCellValueAtIndex(r, fromDateIndex);
						//remove all whitespace include unicode character
						rawAddedDate = rawAddedDate.split("\\p{Z}")[0];
						
						//convert addedDate read from CSV to Australia date time format.
						String addedDate = (!isAustraliaFormat && isNotEmpty(rawAddedDate)) ? LocalDate.parse(rawAddedDate, AMERICAN_CUSTOM_DATE_FORMAT).format(AUSTRALIA_FORMAL_DATE_FORMAT) : rawAddedDate;
						
						String rowBuilder = String.format("%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s%n",
								csvValue(horseId),
								csvValue(horseName),
								csvValue(ownerId),
								csvValue(commsEmail),
								csvValue(financeEmail),
								csvValue(firstName),
								csvValue(lastName),
								csvValue(displayName),
								csvValue(type),
								csvValue(mobile),
								csvValue(phone),
								csvValue(fax),
								csvValue(address),
								csvValue(city),
								csvValue(state),
								csvValue(postCode),
								csvValue(country),
								csvValue(gst),
								csvValue(debtor),
								csvValue(share),
								csvValue(addedDate),
								csvValue(""));
						
						dataBuilder.append(rowBuilder);
					}
					
					String path = getOutputFolder(dirName) + File.separator + "formatted-ownership.csv";
					Files.write(Paths.get(path), dataBuilder.toString().getBytes());
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			result.put("exportedDate", "16/07/2020");
			return result;
		}
		
		@Override
		public Object transformMultipartFile(MultipartFile multipartFile) throws IOException {
			//https://stackoverflow.com/questions/24339990/how-to-convert-a-multipart-file-to-file/39572293
			File convFile = new File(System.getProperty("java.io.tmpdir") + File.separator + "test.csv");
			multipartFile.transferTo(convFile);
			return convFile.getAbsolutePath();
			
			//another idea: write byte to file by get bytes from multipart file.
		}
		
		private void correctOwnershipNameWithoutCT(String firstName,
		                                                          String lastName,
		                                                          String displayName,
		                                                          StringBuilder normalNameBuilder,
		                                                          StringBuilder organizationNameBuilder) {
			
			String formattedDisplayName = null;
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
			
			boolean isOrganizationName =
					organizationNames.stream().anyMatch(name -> displayName.toLowerCase().contains(name.toLowerCase()));
			
			//We have displayName like "Edmonds Racing CT: Toby Edmonds, Logbasex"
			//We wanna extract this name to firstName, lastName, displayName:
			//Any thing before CT is displayName, after is firstName, if after CT contains comma delimiter (,) >> lastName
			if (!isOrganizationName) {
				if (StringUtils.isEmpty(firstName) && StringUtils.isEmpty(lastName)) {
					
					String[] firstAndLastNameArr = displayName.split("\\p{Z}");
					if (firstAndLastNameArr.length > 1) {
						lastName = Arrays.stream(firstAndLastNameArr).reduce((first, second) -> second)
								.orElse("");
						
						String finalLastName = lastName;
						firstName = Arrays.stream(firstAndLastNameArr)
								.filter(i -> !i.equalsIgnoreCase(finalLastName))
								.collect(joining(SPACE)).trim();
						formattedDisplayName = lastName + ", " + firstName;
					}
					
					String extractedName = String.format("%s,%s,%s,%s%n",
							csvValue(displayName),
							csvValue(formattedDisplayName),
							csvValue(firstName),
							csvValue(lastName)
					);
					normalNameBuilder.append(extractedName);
				}
			}
		}
		
		//https://stackoverflow.com/questions/27880505/lambda-expression-in-iterable-implementation
		private static Iterable<MatchResult> allMatches(final Pattern p, final CharSequence input) {
			return () -> new Iterator<MatchResult>() {
				// Use a matcher internally.
				final Matcher matcher = p.matcher(input);
				// Keep a match around that supports any interleaving of hasNext/next calls.
				MatchResult pending;
				
				public boolean hasNext() {
					// Lazily fill pending, and avoid calling find() multiple times if the
					// clients call hasNext() repeatedly before sampling via next().
					if (pending == null && matcher.find()) {
						pending = matcher.toMatchResult();
					}
					return pending != null;
				}
				
				public MatchResult next() {
					// Fill pending if necessary (as when clients call next() without
					// checking hasNext()), throw if not possible.
					if (!hasNext()) {
						throw new NoSuchElementException();
					}
					// Consume pending so next call to hasNext() does a find().
					MatchResult next = pending;
					pending = null;
					return next;
				}
			};
		}
		
		private Map<String, String> correctOwnershipName(String firstName, String lastName,String displayName, StringBuilder normalNameBuilder,
											StringBuilder organizationNameBuilder) {
			
			Map<String, String> ownershipNameMap = new HashMap<>();
			
			String formattedDisplayName = null;
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
		
			Matcher ctMatcher = Pattern.compile(CT_IN_DISPLAY_NAME_PATTERN, Pattern.CASE_INSENSITIVE).matcher(displayName);
			boolean isOrganizationName =
					organizationNames.stream().anyMatch(name -> displayName.toLowerCase().contains(name.toLowerCase()));
		
			//We have displayName like "Edmonds Racing CT: Toby Edmonds, Logbasex"
			//We wanna extract this name to firstName, lastName, displayName:
			//Any thing before CT is displayName, after is firstName, if after CT contains comma delimiter (,) >> lastName
			if (ctMatcher.find()) {
				if (StringUtils.isEmpty(firstName) && StringUtils.isEmpty(lastName)) {
					int ctStartedIndex = ctMatcher.start();
					int ctEndIndex = ctMatcher.end();
				
					//E.g: Edmonds Racing
					//for case displayName contains organizationName. Ex: Michael Hickmott Bloodstock CT: Michael Hickmott;
					// >> Convert to format: Michael Hickmott(after CT) - Michael Hickmott Bloodstock(before CT)
					formattedDisplayName = displayName.substring(0, ctStartedIndex).trim();
					
					//E.g: Toby Edmonds, Logbasex
					String firstAndLastNameStr = displayName.substring(ctEndIndex).trim();
		
					String finalRealDisplayName = formattedDisplayName;
					boolean isContainsOrganizationName = organizationNames.stream()
							.anyMatch(name -> finalRealDisplayName.toLowerCase().contains(name.toLowerCase()));
					
					if (isContainsOrganizationName) {
						formattedDisplayName = String.join(" - ", firstAndLastNameStr, finalRealDisplayName);
					}
					String[] firstAndLastNameArr = firstAndLastNameStr.split("\\p{Z}");
					if (firstAndLastNameArr.length > 1) {
						lastName = Arrays.stream(firstAndLastNameArr).reduce((first, second) -> second)
								.orElse("");
					
						String finalLastName = lastName;
						firstName = Arrays.stream(firstAndLastNameArr)
								.filter(i -> !i.equalsIgnoreCase(finalLastName))
								.collect(joining(SPACE)).trim();
					}
				
					String extractedName = String.format("%s,%s,%s,%s%n",
							csvValue(displayName),
							csvValue(formattedDisplayName),
							csvValue(firstName),
							csvValue(lastName)
					);
					normalNameBuilder.append(extractedName);
				}
			
				//case don't include CT in name.
				ctMatcher.reset();
			} else if (!ctMatcher.find() && isOrganizationName) {
				//if displayName is organization name >> keep it intact.
				formattedDisplayName = displayName;
				firstName = EMPTY;
				lastName = EMPTY;
			
				String extractedName = String.format("%s,%s,%s,%s\n",
						csvValue(displayName),
						csvValue(formattedDisplayName),
						csvValue(firstName),
						csvValue(lastName)
				);
				organizationNameBuilder.append(extractedName);
			} else {
				formattedDisplayName = displayName;
			}
			
			ownershipNameMap.put("firstName", firstName);
			ownershipNameMap.put("lastName", lastName);
			ownershipNameMap.put("displayName", formattedDisplayName);
			return ownershipNameMap;
		}
		
		private static String getParamValue(String link, String paramName) throws URISyntaxException {
			List<NameValuePair> queryParams = new URIBuilder(link).getQueryParams();
			return queryParams.stream()
					.filter(param -> param.getName().equalsIgnoreCase(paramName))
					.map(NameValuePair::getValue)
					.findFirst()
					.orElse("");
		}
		
		
		//https://stackoverflow.com/questions/2843366/how-to-add-new-elements-to-an-array
		public static <T> T[] append(T[] arr, T element) {
			final int N = arr.length;
			arr = Arrays.copyOf(arr, N + 1);
			arr[N] = element;
			return arr;
		}
		
		public class EGiftResponse {
			@JsonProperty(value = "StatusCode")
			private Integer statusCode;
			
			@JsonProperty(value = "GiftID")
			private Integer giftId;
			
			@JsonProperty(value = "Message")
			private String message;
			
			@JsonProperty(value = "Expiry")
			@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy/MM/dd")
			@JsonDeserialize(using = LocalDateDeserializer.class)
			@JsonSerialize(using = LocalDateSerializer.class)
			private LocalDate expiryDate;
			
			public EGiftResponse() {
			
			}
			
			public EGiftResponse(Integer statusCode, Integer giftId, String message, LocalDate expiryDate) {
				this.statusCode = statusCode;
				this.giftId = giftId;
				this.message = message;
				this.expiryDate = expiryDate;
			}
			
			public Integer getStatusCode() {
				return statusCode;
			}
			
			public void setStatusCode(Integer statusCode) {
				this.statusCode = statusCode;
			}
			
			public Integer getGiftId() {
				return giftId;
			}
			
			public void setGiftId(Integer giftId) {
				this.giftId = giftId;
			}
			
			public String getMessage() {
				return message;
			}
			
			public void setMessage(String message) {
				this.message = message;
			}
			
			public LocalDate getExpiryDate() {
				return expiryDate;
			}
			
			public void setExpiryDate(LocalDate expiryDate) {
				this.expiryDate = expiryDate;
			}
		}
		
		public static void main(String[] args) {
			System.out.println(System.getProperty("line.separator"));
			
			final String uri = "https://sandbox.express.giftpay.com/api/gift.svc/send";
			UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(uri)
					.queryParam("key", "920237C7-EF66-4859-9633-5BF055EE9AA8")
					.queryParam("to", "contact.hoducanh@gmail.com")
					.queryParam("value", "5")
					.queryParam("clientref", 99)
					.queryParam("message", "");
			
			RestTemplate restTemplate = new RestTemplate();
			restTemplate.getMessageConverters()
					.add(0, createMappingJacksonHttpMessageConverter());
			
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);
			
			HttpEntity<String> entity = new HttpEntity<>(headers);
			ResponseEntity<EGiftResponse> response = restTemplate.exchange(builder.toUriString(), HttpMethod.GET,
					entity, EGiftResponse.class);
			
			System.out.println(response.getBody());
		}
		
		private static ObjectMapper createObjectMapper() {
			ObjectMapper objectMapper = new ObjectMapper();
//			objectMapper.configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true);
			objectMapper.registerModule(new JavaTimeModule());
			objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
			
			return objectMapper;
		}
		
		private static MappingJackson2HttpMessageConverter createMappingJacksonHttpMessageConverter() {
			
			MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
			converter.setObjectMapper(createObjectMapper());
			return converter;
		}
	}