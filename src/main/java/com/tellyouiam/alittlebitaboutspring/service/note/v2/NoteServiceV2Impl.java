package com.tellyouiam.alittlebitaboutspring.service.note.v2;

import com.tellyouiam.alittlebitaboutspring.utils.io.FileHelper;
import com.tellyouiam.alittlebitaboutspring.utils.string.OnboardHelper;
import com.tellyouiam.alittlebitaboutspring.utils.string.StringHelper;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.AbstractMap.SimpleImmutableEntry;
import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static com.tellyouiam.alittlebitaboutspring.service.note.consts.NoteConst.*;
import static com.tellyouiam.alittlebitaboutspring.service.note.utils.NoteHelper.*;
import static com.tellyouiam.alittlebitaboutspring.utils.io.FileHelper.getOutputFolder;
import static com.tellyouiam.alittlebitaboutspring.utils.string.OnboardHelper.getCsvCellValueAtIndex;
import static com.tellyouiam.alittlebitaboutspring.utils.string.OnboardHelper.readCsvLine;
import static com.tellyouiam.alittlebitaboutspring.utils.string.StringHelper.csvValue;
import static java.util.Collections.singletonList;
import static java.util.stream.Collectors.*;
import static org.apache.commons.collections4.CollectionUtils.isNotEmpty;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.apache.commons.lang3.StringUtils.isNotEmpty;
import static org.springframework.util.CollectionUtils.isEmpty;

@Service
public class NoteServiceV2Impl implements NoteServiceV2 {
	
	private Map.Entry<Integer, List<String>> colAccumulatorMapper(int index, List<String> csvData) {
		List<String> dataByColIndex = csvData.stream().map(line -> this.getCellValueByLineIndex(line, index)).collect(toList());
		return new SimpleImmutableEntry<>(index, dataByColIndex);
	}
	
	private String getCellValueByLineIndex(String line, int index) {
		String[] rowArr = OnboardHelper.splitCsvLineByComma(line);
		if (index >= rowArr.length) return EMPTY;
		return getCsvCellValueAtIndex(rowArr, index);
	}
	
	private List<String> ignoreLinePrecedeHeader(List<String> csvData) {
		//find line is header
		Predicate<String> isLineContainHeader = line -> Stream.of("Name", "Address", "Phone", "Mobile", "Sire", "Dam")
				.anyMatch(headerElement -> containsIgnoreCase(line, headerElement));
		
		String headerLine = csvData.stream().filter(isLineContainHeader).findFirst().orElse(EMPTY);
		int headerLineNum = csvData.indexOf(headerLine);
		csvData = csvData.stream().skip(headerLineNum).collect(toList());
		return csvData;
	}
	
	public List<String> formatAustraliaDate(Map.Entry<Integer, List<String>> entry) {
		List<String> rawColData = entry.getValue();
		
		if (!isAustraliaFormatV2(rawColData) && rawColData.stream().noneMatch(value -> value.matches("^\\d{1,2}/\\d{1,2}/\\d{1,4}$"))) {
			return rawColData;
		} else if (isAustraliaFormatV2(rawColData)) {
			return rawColData;
		} else {
			return rawColData.stream().map(rawDate -> {
				if (isNotEmpty(rawDate) && (isMDYFormat(rawDate))) {
					return LocalDate.parse(rawDate, AMERICAN_CUSTOM_DATE_FORMAT).format(AUSTRALIA_FORMAL_DATE_FORMAT);
				}
				return rawDate;
			}).collect(toList());
		}
	}
	
	private Map<Integer, List<String>> standardizedRawData(List<String> csvData) {
		csvData = csvData.stream()
				.filter(StringUtils::isNotEmpty)
				.filter(row -> (!row.matches("^[,+\\s.`'\"]+$"))) //filter non data rows
				.filter(row -> (!row.matches("(.+)([\\d]+)\\sRecords(.+)"))) //filter footer rows
				.collect(toList());
		
		csvData = this.ignoreLinePrecedeHeader(csvData);
		
		List<String> finalCsvData = csvData;
		String[][] data = csvData.stream().map(OnboardHelper::splitCsvLineByComma).toArray(String[][]::new);
		int rowLength = Arrays.stream(data).max(Comparator.comparingInt(ArrayUtils::getLength))
				.orElseThrow(IllegalAccessError::new).length;
		
		List<Map.Entry<Integer, List<String>>> columnDataGroupByIndex = Stream.iterate(0, n -> n + 1)
				.limit(rowLength)
				.map(index -> this.colAccumulatorMapper(index, finalCsvData))
				//col has owner data is col has header (retain col has header although it doesn't contain data in order to merge data in the next col)
				//or has at least two different cell value (include empty string).
				.filter(colEntry -> colEntry.getValue().stream().distinct().count() > 2 || isNotEmpty(colEntry.getValue().get(0)))
				.sorted(Map.Entry.comparingByKey())
				.collect(toList());
		
		Map<Integer, List<String>> formattedDataMap = IntStream.range(0, columnDataGroupByIndex.size()).mapToObj(i -> {
			//each Map.Entry include key is col index in file, and value is collection of all data cells present in that column.
			Map.Entry<Integer, List<String>> dataCurrentColumn = columnDataGroupByIndex.get(i);
			
			//handle index out of bound exception
			if ((i >= columnDataGroupByIndex.size() - 1) || i == 0) {
				return new SimpleImmutableEntry<>(dataCurrentColumn.getKey(), dataCurrentColumn.getValue());
			}
			
			Map.Entry<Integer, List<String>> dataNextColumn = columnDataGroupByIndex.get(i + 1);
			Map.Entry<Integer, List<String>> dataPreviousColumn = columnDataGroupByIndex.get(i - 1);
			
			boolean isNotHavePreviousKey = dataPreviousColumn != null && (!dataCurrentColumn.getKey().equals(dataPreviousColumn.getKey() + 1));
			boolean isConsecutive = dataCurrentColumn.getKey().equals(dataNextColumn.getKey() - 1);
			
			//join two consecutive column in csv, one has header missing body, one has body missing header.
			if (isConsecutive && isNotHavePreviousKey
			    //if current col has header and next col is not.
			    && isNotEmpty(dataCurrentColumn.getValue().get(0)) && StringUtils.isEmpty(dataNextColumn.getValue().get(0))
			) {
				List<String> consecutiveColumnMergedData = Stream.iterate(0, j -> j + 1)
						.limit(dataCurrentColumn.getValue().size())
						.map(j -> dataCurrentColumn.getValue().get(j).concat(dataNextColumn.getValue().get(j)).trim())
						.collect(toList());
				
				return new SimpleImmutableEntry<>(dataNextColumn.getKey(), consecutiveColumnMergedData);
			} else {
				return new SimpleImmutableEntry<>(dataCurrentColumn.getKey(), dataCurrentColumn.getValue());
			}
		}).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (o1, o2) -> o1, LinkedHashMap::new));
		
		return formattedDataMap;
	}
	
	public Object formatOwnerV2(MultipartFile file, String dirName) throws IOException {
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
		
		//-------------------------------------------------------
		List<String> csvData = getCsvDataFromXlsFile(file);
		StringBuilder streamBuilder = new StringBuilder();
		
		if (isNotEmpty(csvData)) {
			
			Map<Integer, List<String>> formattedDataMap = this.standardizedRawData(csvData) ;
			
			//first cell in column as header
			Map<String, List<String>> csvDataHeaderAsKey = formattedDataMap.entrySet().stream()
					.filter(e-> isNotEmpty(e.getValue().get(0)))
					.collect(toMap(e -> e.getValue().get(0), Map.Entry::getValue));
			
			int maxElementsInCols = csvDataHeaderAsKey.values().stream().map(List::size)
					.max(Comparator.comparingInt(i -> i)).orElseThrow(IllegalArgumentException::new);
			
			Map<String, List<String>> ownerDataMap = standardHeaderMap.keySet().stream().map(standardHeader -> {
				List<String> filledColMissingData = Stream
						.of(standardHeaderMap.get(standardHeader), Collections.nCopies(maxElementsInCols - 1, EMPTY))
						.flatMap(Collection::stream).collect(toList());
				
				//if header present in standard header list -> return data.
				//else create new list include standard header and filled with empty data cell.
				return csvDataHeaderAsKey.keySet().stream()
						.filter(csvHeader -> stringContainsIgnoreCase(standardHeader, csvHeader, ",")).findFirst()
						.map(key -> new SimpleImmutableEntry<>(standardHeaderMap.get(standardHeader).get(0), csvDataHeaderAsKey.get(key)))
						.orElseGet(() -> new SimpleImmutableEntry<>(standardHeaderMap.get(standardHeader).get(0), filledColMissingData));
			}).collect(toMap(Map.Entry::getKey, Map.Entry::getValue, (m, n) -> m, LinkedHashMap::new));
			
			//transpose list of cols data to list of rows data.
			String allLiner = IntStream.range(0, maxElementsInCols)
					.mapToObj(n -> ownerDataMap.values().stream().map(i -> i.get(n)).collect(Collectors.toList()))
					.map(l -> l.stream()
							.map(StringHelper::csvValue)
							.collect(Collectors.joining(",")))
					//ignore non data line
					.filter(row -> (!row.matches("^[,+\\s.`'\"]+$")))
					.collect(Collectors.joining(StringUtils.LF));
			
			streamBuilder.append(allLiner);
			String path = getOutputFolder(dirName) + File.separator + "formatted-owner.csv";
			FileHelper.writeDataToFile(path, streamBuilder.toString().getBytes());
			return streamBuilder;
		}
		return null;
	}
	
	@Override
	public Object formatHorseV2(MultipartFile file, String dirName) throws IOException {
		Map<String, List<String>> standardHeaderMap = new LinkedHashMap<>();
		
		standardHeaderMap.put("External Id, ExternalId",                    singletonList("ExternalId"));          //0
		standardHeaderMap.put("Name, Horse, Horse Name, HorseName",         singletonList("Name"));                //1
		standardHeaderMap.put("Foaled, DOB",                                singletonList("Foaled"));              //2
		standardHeaderMap.put("Sire",                                       singletonList("Sire"));                //3
		standardHeaderMap.put("Dam",                                        singletonList("Dam"));                 //4
		standardHeaderMap.put("Colour, Color",                              singletonList("Colour"));              //5
		standardHeaderMap.put("Sex, Gender",                                singletonList("Sex"));                 //6
		standardHeaderMap.put("Avatar",                                     singletonList("Avatar"));              //7
		standardHeaderMap.put("Added Date, addedDate, Status Date",         singletonList("Added Date"));          //8
		standardHeaderMap.put("Active Status, ActiveStatus, active_status", singletonList("Status"));              //9
		standardHeaderMap.put("Current Location, location",                 singletonList("Property"));            //10
		standardHeaderMap.put("Current Status, CurrentStatus, status",      singletonList("Current Status"));      //11
		standardHeaderMap.put("Type",                                       singletonList("Type"));                //12
		standardHeaderMap.put("Category",                                   singletonList("Category"));            //13
		standardHeaderMap.put("Bonus Scheme, BonusScheme, Schemes",         singletonList("Bonus Scheme"));        //14
		standardHeaderMap.put("Nick Name, NickName",                        singletonList("Nickname"));            //15
		standardHeaderMap.put("Country",                                    singletonList("Country"));             //16
		standardHeaderMap.put("Microchip",                                  singletonList("Microchip"));           //17
		standardHeaderMap.put("Brand, Off Side",                            singletonList("Brand"));               //18
		
		List<String> csvData = getCsvDataFromXlsFile(file);
		StringBuilder streamBuilder = new StringBuilder();
		
		if (!isEmpty(csvData)) {
			
			Map<Integer, List<String>> formattedDataMap = this.standardizedRawData(csvData);
			Map<String, List<String>> csvDataHeaderAsKey = formattedDataMap.entrySet().stream()
					.filter(entry -> isNotEmpty(entry.getValue().get(0)))
					.collect(toMap(entry -> entry.getValue().get(0), this::formatAustraliaDate));
			
			int maxElementsInCols = csvDataHeaderAsKey.values().stream().map(List::size)
					.max(Comparator.comparingInt(i -> i)).orElseThrow(IllegalArgumentException::new);
			
			Map<String, List<String>> ownerDataMap = standardHeaderMap.keySet().stream().map(standardHeader -> {
				List<String> filledColMissingData = Stream
						.of(standardHeaderMap.get(standardHeader), Collections.nCopies(maxElementsInCols - 1, EMPTY))
						.flatMap(Collection::stream).collect(toList());
				
				//if header present in standard header list -> return data.
				//else create new list include standard header and filled with empty data cell.
				return csvDataHeaderAsKey.keySet().stream()
						.filter(csvHeader -> stringContainsIgnoreCase(standardHeader, csvHeader, ",")).findFirst()
						.map(key -> new SimpleImmutableEntry<>(standardHeaderMap.get(standardHeader).get(0), csvDataHeaderAsKey.get(key)))
						.orElseGet(() -> new SimpleImmutableEntry<>(standardHeaderMap.get(standardHeader).get(0), filledColMissingData));
			}).collect(toMap(Map.Entry::getKey, Map.Entry::getValue, (m, n) -> m, LinkedHashMap::new));
			
			//transpose list of cols data to list of rows data.
			String allLiner = IntStream.range(0, maxElementsInCols)
					.mapToObj(n -> ownerDataMap.values().stream().map(i -> i.get(n)).collect(Collectors.toList()))
					//try to remove footer
					.filter(row -> row.stream().distinct().count() > 2)
					.map(l -> l.stream()
							.map(StringHelper::csvValue)
							.collect(Collectors.joining(",")))
					//ignore non data line
					.filter(row -> (!row.matches("^[,+\\s.`'\"]+$")))
					.collect(Collectors.joining(StringUtils.LF));
			
			streamBuilder.append(allLiner);
			String path = getOutputFolder(dirName) + File.separator + "formatted-horse-v2.csv";
			FileHelper.writeDataToFile(path, streamBuilder.toString().getBytes());
			return streamBuilder;
		}
		return null;
	}
	
	private Map<Integer, List<String>> dataAccumulator(final List<String> firstCsvData, final List<String> secondCsvData) {
		int rowLength = getMaxRowLength(firstCsvData);
		
		return Stream.iterate(0, n -> n + 1).limit(rowLength)
				.map(i -> this.colDataAccumulator(i, firstCsvData, secondCsvData))
				.collect(toMap(Map.Entry::getKey, Map.Entry::getValue));
	}
	
	private Map.Entry<Integer, List<String>> colDataAccumulator(int index, List<String> firstCsvData,
	                                                            List<String> secondCsvData) {
		List<String> firstColData = firstCsvData.stream().map(line -> this.getCellValueByLineIndex(line, index)).collect(toList());
		List<String> secondColData = secondCsvData.stream().map(line -> this.getCellValueByLineIndex(line, index)).collect(toList());
		
		//merge two columns has same title. Get second col if the first one is empty (Only header)
//		List<String> mergeColData = isCsvColHasOnlyHeader(firstColData) ? secondColData : firstColData;
		List<String> mergeColData = (!isCsvColHasOnlyHeader(firstColData) && firstColData.containsAll(secondColData)) ? firstColData : secondColData;
		return new SimpleImmutableEntry<>(index, mergeColData);
	}
	
	//=VLOOKUP(B9,Sheet1!$B$1:$M$566,12,FALSE)
	public void mergeHorseFile(MultipartFile first, MultipartFile second, String dirName) throws IOException {
		List<String> firstCsvData = getCsvData(first);
		List<String> secondCsvData = getCsvData(second);
		Map<Integer, List<String>> joinCsvDataMap = this.dataAccumulator(firstCsvData, secondCsvData);
		
		StringBuilder streamBuilder = new StringBuilder();
		
		int maxMapValueSize = joinCsvDataMap.values().stream().map(List::size)
				.max(Comparator.comparingInt(i -> i)).orElseThrow(IllegalArgumentException::new);
		
		List<Iterator<String>> iteratorDataList = new ArrayList<>(joinCsvDataMap.values()).stream()
				.map(List::iterator).collect(toList());
		
		List<List<String>> transposeList = IntStream.range(0, maxMapValueSize)
				.mapToObj(
						n -> iteratorDataList.stream().filter(Iterator::hasNext)
								.map(Iterator::next).collect(toList())
				).collect(toList());
		
		//filter line only contains " (quotes) and ,(comma) >> is not empty after remove quote and comma
		Predicate<StringJoiner> isNotEmptyCsvLine = line -> isNotEmpty(
				line.toString().chars().distinct()
						.mapToObj(c -> String.valueOf((char) c))
						.collect(joining())
						.replace(QUOTE_CHAR, EMPTY)
						.replace(CSV_LINE_SEPARATOR, EMPTY)
		);
		
		Function<List<String>, StringJoiner> convertListToCsvLine = list -> list.stream()
				.map(StringHelper::csvValue)
				.collect(() -> new StringJoiner(CSV_LINE_SEPARATOR), StringJoiner::add, StringJoiner::merge);
		
		//https://stackoverflow.com/questions/48672931/efficiently-joining-text-in-nested-lists
		String allLiner = transposeList.stream()
				.map(convertListToCsvLine)
				.filter(isNotEmptyCsvLine)
				.collect(() -> new StringJoiner(CSV_LINE_END), StringJoiner::merge, StringJoiner::merge)
				.toString();
		
		streamBuilder.append(allLiner);
		String path = getOutputFolder(dirName) + File.separator + "merge-horse-v2.csv";
		FileHelper.writeDataToFile(path, streamBuilder.toString().getBytes());
	}
	
	//format this file https://drive.google.com/drive/u/1/folders/1Sy3FR_Lej8grsCJexysjDSUMvyT0moSk
	public static void main(String[] args) throws IOException {
		BufferedReader reader = new BufferedReader(new FileReader(new File("C:\\Users\\conta\\OneDrive\\Desktop\\data\\08 Oct _ Data\\Ownerships.csv")));
		List<String> data = reader.lines().filter(i -> !i.isEmpty()).collect(toList());
		
		List<Integer> horseIndexes = new ArrayList<>();
		List<Integer> ownerIndexes = new ArrayList<>();
		for (int i = 7; i < data.size() + 1 ; i++) {
			if (i >= data.size()) continue;
			if (i == 7) {
				horseIndexes.add(i);
			}
			
			
			for (int x = 1; x < 21; x++) {
				
				int index = i + 8 + 5 * x;
				int ownerIndex = index + 4; //"Owner"
				
				if (index >= data.size()) continue;
				if (ownerIndex >= data.size()) continue;
				
				String[] nextHorseLine = readCsvLine(data.get(index));
				String[] ownerString = readCsvLine(data.get(ownerIndex));
				
				if (isNotEmpty(nextHorseLine[1]) && (nextHorseLine[1]).matches("[A-Z0-9]+") && ownerString[1].equals("Owner")) {
					horseIndexes.add(index);
					i = index - 1;
					break;
				}
			}
		}
		
		Map<Integer, List<Integer>> ownerMap = new LinkedHashMap<>();
		for (int u = 0; u < horseIndexes.size(); u++) {
			for (int i = 6; i < data.size() + 1; i++) {
				
				if (i >= data.size()) continue;
				if (u + 1 >= horseIndexes.size()) continue;
				
				if (horseIndexes.get(u) > i && i < horseIndexes.get(u + 1)) {
					i = horseIndexes.get(u);
					List<Integer> ownerIndexs = new ArrayList<>();
					for (int x = 0; x < 20; x++) {
						int nextOwnerIndex = horseIndexes.get(u) + 6 + 5 * x;
						if (nextOwnerIndex >= data.size()) continue;
						if (nextOwnerIndex < horseIndexes.get(u + 1)) {
							String[] nextOwner = readCsvLine(data.get(nextOwnerIndex));
							if (isNotEmpty(nextOwner[1]) && (nextOwner[1]).matches("[A-Z0-9]+")) {
								ownerIndexs.add(nextOwnerIndex);
							}
						}
					}
					ownerMap.put(horseIndexes.get(u), ownerIndexs);
				}
			}
		}
	
		List<Integer> phoneIndexes = ownerIndexes.stream().peek(i -> i = i + 2).collect(toList());
		List<Integer> privatePhoneIndexes = ownerIndexes.stream().peek(i -> i = i + 3).collect(toList());
		List<Integer> emailIndexes = ownerIndexes.stream().peek(i -> i = i + 4).collect(toList());
		List<Integer> address1Indexes = ownerIndexes;
		List<Integer> address2Indexes = ownerIndexes.stream().peek(i -> i = i + 1).collect(toList());
		List<Integer> addedDateIndexes = address1Indexes;
		List<Integer> shareIndexes = address2Indexes;
		
		String ownershipHeader = String.format("%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s%n",
				"HorseId", "HorseName",
				"OwnerID", "CommsEmail", "FinanceEmail", "FirstName", "LastName", "DisplayName",
				"Type", "Mobile", "Phone", "Fax", "Address", "City", "State", "PostCode",
				"Country", "GST", "Debtor", "Shares", "FromDate", "ToDate"
		);
		
		StringBuilder dataBuilder = new StringBuilder(ownershipHeader);
//		MapUtils.verbosePrint(System.out, "Owner Map", ownerMap);
		ownerMap.forEach((k, v) -> {
			String[] horseLine = readCsvLine(data.get(k));
			String horseName = horseLine[6];
			for (int i = 0; i < v.size(); i++) {
				//mobile : 35
				int displayNameLineIndex  = v.get(i);
				int phoneLineIndex        = v.get(i) + 2;
				int privatePhoneLineIndex = v.get(i) + 3;
				int emailLineIndex        = v.get(i) + 4;
				int address1LineIndex     = v.get(i);
				int address2LineIndex     = v.get(i) + 1;
				int addedDateLineIndex    = v.get(i);
				int shareLineIndex        = v.get(i) + 1;
				
				String displayName = getCsvCellValueAtIndex(readCsvLine(data.get(displayNameLineIndex)), 6);
				String phone = getCsvCellValueAtIndex(readCsvLine(data.get(phoneLineIndex)), 11);
				String privatePhone = getCsvCellValueAtIndex(readCsvLine(data.get(privatePhoneLineIndex)), 11);
				String mobile = getCsvCellValueAtIndex(readCsvLine(data.get(phoneLineIndex)), 35);
				String privateMobile = getCsvCellValueAtIndex(readCsvLine(data.get(privatePhoneLineIndex)), 35);
				String fax = getCsvCellValueAtIndex(readCsvLine(data.get(phoneLineIndex)), 22);
				String privateFax = getCsvCellValueAtIndex(readCsvLine(data.get(privatePhoneLineIndex)), 22);
				String email = getCsvCellValueAtIndex(readCsvLine(data.get(emailLineIndex)), 11);
				String address1 = getCsvCellValueAtIndex(readCsvLine(data.get(address1LineIndex)), 18);
				String address2 = getCsvCellValueAtIndex(readCsvLine(data.get(address2LineIndex)), 18);
				String addedDate = getCsvCellValueAtIndex(readCsvLine(data.get(addedDateLineIndex)), 37);
				String share = getCsvCellValueAtIndex(readCsvLine(data.get(shareLineIndex)), 39);
				String rowBuilder = String.format("%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s%n",
						csvValue(""),
						csvValue(horseName),
						csvValue(""),
						csvValue(email),
						csvValue(""),
						csvValue(""),
						csvValue(""),
						csvValue(displayName),
						csvValue(""),
						csvValue(Stream.of(mobile, privateMobile).filter(x -> !x.isEmpty()).collect(Collectors.joining(";"))),
						csvValue(Stream.of(phone, privatePhone).filter(x -> !x.isEmpty()).collect(Collectors.joining(";"))),
						csvValue(Stream.of(fax, privateFax).filter(x -> !x.isEmpty()).collect(Collectors.joining(";"))),
						csvValue(address1 + " " + address2),
						csvValue(""),
						csvValue(""),
						csvValue(""),
						csvValue(""),
						csvValue(""),
						csvValue(""),
						csvValue(share),
						csvValue(addedDate),
						csvValue(""));
				dataBuilder.append(rowBuilder);
			}
		});
		
		String path = "C:\\Users\\conta\\OneDrive\\Desktop\\data\\08 Oct _ Data\\submit\\formatted-ownership.csv";
		Files.write(Paths.get(path), dataBuilder.toString().getBytes());
	}
}
