	package com.tellyouiam.alittlebitaboutspring.service.note;
	
	import com.google.common.collect.Multimap;
	import com.tellyouiam.alittlebitaboutspring.exception.CustomException;
	import com.tellyouiam.alittlebitaboutspring.utils.*;
	import org.apache.commons.collections4.SetUtils;
	import org.apache.commons.lang3.ArrayUtils;
	import org.apache.commons.lang3.StringUtils;
	import org.apache.commons.lang3.math.NumberUtils;
	import org.apache.http.NameValuePair;
	import org.apache.http.client.utils.URIBuilder;
	import org.slf4j.Logger;
	import org.slf4j.LoggerFactory;
	import org.springframework.stereotype.Service;
	import org.springframework.util.CollectionUtils;
	import org.springframework.web.multipart.MultipartFile;
	
	import java.io.*;
	import java.net.URISyntaxException;
	import java.nio.file.Files;
	import java.nio.file.Path;
	import java.nio.file.Paths;
	import java.time.LocalDate;
	import java.time.format.DateTimeFormatter;
	import java.time.format.DateTimeFormatterBuilder;
	import java.time.format.DateTimeParseException;
	import java.time.format.ResolverStyle;
	import java.time.format.SignStyle;
	import java.util.*;
	import java.util.AbstractMap.SimpleImmutableEntry;
	import java.util.concurrent.atomic.AtomicInteger;
	import java.util.function.BinaryOperator;
	import java.util.function.Function;
	import java.util.function.Predicate;
	import java.util.regex.MatchResult;
	import java.util.regex.Matcher;
	import java.util.regex.Pattern;
	import java.util.stream.Collectors;
	import java.util.stream.IntStream;
	import java.util.stream.Stream;
	
	import static com.tellyouiam.alittlebitaboutspring.utils.OnboardHelper.*;
	import static com.tellyouiam.alittlebitaboutspring.utils.StringHelper.*;
	import static java.time.temporal.ChronoField.*;
	import static java.util.Collections.*;
	import static java.util.Objects.*;
	import static java.util.stream.Collectors.*;
	import static org.apache.commons.lang3.StringUtils.*;
	import static org.springframework.util.CollectionUtils.isEmpty;
	
	@Service
	public class NoteServiceImpl implements NoteService {
	
		private static final Logger logger = LoggerFactory.getLogger(NoteServiceImpl.class);
	
		private static final String HORSE_FILE_HEADER = String.format("%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s%n",
				"OwnerID", "Email", "FinanceEmail", "FirstName", "LastName", "DisplayName", "Type",
				"Mobile", "Phone", "Fax", "Address", "City", "State", "PostCode", "Country", "GST");
	
		private int checkColumnIndex(String[] arr, String... valuesToCheck) {
			int index;
			for (String element : arr) {
				for (String value : valuesToCheck) {
					String formattedElement = element.replace("\"", "").trim();
					if (formattedElement.equalsIgnoreCase(value)) {
						index = Arrays.asList(arr).indexOf(element);
						return index;
					}
				}
			}
			return -1;
		}
	
		private List<String> getCsvData(MultipartFile multipart) throws IOException {
			InputStream is = multipart.getInputStream();
			BufferedReader br = new BufferedReader(new InputStreamReader(is));
			return this.getCsvData(br, false);
		}
	
		private List<String> getCsvData(BufferedReader bufReader, boolean ignoreHeader) throws IOException {
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
	
		private boolean isRecognizedAsValidDate(String dateStr) {
			return isDMYFormat(dateStr) || isMDYFormat(dateStr);
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
	
						lines.add(readCsvLine(line));
					}
				}
				return lines.toArray(new String[lines.size()][]);
			}
		}
	
		private String getOutputFolder(String dirName) {
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
	
		//https://stackoverflow.com/questions/86780/how-to-check-if-a-string-contains-another-string-in-a-case-insensitive-manner-in
		private boolean containsIgnoreCase(String container, String what) {
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
		
		private boolean stringContainsIgnoreCase(String container, String what, String delimiter) {
			return Stream.of(container.split(delimiter)).anyMatch(i -> i.trim().equalsIgnoreCase(what));
		}
		
		private static final String HORSE_RECORDS_PATTERN = "([\\d]+)\\sRecords"; //like: 162 records
		
		@Override
		public Object automateImportOwner(MultipartFile ownerFile, String dirName) throws CustomException {
			try {
				List<String> csvData = this.getCsvData(ownerFile);
				List<String> preparedData = new ArrayList<>();
				StringBuilder builder = new StringBuilder();
				
				String ownerErrorData = StringUtils.EMPTY;
				
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
					int gstIndex = checkColumnIndex(header, "GST");
					
					builder.append(HORSE_FILE_HEADER);
					
					csvData = csvData.stream().skip(1).collect(toList());
					
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
						
						String ownerId = getCsvCellValue(r, ownerIdIndex);
						String email = getCsvCellValue(r, emailIndex);
						String financeEmail = getCsvCellValue(r, financeEmailIndex);
						String firstName = getCsvCellValue(r, firstNameIndex);
						String lastName = getCsvCellValue(r, lastNameIndex);
						String displayName = getCsvCellValue(r, displayNameIndex);
						String type = getCsvCellValue(r, typeIndex);
						
						String mobile = getCsvCellValue(r, mobileIndex);
						
						String phone = getCsvCellValue(r, phoneIndex);
						
						String fax = getCsvCellValue(r, faxIndex);
						String address = getCsvCellValue(r, addressIndex);
						
						String city = getCsvCellValue(r, cityIndex);
						String state = getCsvCellValue(r, stateIndex);
						String postCode = getPostcode(getCsvCellValue(r, postCodeIndex));
						String country = getCsvCellValue(r, countryIndex);
						String gst = getCsvCellValue(r, gstIndex);
						
						String rowBuilder = String.format("%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s%n",
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
								csvValue(gst)
						);
						preparedData.add(rowBuilder);
						
						builder.append(rowBuilder);
					}
					
					ownerErrorData = CsvHelper.validateInputFile(preparedData);
				}
				
				String errorDataPath = getOutputFolder(dirName) + File.separator + "owner-input-error.csv";
				FileHelper.writeDataToFile(errorDataPath, ownerErrorData.getBytes());
				
				String path = getOutputFolder(dirName) + File.separator + "formatted-owner.csv";
				FileHelper.writeDataToFile(path, builder.toString().getBytes());
				
				return builder;
			} catch (IOException e) {
				e.printStackTrace();
			}
			return null;
		}
		
		@Override
		public Object formatOwnerV2(MultipartFile ownerFile, String dirName) throws IOException {
			//InputStream inputStream = new BufferedInputStream(ownerFile.getInputStream());
			List<String> csvData = this.getCsvData(ownerFile);
			StringBuilder streamBuilder = new StringBuilder();
			
			if (!isEmpty(csvData)) {
				Predicate<String> isEmptyRowCsv = row -> (row.matches("^(,+)$"));
				Predicate<String> isFooterRow = row -> (row.matches("(.+)([\\d]+)\\sRecords(.+)"));
				
				//filter non-data row.
				csvData = csvData.stream()
						.filter(StringUtils::isNotEmpty)
						.filter(isEmptyRowCsv.negate())
						.filter(isFooterRow.negate())
						.collect(toList());
				
				//find line is header
				Predicate<String> isLineContainHeader = line -> Stream.of("Name", "Address", "Phone", "Mobile")
						.anyMatch(headerElement -> containsIgnoreCase(line, headerElement));
				
				String headerLine = csvData.stream().filter(isLineContainHeader).findFirst().orElse("");
				int headerLineNum = csvData.indexOf(headerLine);
				csvData = csvData.stream().skip(headerLineNum).collect(toList());
				
				List<String> finalCsvData = csvData;
				String[][] data = csvData.stream().map(OnboardHelper::readCsvLine).toArray(String[][]::new);
				int rowLength = Arrays.stream(data).max(Comparator.comparingInt(ArrayUtils::getLength))
						.orElseThrow(IllegalAccessError::new).length;
				
				Function<Integer, Map.Entry<Integer, List<String>>> colAccumulatorMapper = index -> {
					//value of cell in row based on its index in row.
					//split csv by the comma using java algorithm is damn fast. Faster a thousand times than regex.
					Function<String, String> valueRowIndexMapper = line -> {
						String[] rowArr = customSplitSpecific(line).toArray(new String[0]);
						return getCsvCellValue(rowArr, index);
					};
			
					return new SimpleImmutableEntry<>(
						index, finalCsvData.stream().map(valueRowIndexMapper).collect(toList())
					);
				};
				
				//col has owner data is col has header and has at least two different cell value.
				Predicate<Map.Entry<Integer, List<String>>> colHasOwnerData = colEntry ->
						(colEntry.getValue().stream().distinct().count() > 2 || isNotEmpty(colEntry.getValue().get(0)));
				
				List<Map.Entry<Integer, List<String>>> columnEntries = Stream.iterate(0, n -> n + 1).limit(rowLength)
						.map(colAccumulatorMapper)
						.filter(colHasOwnerData)
						.collect(toList());
				
				//process for case header and data in separate col in csv.
				Map<Integer, List<String>> formattedDataMap = new LinkedHashMap<>();
				for (Map.Entry<Integer, List<String>> entry : columnEntries) {
					Integer entryKey = entry.getKey();
					List<String> entryValue = entry.getValue();
					
					if ((columnEntries.indexOf(entry) >= columnEntries.size() - 1) || (columnEntries.indexOf(entry) == 0)) {
						formattedDataMap.put(entryKey, entryValue);
						continue;
					}
					
					Map.Entry<Integer, List<String>> nextEntry = columnEntries.get(columnEntries.indexOf(entry) + 1);
					Integer nextEntryKey = nextEntry.getKey();
					List<String> nextEntryValue = nextEntry.getValue();
					
					Optional<Map.Entry<Integer, List<String>>> previousEntry =
							Optional.ofNullable(columnEntries.get(columnEntries.indexOf(entry) - 1));
					boolean isHavePreviousKey = previousEntry.isPresent() && (entryKey.equals(previousEntry.get().getKey() + 1));
					boolean isConsecutive = entryKey.equals(nextEntryKey - 1);
					
					//join two consecutive column in csv, one has header missing body, one has body missing header.
					if (isConsecutive && !isHavePreviousKey
							&& columnEntries.indexOf(entry) != 0
							&& isNotEmpty(entryValue.get(0))
							&& entryValue.stream().distinct().count() < 3
							&& StringUtils.isEmpty(nextEntryValue.get(0))
							&& nextEntryValue.stream().distinct().count() > 2) {
						
						List<String> mergedEntryValue = Stream.iterate(0, i -> i + 1).limit(entryValue.size())
								.map(i -> entryValue.get(i).concat(nextEntryValue.get(i)).trim())
								.collect(toList());
						
						formattedDataMap.put(nextEntryKey, mergedEntryValue);
					} else {
						formattedDataMap.putIfAbsent(entryKey, entryValue);
					}
				}
				
				//first cell in column as header
				Map<String, List<String>> csvDataHeaderAsKey = formattedDataMap.entrySet().stream()
						.filter(e-> isNotEmpty(e.getValue().get(0)))
						.collect(toMap(e -> e.getValue().get(0), Map.Entry::getValue));
				
//				List<String> headerList = Arrays.asList("Address", "Suburb", "State", "PostCode", "Country");
//				List<List<String>> splitAddressList = csvDataHeaderAsKey.get("Address").stream().skip(1).map(rawAddress -> {
//					Map<String, String> splitAddress = OwnerSplitAddress.splitAddress(rawAddress);
//					String address = Optional.ofNullable(splitAddress.get("address")).orElse("");
//					String suburb = Optional.ofNullable(splitAddress.get("suburb")).orElse("");
//					String state = Optional.ofNullable(splitAddress.get("state")).orElse("");
//					String postcode = Optional.ofNullable(splitAddress.get("postcode")).orElse("");
//					String country = Optional.ofNullable(splitAddress.get("country")).orElse("");
//					return Arrays.asList(address, suburb, state, postcode, country);
//				}).collect(toList());
//
//				//Add header
//				splitAddressList.add(0, headerList);
//
//				List<List<String>> transposedAddressList = IntStream.range(0, splitAddressList.get(0).size())
//						.mapToObj(i -> splitAddressList.stream().map(l -> l.get(i))
//								.collect(toList())
//						).collect(toList());
//
//				Map<String, List<String>> addressMap = transposedAddressList.stream()
//						.collect(toMap(i -> i.get(0), u -> u));
//
//				long distinctPostCode = csvDataHeaderAsKey.getOrDefault("PostCode", singletonList("")).stream().distinct().count();
//				BinaryOperator<List<String>> mergeFunction = (newValue, oldValue) -> distinctPostCode == 1 ? oldValue : newValue;
//				csvDataHeaderAsKey = Stream.of(csvDataHeaderAsKey, addressMap).flatMap(map -> map.entrySet().stream())
//						.collect(toMap(Map.Entry::getKey, Map.Entry::getValue, mergeFunction));
//
				Map<String, List<String>> standardHeaderMap = new LinkedHashMap<>();
				
				standardHeaderMap.put("OwnerID", singletonList("OwnerID"));
				standardHeaderMap.put("Email", singletonList("Email"));
				standardHeaderMap.put("FinanceEmail", singletonList("FinanceEmail"));
				standardHeaderMap.put("FirstName, First Name", singletonList("FirstName"));
				standardHeaderMap.put("LastName, Last Name", singletonList("LastName"));
				standardHeaderMap.put("DisplayName, Name, Display Name", singletonList("DisplayName"));
				standardHeaderMap.put("Type", singletonList("Type"));
				standardHeaderMap.put("Mobile, Mobile Phone", singletonList("Mobile"));
				standardHeaderMap.put("Phone", singletonList("Phone"));
				standardHeaderMap.put("Fax", singletonList("Fax"));
				standardHeaderMap.put("Address", singletonList("Address"));
				standardHeaderMap.put("City", singletonList("City"));
				standardHeaderMap.put("State", singletonList("State"));
				standardHeaderMap.put("PostCode", singletonList("PostCode"));
				standardHeaderMap.put("Country", singletonList("Country"));
				standardHeaderMap.put("GST", singletonList("GST"));
				standardHeaderMap.put("Debtor", singletonList("Debtor"));
				
				int maxMapValueSize = csvDataHeaderAsKey.values().stream().map(List::size)
						.max(Comparator.comparingInt(i -> i)).orElseThrow(IllegalArgumentException::new);
				
				Map<String, List<String>> finalCsvDataHeaderAsKey = csvDataHeaderAsKey;
				Map<String, List<String>> ownerDataMap = standardHeaderMap.keySet().stream().map(standardHeader -> {
					
					Predicate<String> csvHeaderMatchStandardHeader = csvHeader ->
							this.stringContainsIgnoreCase(standardHeader, csvHeader, ",");
					
					Optional<String> matchHeaderKey = finalCsvDataHeaderAsKey.keySet().stream()
							.filter(csvHeaderMatchStandardHeader).findFirst();
					
					String standardKey = standardHeaderMap.get(standardHeader).get(0);
					List<String> filledColMissingData = Stream
							.of(standardHeaderMap.get(standardHeader), Collections.nCopies(maxMapValueSize - 1, ""))
							.flatMap(Collection::stream).collect(toList());
					return matchHeaderKey
							.map(key -> new SimpleImmutableEntry<>(standardKey, finalCsvDataHeaderAsKey.get(key)))
							.orElseGet(() -> new SimpleImmutableEntry<>(standardKey, filledColMissingData));
				}).collect(toMap(Map.Entry::getKey, Map.Entry::getValue, (m, n) -> m, LinkedHashMap::new));
				
				//https://stackoverflow.com/questions/2941997/how-to-transpose-listlist
				List<Iterator<String>> iteratorDataList = new ArrayList<>(ownerDataMap.values()).stream()
						.map(List::iterator).collect(toList());
				
				List<List<String>> transposeList = IntStream.range(0, maxMapValueSize)
						.mapToObj(
								n -> iteratorDataList.stream().filter(Iterator::hasNext)
								.map(Iterator::next).collect(toList())
						).collect(toList());
				
				//filter line only contains " (quotes) and ,(comma)
				Predicate<StringJoiner> validDataLine = line -> isNotEmpty(
						line.toString().chars().distinct()
						.mapToObj(c -> String.valueOf((char) c))
						.collect(joining())
						.replace("\"", "")
						.replace(",", "")
				);
				
				//https://stackoverflow.com/questions/48672931/efficiently-joining-text-in-nested-lists
				String allLiner = transposeList.stream()
						.map(l -> l.stream()
								.map(StringHelper::csvValue)
								.collect(() -> new StringJoiner(","), StringJoiner::add, StringJoiner::merge)
						).filter(validDataLine)
						.collect(() -> new StringJoiner("\n"), StringJoiner::merge, StringJoiner::merge).toString();
				
				streamBuilder.append(allLiner);
				String path = getOutputFolder(dirName) + File.separator + "formatted-owner.csv";
				FileHelper.writeDataToFile(path, streamBuilder.toString().getBytes());
				return streamBuilder;
			}
			return null;
		}
		
		@Override
		public Object formatHorseV2(MultipartFile horseFile, String dirName) throws IOException {
			List<String> csvData = this.getCsvData(horseFile);
			StringBuilder streamBuilder = new StringBuilder();
			
			if (!isEmpty(csvData)) {
				Predicate<String> isEmptyRowCsv = row -> (row.matches("^(,+)$"));
				Predicate<String> isFooterRow = row -> (row.matches("(.+)([\\d]+)\\sRecords(.+)"));
				Predicate<String> nonDataRow = row -> (row.matches("^,([^,]+),(.+)((\\w|\\d)+)(.+)$"));
				
				//filter non-data row.
				csvData = csvData.stream()
						.filter(StringUtils::isNotEmpty)
						.filter(isEmptyRowCsv.negate())
						.filter(isFooterRow.negate())
						.filter(nonDataRow)
						.collect(toList());
				
				List<String> finalCsvData = csvData;
				String[][] data = csvData.stream().map(OnboardHelper::readCsvLine).toArray(String[][]::new);
				int rowLength = Arrays.stream(data).max(Comparator.comparingInt(ArrayUtils::getLength))
						.orElseThrow(IllegalAccessError::new).length;
				
				Function<Integer, Map.Entry<Integer, List<String>>> colAccumulatorMapper = index -> {
					//value of cell in row based on its index in row.
					//split csv by the comma using java algorithm is damn fast. Faster a thousand times than regex.
					Function<String, String> valueRowIndexMapper = line -> {
						String[] rowArr = customSplitSpecific(line).toArray(new String[0]);
						return getCsvCellValue(rowArr, index);
					};
					
					return new SimpleImmutableEntry<>(
							index, finalCsvData.stream().map(valueRowIndexMapper).collect(toList())
					);
				};
				
				//col has owner data is col has header and has at least two different cell value.
				Predicate<Map.Entry<Integer, List<String>>> colHasOwnerData = colEntry ->
						(colEntry.getValue().stream().distinct().count() > 2 || isNotEmpty(colEntry.getValue().get(0)));
				
				List<Map.Entry<Integer, List<String>>> columnEntries = Stream.iterate(0, n -> n + 1).limit(rowLength)
						.map(colAccumulatorMapper)
						.filter(colHasOwnerData)
						.collect(toList());
				
				//process for case header and data in separate col in csv.
				Map<Integer, List<String>> formattedDataMap = new LinkedHashMap<>();
				for (Map.Entry<Integer, List<String>> entry : columnEntries) {
					Integer entryKey = entry.getKey();
					List<String> entryValue = entry.getValue();
					
					if ((columnEntries.indexOf(entry) >= columnEntries.size() - 1) || (columnEntries.indexOf(entry) == 0)) {
						formattedDataMap.put(entryKey, entryValue);
						continue;
					}
					
					Map.Entry<Integer, List<String>> nextEntry = columnEntries.get(columnEntries.indexOf(entry) + 1);
					Integer nextEntryKey = nextEntry.getKey();
					List<String> nextEntryValue = nextEntry.getValue();
					
					Optional<Map.Entry<Integer, List<String>>> previousEntry =
							Optional.ofNullable(columnEntries.get(columnEntries.indexOf(entry) - 1));
					boolean isHavePreviousKey = previousEntry.isPresent() && (entryKey.equals(previousEntry.get().getKey() + 1));
					boolean isConsecutive = entryKey.equals(nextEntryKey - 1);
					
					//join two consecutive column in csv, one has header missing body, one has body missing header.
					if (isConsecutive && !isHavePreviousKey
							&& columnEntries.indexOf(entry) != 0
							&& isNotEmpty(entryValue.get(0))
							&& entryValue.stream().distinct().count() < 3
							&& StringUtils.isEmpty(nextEntryValue.get(0))
							&& nextEntryValue.stream().distinct().count() > 2) {
						
						List<String> mergedEntryValue = Stream.iterate(0, i -> i + 1).limit(entryValue.size())
								.map(i -> entryValue.get(i).concat(nextEntryValue.get(i)).trim())
								.collect(toList());
						
						formattedDataMap.put(nextEntryKey, mergedEntryValue);
					} else {
						formattedDataMap.putIfAbsent(entryKey, entryValue);
					}
				}
				
				//if column is not date column >> retain as raw, if column is date column but already is DMY format >> retain as raw
				//else >> reformat
				Predicate<String> containsDate = value -> value.matches("^\\d{1,2}/\\d{1,2}/\\d{1,4}$");
				Function<Map.Entry<Integer, List<String>>, List<String>> australiaDateMapping = entry ->
						(!isAustraliaFormatV2(entry.getValue()) && entry.getValue().stream().noneMatch(containsDate))
								? entry.getValue()
								: isAustraliaFormatV2(entry.getValue())
								? entry.getValue()
								: entry.getValue().stream().map(rawDate -> {
									if (isNotEmpty(rawDate) && (isMDYFormat(rawDate) || isDMYFormat(rawDate))) {
										return LocalDate.parse(rawDate, AMERICAN_CUSTOM_DATE_FORMAT).format(AUSTRALIA_FORMAL_DATE_FORMAT);
									}
									return rawDate;
								}).collect(toList());
				
				Map<String, List<String>> csvDataHeaderAsKey = formattedDataMap.entrySet().stream()
						.filter(entry -> isNotEmpty(entry.getValue().get(0)))
						.collect(toMap(entry -> entry.getValue().get(0), australiaDateMapping));
				
				Map<String, List<String>> standardHeaderMap = new LinkedHashMap<>();
				
				standardHeaderMap.put("External Id, ExternalId", singletonList("ExternalId"));
				standardHeaderMap.put("Name", singletonList("Name"));
				standardHeaderMap.put("Foaled, DOB", singletonList("Foaled"));
				standardHeaderMap.put("Sire", singletonList("Sire"));
				standardHeaderMap.put("Dam", singletonList("Dam"));
				standardHeaderMap.put("Colour, Color", singletonList("Colour"));
				standardHeaderMap.put("Sex", singletonList("Sex"));
				standardHeaderMap.put("Avatar", singletonList("Avatar"));
				standardHeaderMap.put("Added Date", singletonList("Added Date"));
				standardHeaderMap.put("Active Status, ActiveStatus", singletonList("Status"));
				standardHeaderMap.put("Current Location", singletonList("Property"));
				standardHeaderMap.put("Current Status, CurrentStatus", singletonList("Current Status"));
				standardHeaderMap.put("Type", singletonList("Type"));
				standardHeaderMap.put("Category", singletonList("Category"));
				standardHeaderMap.put("Bonus Scheme, BonusScheme, Schemes", singletonList("Bonus Scheme"));
				standardHeaderMap.put("Nick Name, NickName", singletonList("Nickname"));
				standardHeaderMap.put("Country", singletonList("Country"));
				standardHeaderMap.put("Microchip", singletonList("Microchip"));
				standardHeaderMap.put("Brand", singletonList("Brand"));
				
				int maxMapValueSize = csvDataHeaderAsKey.values().stream().map(List::size)
						.max(Comparator.comparingInt(i -> i)).orElseThrow(IllegalArgumentException::new);
				
				Map<String, List<String>> ownerDataMap = standardHeaderMap.keySet().stream().map(standardHeader -> {
					
					Predicate<String> csvHeaderMatchStandardHeader = csvHeader ->
							this.stringContainsIgnoreCase(standardHeader, csvHeader, ",");
					
					Optional<String> matchHeaderKey = csvDataHeaderAsKey.keySet().stream()
							.filter(csvHeaderMatchStandardHeader).findFirst();
					
					String standardKey = standardHeaderMap.get(standardHeader).get(0);
					List<String> filledColMissingData = Stream
							.of(standardHeaderMap.get(standardHeader), Collections.nCopies(maxMapValueSize - 1, ""))
							.flatMap(Collection::stream).collect(toList());
					return matchHeaderKey
							.map(key -> {
								csvDataHeaderAsKey.get(key).set(0, standardKey);
								return new SimpleImmutableEntry<>(standardKey, csvDataHeaderAsKey.get(key));
							})
							.orElseGet(() -> new SimpleImmutableEntry<>(standardKey, filledColMissingData));
				}).collect(toMap(Map.Entry::getKey, Map.Entry::getValue, (m, n) -> m, LinkedHashMap::new));
				
				//https://stackoverflow.com/questions/2941997/how-to-transpose-listlist
				List<Iterator<String>> iteratorDataList = new ArrayList<>(ownerDataMap.values()).stream()
						.map(List::iterator).collect(toList());
				
				List<List<String>> transposeList = IntStream.range(0, maxMapValueSize)
						.mapToObj(
								n -> iteratorDataList.stream().filter(Iterator::hasNext)
										.map(Iterator::next).collect(toList())
						).collect(toList());
				
				//filter line only contains " (quotes) and ,(comma)
				Predicate<StringJoiner> validDataLine = line -> isNotEmpty (
						line.toString().chars().distinct()
								.mapToObj(c -> String.valueOf((char) c))
								.collect(joining())
								.replace("\"", "")
								.replace(",", "")
				);
				
				//https://stackoverflow.com/questions/48672931/efficiently-joining-text-in-nested-lists
				String allLiner = transposeList.stream()
						.map(l -> l.stream()
								.map(StringHelper::csvValue)
								.collect(() -> new StringJoiner(","), StringJoiner::add, StringJoiner::merge)
						).filter(validDataLine)
						.collect(() -> new StringJoiner("\n"), StringJoiner::merge, StringJoiner::merge).toString();
				
				streamBuilder.append(allLiner);
				String path = getOutputFolder(dirName) + File.separator + "formatted-horse-v2.csv";
				FileHelper.writeDataToFile(path, streamBuilder.toString().getBytes());
				return streamBuilder;
			}
			return null;
		}
		
		public int getMaxRowLength(List<String> csvData) {
			String[][] data = csvData.stream().map(OnboardHelper::readCsvLine).toArray(String[][]::new);
			return Arrays.stream(data).max(Comparator.comparingInt(ArrayUtils::getLength))
					.orElseThrow(IllegalAccessError::new).length;
		}
		
		public Map<Integer, List<String>> dataAccumulator(final List<String> firstCsvData, final List<String> secondCsvData) {
			Function<Integer, Map.Entry<Integer, List<String>>> colAccumulatorMapper = index -> {
				//value of cell in row based on its index in row.
				//split csv by the comma using java algorithm is damn fast. Faster a thousand times than regex.
				Function<String, String> valueRowIndexMapper = line -> {
					String[] rowArr = customSplitSpecific(line).toArray(new String[0]);
					return getCsvCellValue(rowArr, index);
				};
				List<String> firstColData = firstCsvData.stream().map(valueRowIndexMapper).collect(toList());
				List<String> secondColData = secondCsvData.stream().map(valueRowIndexMapper).collect(toList());
				return new SimpleImmutableEntry<>(
						index, (firstColData.stream().distinct().count() == 2) ? secondColData : firstColData
				);
			};
			
			//col has owner data is col has header and has at least two different cell value.
			Predicate<Map.Entry<Integer, List<String>>> colHasOwnerData = colEntry ->
					(colEntry.getValue().stream().distinct().count() > 2 || isNotEmpty(colEntry.getValue().get(0)));
			
			int rowLength = this.getMaxRowLength(firstCsvData);
			Map<Integer, List<String>> columnEntries = Stream.iterate(0, n -> n + 1).limit(rowLength)
					.map(colAccumulatorMapper)
					.filter(colHasOwnerData)
					.collect(toMap(Map.Entry::getKey, Map.Entry::getValue));
			
			return columnEntries;
		}
		
		public void mergeHorseFile(MultipartFile first, MultipartFile second, String dirName) throws IOException {
			List<String> firstCsvData = this.getCsvData(first);
			List<String> secondCsvData = this.getCsvData(second);
			Map<Integer, List<String>> joinCsvData = this.dataAccumulator(firstCsvData, secondCsvData);
			
			StringBuilder streamBuilder = new StringBuilder();
			
			int maxMapValueSize = joinCsvData.values().stream().map(List::size)
					.max(Comparator.comparingInt(i -> i)).orElseThrow(IllegalArgumentException::new);
			
			List<Iterator<String>> iteratorDataList = new ArrayList<>(joinCsvData.values()).stream()
					.map(List::iterator).collect(toList());
			
			List<List<String>> transposeList = IntStream.range(0, maxMapValueSize)
					.mapToObj(
							n -> iteratorDataList.stream().filter(Iterator::hasNext)
									.map(Iterator::next).collect(toList())
					).collect(toList());
			
			//filter line only contains " (quotes) and ,(comma)
			Predicate<StringJoiner> validDataLine = line -> isNotEmpty (
					line.toString().chars().distinct()
							.mapToObj(c -> String.valueOf((char) c))
							.collect(joining())
							.replace("\"", "")
							.replace(",", "")
			);
			
			//https://stackoverflow.com/questions/48672931/efficiently-joining-text-in-nested-lists
			String allLiner = transposeList.stream()
					.map(l -> l.stream()
							.map(StringHelper::csvValue)
							.collect(() -> new StringJoiner(","), StringJoiner::add, StringJoiner::merge)
					).filter(validDataLine)
					.collect(() -> new StringJoiner("\n"), StringJoiner::merge, StringJoiner::merge).toString();
			
			streamBuilder.append(allLiner);
			String path = getOutputFolder(dirName) + File.separator + "merge-horse-v2.csv";
			FileHelper.writeDataToFile(path, streamBuilder.toString().getBytes());
		}
		
		private Object importHorseFromMiStable(MultipartFile horseFile, String dirName) {
	
			try {
				String path = getOutputFolder(dirName).concat(File.separator).concat("formatted-horse.csv");
	
				List<String> csvData = this.getCsvData(horseFile);
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
	
					String rowHeader = String.format("%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s%n",
							"ExternalId", "Name", "Foaled", "Sire", "Dam", "Color",
							"Sex", "Avatar", "AddedDate", "ActiveStatus",
							"CurrentLocation", "CurrentStatus",
							"Type", "Category", "BonusScheme", "NickName", "Country", "Microchip", "Brand"
					);
	
					builder.append(rowHeader);
	
					csvData = csvData.stream().skip(1).collect(toList());
					for (String line : csvData) {
						String[] r = readCsvLine(line);
	
						String externalId = getCsvCellValue(r, externalIdIndex);
						String name = getCsvCellValue(r, nameIndex);
	
						String rawFoaled = getCsvCellValue(r, foaledIndex);
						String foaled = EMPTY;
	
						boolean isAustraliaFormat = isAustraliaFormat(csvData, foaledIndex, "horse");
	
						if (!isAustraliaFormat && isNotEmpty(rawFoaled)) {
							foaled = LocalDate.parse(rawFoaled, AMERICAN_CUSTOM_DATE_FORMAT).format(AUSTRALIA_FORMAL_DATE_FORMAT);
						} else {
							foaled = rawFoaled;
						}
	
						String sire = getCsvCellValue(r, sireIndex);
						String dam = getCsvCellValue(r, damIndex);
						String color = getCsvCellValue(r, colorIndex);
						String sex = getCsvCellValue(r, sexIndex);
	
						String avatar = getCsvCellValue(r, avatarIndex);
	
						String addedDate = getCsvCellValue(r, addedDateIndex);
						addedDateBuilder.append(addedDate);
	
						String activeStatus = getCsvCellValue(r, activeStatusIndex);
						addedDateBuilder.append(activeStatus);
	
						String currentLocation = getCsvCellValue(r, horseLocationIndex);
						addedDateBuilder.append(currentLocation);
	
						String currentStatus = getCsvCellValue(r, horseStatusIndex);
						String type = getCsvCellValue(r, typeIndex);
						String category = getCsvCellValue(r, categoryIndex);
						String bonusScheme = getCsvCellValue(r, bonusSchemeIndex);
						String nickName = getCsvCellValue(r, nickNameIndex);
						String country = getCsvCellValue(r, countryIndex);
						String microchip = getCsvCellValue(r, microchipIndex);
						String brand = getCsvCellValue(r, brandIndex);
	
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
				List<String> csvData = this.getCsvData(horseFile);
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
	
					String rowHeader = String.format("%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s\n",
							"ExternalId", "Name", "Foaled", "Sire", "Dam", "Color",
							"Sex", "Avatar", "AddedDate", "ActiveStatus/Status",
							"CurrentLocation/HorseLocation", "CurrentStatus/HorseStatus",
							"Type", "Category", "BonusScheme", "NickName"
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
	
						String externalId = getCsvCellValue(r, externalIdIndex);
						String name = getCsvCellValue(r, nameIndex);
	
						if (StringUtils.isEmpty(name)) {
							logger.info("**************************Empty Horse Name: {} at line: {}", name, line);
							continue;
						}
	
						String rawFoaled = getCsvCellValue(r, foaledIndex);
						rawFoaled = rawFoaled.split("\\p{Z}")[0];
						String foaled;
						if (!isAustraliaFormat && isNotEmpty(rawFoaled)) {
							foaled = LocalDate.parse(rawFoaled, AMERICAN_CUSTOM_DATE_FORMAT).format(AUSTRALIA_FORMAL_DATE_FORMAT);
						} else {
							foaled = rawFoaled;
						}
	
						String sire = getCsvCellValue(r, sireIndex);
						String dam = getCsvCellValue(r, damIndex);
	
						if (StringUtils.isEmpty(name) && StringUtils.isEmpty(sire) && StringUtils.isEmpty(dam)
								&& StringUtils.isEmpty(rawFoaled)) continue;
	
						String color = getCsvCellValue(r, colorIndex);
						String sex = getCsvCellValue(r, sexIndex);
						String avatar = getCsvCellValue(r, avatarIndex);
						String dayHere = getCsvCellValue(r, daysHereIndex);
						String addedDate = getCsvCellValue(r, addedDateIndex);
						String activeStatus = getCsvCellValue(r, activeStatusIndex);
						String currentLocation = getCsvCellValue(r, horseLocationIndex);
						String currentStatus = getCsvCellValue(r, horseStatusIndex);
						String type = getCsvCellValue(r, typeIndex);
						String category = getCsvCellValue(r, categoryIndex);
						String bonusScheme = getCsvCellValue(r, bonusSchemeIndex);
						String nickName = getCsvCellValue(r, nickNameIndex);
						String country = getCsvCellValue(r, countryIndex);
						String microchip = getCsvCellValue(r, microchipIndex);
						String brand = getCsvCellValue(r, brandIndex);
	
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
					Map<String, String> fromHorseFile = CollectionsHelper.getDiffMap(horseMap, horseOwnershipMap, false);
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
		private static final String REMOVE_LINE_BREAK_PATTERN = "\nCT\\b";
		private static final String REMOVE_INVALID_SHARES_PATTERN = "\\bInt.Party\\b";
		private static final String CORRECT_HORSE_NAME_PATTERN = "^([^,]*)(?=\\s\\(.*).*$";
		private static final String CORRECT_SHARE_COLUMN_POSITION_PATTERN = "(?m)^,(([\\d]{1,3})(\\.)([\\d]{1,2})%)";
		private static final String TRYING_SHARE_COLUMN_POSITION_PATTERN = "(?m)^(([\\d]{1,3})(\\.)([\\d]{1,2})%)";
		private static final String TRIM_HORSE_NAME_PATTERN = "(?m)^\\s";
		private static final String MOVE_HORSE_TO_CORRECT_LINE_PATTERN = "(?m)^([^,].*)\\n,(?=([\\d]{1,3})?(\\.)?([\\d]{1,2})?%)";
		private static final String EXTRACT_DEPARTED_DATE_OF_HORSE_PATTERN =
				"(?m)^([^,].*)\\s\\(\\s.*([\\s]+)([0-9]{0,2}([/\\-.])[0-9]{0,2}([/\\-.])[0-9]{0,4})";
		private static final String NORMAL_OWNERSHIP_EXPORTED_DATE_PATTERN =
				"(?m)(\\bPrinted\\b[:\\s]+)([0-9]{0,2}([/\\-.])[0-9]{0,2}([/\\-.])[0-9]{0,4})";
		
		private static final String ARDEX_OWNERSHIP_EXPORTED_DATE_PATTERN = "(Monday|Tuesday|Wednesday|Thursday|Friday|Saturday|Sunday),\\s" +
				"((\\(0[1-9]|[12][0-9]|3[01])\\s" +
				"(Jan(uary)?|Feb(ruary)?|Mar(ch)?|Apr(il)?|May|Jun(e)?|Jul(y)?|Aug(ust)?|Sep(tember)?|Oct(ober)?|Nov(ember)?|Dec(ember)?),\\s" +
				"((19|20)\\d\\d))";
	
		private static final String MIXING_COMMS_FINANCE_EMAIL_PATTERN = "\"?\\bAccs\\b:\\s((.+) (?=(\\bComms\\b)))\\bComms\\b:\\s((.+)(\\.[a-zA-Z;]+)(?=,))\"?";
	
		private static final String REMOVE_UNNECESSARY_DATA =
				"(?m)^(?!((,)?Share %)|(.*(?=([\\d]{1,3})(\\.)([\\d]{1,2})%))).*$(\\n)?";
		private static final String EXTRACT_FILE_OWNER_NAME_PATTERN = "(?m)^(Horses)(.+)$(?=\\n)";
		private static final String CSV_HORSE_COUNT_PATTERN = "(?m)^(.+)Horses([,]+)$";
		private static final String OWNERSHIP_STANDARD_HEADER_PATTERN = "(?m)^(.*)?Share %.*$";
		private static final int IGNORED_NON_DATA_LINE_THRESHOLD = 9;
		
		private static final String WINDOW_OUTPUT_FILE_PATH = "C:\\Users\\conta\\OneDrive\\Desktop\\data\\";
		private static final String UNIX_OUTPUT_FILE_PATH = "/home/logbasex/Desktop/data/";
		private static final String CT_IN_DISPLAY_NAME_PATTERN = "\\bCT:";
	
		private static final DateTimeFormatter AUSTRALIA_CUSTOM_DATE_FORMAT;
		static {
			AUSTRALIA_CUSTOM_DATE_FORMAT = new DateTimeFormatterBuilder()
					.appendValue(DAY_OF_MONTH, 1, 2, SignStyle.NEVER)
					.appendLiteral('/')
					.appendValue(MONTH_OF_YEAR, 1, 2, SignStyle.NEVER)
					.appendLiteral('/')
					.appendValue(YEAR, 2, 4, SignStyle.NEVER)
					.toFormatter()
					.withResolverStyle(ResolverStyle.STRICT);
		}
	
		private static final DateTimeFormatter AUSTRALIA_FORMAL_DATE_FORMAT;
		static {
			AUSTRALIA_FORMAL_DATE_FORMAT = new DateTimeFormatterBuilder()
					.appendValue(DAY_OF_MONTH, 2)
					.appendLiteral('/')
					.appendValue(MONTH_OF_YEAR, 2)
					.appendLiteral('/')
					.appendValue(YEAR, 4)
					.toFormatter()
					.withResolverStyle(ResolverStyle.STRICT);
		}
	
		private static final DateTimeFormatter AMERICAN_CUSTOM_DATE_FORMAT;
		static {
			AMERICAN_CUSTOM_DATE_FORMAT = new DateTimeFormatterBuilder()
					.appendValue(MONTH_OF_YEAR, 1, 2, SignStyle.NEVER)
					.appendLiteral('/')
					.appendValue(DAY_OF_MONTH, 1, 2, SignStyle.NEVER)
					.appendLiteral('/')
					.appendValue(YEAR, 2, 4, SignStyle.NEVER)
					.toFormatter()
					.withResolverStyle(ResolverStyle.STRICT);
		}
		
		//ResolverStyle should using yyyy instead of uuuu
		private static final DateTimeFormatter ARDEX_DATE_FORMAT;
		static {
			ARDEX_DATE_FORMAT = new DateTimeFormatterBuilder()
					.appendPattern("dd MMMM, uuuu")
					.parseCaseSensitive()
					.toFormatter()
					.withResolverStyle(ResolverStyle.STRICT);
		}
		
		@Override
		public Map<Object, Object> automateImportOwnerShips(List<MultipartFile> ownershipFiles) {
			String ownershipHeader = String.format("%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s\n",
					"HorseId", "HorseName",
					"OwnerID", "CommsEmail", "FinanceEmail", "FirstName", "LastName", "DisplayName",
					"Type", "Mobile", "Phone", "Fax", "Address", "City", "State", "PostCode",
					"Country", "GST", "Shares", "FromDate", "ExportedDate"
			);
		
			String nameHeader = String.format("%s,%s,%s,%s\n", "RawDisplayName", "Extracted DisplayName", "Extracted FirstName", "Extracted LastName");
			
			Map<Object, Object> result = new HashMap<>();
			List<String> exportedDateList = new ArrayList<>();
			
			StringBuilder dataBuilder = new StringBuilder(ownershipHeader);
			StringBuilder nameBuilder = new StringBuilder(nameHeader);
			StringBuilder normalNameBuilder = new StringBuilder("\n***********NORMAL NAME***********\n");
			StringBuilder organizationNameBuilder = new StringBuilder("\n***********ORGANIZATION NAME***********\n");
			
			for (MultipartFile ownershipFile: ownershipFiles) {
				try {
					List<String> csvData = this.getCsvData(ownershipFile);
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
					exportedDateList.add(exportedDate);
					
					// Line has departedDate likely to extract:
					//Azurite (IRE) ( Azamour (IRE) - High Lite (GB)) 9yo Bay Gelding     Michael Hickmott Bloodstock - In
					//training Michael Hickmott Bloodstock 1/08/2019 >> 1/08/2019
					//This is required for make sure horse data after format csv are exact.
					
					Matcher departedDateMatcher = Pattern.compile(EXTRACT_DEPARTED_DATE_OF_HORSE_PATTERN).matcher(allLines);
					
					Map<String, String> horseDataMap = new LinkedHashMap<>();
					
					while (departedDateMatcher.find()) {
						String horseName = departedDateMatcher.group(1).trim();
						String horseDepartedDate = departedDateMatcher.group(3).trim();
						
						if (StringUtils.isEmpty(horseName))
							continue;
						
						if (StringUtils.isEmpty(horseDepartedDate))
							logger.info("Horse without departed date: {}", horseName);
						
						if (!isDMYFormat(horseDepartedDate)) {
							throw new CustomException(new ErrorInfo("The departed date was not recognized as a valid Australia format: {}", horseDepartedDate));
						}
						
						//process for case: 25/08/19 (usually 25/08/2019)
						String horseDate = LocalDate.parse(horseDepartedDate, AUSTRALIA_CUSTOM_DATE_FORMAT).format(AUSTRALIA_FORMAL_DATE_FORMAT);
						horseDataMap.put(horseName, horseDate);
					}
					
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
		
							int startSireDamInfoIndex = max(leftIndexes);
							int endSireDamInfoIndex = max(rightIndexes);
							String sireDamPart = substring(line, startSireDamInfoIndex, endSireDamInfoIndex);
							String namePart = substringBeforeLast(line, sireDamPart);
							String additionalInfoPart = substringAfterLast(line, sireDamPart);
							
							if (!additionalInfoPart.contains("yo")) {
								logger.warn("Wired data");
							}
							line = namePart;
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
						throw new CustomException(new ErrorInfo("CSV data seems a little weird. Please check!"));
					}
			
					String[][] data = this.get2DArrayFromString(allLines);
					
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
						gstString.append(row[gstIndex]);
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
			
					List<String> csvDataWithBankColumns = this.getListFrom2DArrString(data);
			
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
			
					String[][] blankHorseNameData = this.get2DArrayFromString(builder.toString());
			
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
			
					List<String> csvDataList = this.getListFrom2DArrString(blankHorseNameData);
			
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
						int shareIndex = checkColumnIndex(header, "Shares", "Share", "Ownership", "Share %");
						int addedDateIndex = checkColumnIndex(header, "AddedDate", "Added Date");
						int realGstIndex = checkColumnIndex(header, "GST");
				
						//process file without header
						csvDataList = csvDataList.stream().skip(1).collect(toList());
				
						boolean isAustraliaFormat = isAustraliaFormat(csvDataList, addedDateIndex, "ownership");
				
						for (String line : csvDataList) {
							String[] r = readCsvLine(line);
					
							String horseId = getCsvCellValue(r, horseIdIndex);
							String horseName = getCsvCellValue(r, horseNameIndex);
							String ownerId = getCsvCellValue(r, ownerIdIndex);
							String commsEmail = getCsvCellValue(r, commsEmailIndex);
							String financeEmail = getCsvCellValue(r, financeEmailIndex);
	
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
								commsEmail = this.getValidEmailStr(tryingCommsEmail, line);
						
								if (StringUtils.isEmpty(financeEmail)) {
									financeEmail = this.getValidEmailStr(tryingFinanceEmail, line);
								}
							} else {
								commsEmail = this.getValidEmailStr(commsEmail, line);
								financeEmail = this.getValidEmailStr(financeEmail, line);
							}
					
							String firstName = getCsvCellValue(r, firstNameIndex);
							String lastName = getCsvCellValue(r, lastNameIndex);
							String displayName = getCsvCellValue(r, displayNameIndex);
					
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
					
							String type = getCsvCellValue(r, typeIndex);
							String mobile = getCsvCellValue(r, mobileIndex);
							String phone = getCsvCellValue(r, phoneIndex);
							String fax = getCsvCellValue(r, faxIndex);
							String address = getCsvCellValue(r, addressIndex);
							String city = getCsvCellValue(r, cityIndex);
							String state = getCsvCellValue(r, stateIndex);
							String postCode = getPostcode(getCsvCellValue(r, postCodeIndex));
							String country = getCsvCellValue(r, countryIndex);
							String gst = getCsvCellValue(r, realGstIndex);
							String share = getCsvCellValue(r, shareIndex);
					
							String rawAddedDate = getCsvCellValue(r, addedDateIndex);
							//remove all whitespace include unicode character
							rawAddedDate = rawAddedDate.split("\\p{Z}")[0];
							
							//convert addedDate read from CSV to Australia date time format.
							String addedDate = (!isAustraliaFormat && isNotEmpty(rawAddedDate))
									? LocalDate.parse(rawAddedDate, AMERICAN_CUSTOM_DATE_FORMAT).format(AUSTRALIA_FORMAL_DATE_FORMAT)
									: rawAddedDate;
							
					
							String rowBuilder = String.format("%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s," +
											"%s,%s\n",
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
			
			result.put("extractedName", nameBuilder.append(normalNameBuilder).append(organizationNameBuilder));
			result.put("csvData", dataBuilder);
			result.put("exportedDate", exportedDateList);
			return result;
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
		
		private String[][] get2DArrayFromString(String value) {
			List<List<String>> nestedListData = Arrays.stream(value.split("\n"))
					.map(StringHelper::customSplitSpecific)
					.collect(toList());
	
			return nestedListData.stream()
					.map(l -> l.toArray(new String[0]))
					.toArray(String[][]::new);
		}
	
		private List<String> getListFrom2DArrString(String[][] value) {
			List<String> result = new ArrayList<>();
			for (String[] strings : value) {
				String row = String.join(",", strings);
				result.add(row);
			}
			return result;
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
				
					String extractedName = String.format("%s,%s,%s,%s\n",
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
		
		
		private boolean isDMYFormat(String date) {
			boolean isParsable = true;
			try {
				LocalDate.parse(date, AUSTRALIA_CUSTOM_DATE_FORMAT);
			} catch (DateTimeParseException e) {
				isParsable = false;
			}
			 return isParsable;
		}
		
		private boolean isMDYFormat(String date) {
			boolean isParsable = true;
			try {
				LocalDate.parse(date, AMERICAN_CUSTOM_DATE_FORMAT);
			} catch (DateTimeParseException e) {
				isParsable = false;
			}
			return isParsable;
		}
		
		private boolean isAustraliaFormatV2(List<String> dates) {
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
		
		private boolean isAustraliaFormat(List<String> csvData, int dateIndex, String fileType) {
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
				String rawDateTime = getCsvCellValue(r, dateIndex);
	
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
				logger.info("Type of DATE in {} file is DD/MM/YYY format **OR** M/D/Y format >>>>>>>>> Please check.", upperCase(fileType));
	
			} else if (!isEmpty(mdyFormatList)) {
				logger.info("Type of DATE in {} file is MM/DD/YYY format", upperCase(fileType));
	
			} else {
				logger.info("Type of DATE in {} file is UNDEFINED", upperCase(fileType));
			}
	
			return isAustraliaFormat;
		}
		
		private String getValidEmailStr(String emailsStr, String line) throws CustomException {
			if (StringUtils.isEmpty(emailsStr)) return EMPTY;
			String[] emailList = emailsStr.split(";");
			
			for (String email : emailList) {
				if (!isValidEmail(email.trim())) {
					logger.error("*********************Email is invalid: {} at line: {}. Please check!", email, line);
					throw new CustomException(new ErrorInfo("Invalid Email"));
				}
			}
			
			return emailsStr;
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
	}