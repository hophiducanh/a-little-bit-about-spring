package com.tellyouiam.alittlebitaboutspring.service.note.v2;

import com.tellyouiam.alittlebitaboutspring.service.note.utils.CommonHelper;
import com.tellyouiam.alittlebitaboutspring.utils.FileHelper;
import com.tellyouiam.alittlebitaboutspring.utils.OnboardHelper;
import com.tellyouiam.alittlebitaboutspring.utils.StringHelper;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.StringJoiner;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static com.tellyouiam.alittlebitaboutspring.service.note.consts.CommonConst.AMERICAN_CUSTOM_DATE_FORMAT;
import static com.tellyouiam.alittlebitaboutspring.service.note.consts.CommonConst.AUSTRALIA_FORMAL_DATE_FORMAT;
import static com.tellyouiam.alittlebitaboutspring.service.note.utils.CommonHelper.isAustraliaFormatV2;
import static com.tellyouiam.alittlebitaboutspring.service.note.utils.CommonHelper.isDMYFormat;
import static com.tellyouiam.alittlebitaboutspring.service.note.utils.CommonHelper.isMDYFormat;
import static com.tellyouiam.alittlebitaboutspring.utils.FileHelper.getOutputFolder;
import static com.tellyouiam.alittlebitaboutspring.utils.OnboardHelper.getCsvCellValue;
import static com.tellyouiam.alittlebitaboutspring.utils.StringHelper.customSplitSpecific;
import static java.util.Collections.singletonList;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;
import static org.apache.commons.lang3.StringUtils.isNotEmpty;
import static org.springframework.util.CollectionUtils.isEmpty;

@Service
public class NoteServiceV2Impl implements NoteServiceV2 {
	
	public Object formatOwnerV2(MultipartFile ownerFile, String dirName) throws IOException {
		//InputStream inputStream = new BufferedInputStream(ownerFile.getInputStream());
		List<String> csvData = CommonHelper.getCsvData(ownerFile);
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
					.anyMatch(headerElement -> CommonHelper.containsIgnoreCase(line, headerElement));
			
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
				
				return new AbstractMap.SimpleImmutableEntry<>(
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
						CommonHelper.stringContainsIgnoreCase(standardHeader, csvHeader, ",");
				
				Optional<String> matchHeaderKey = finalCsvDataHeaderAsKey.keySet().stream()
						.filter(csvHeaderMatchStandardHeader).findFirst();
				
				String standardKey = standardHeaderMap.get(standardHeader).get(0);
				List<String> filledColMissingData = Stream
						.of(standardHeaderMap.get(standardHeader), Collections.nCopies(maxMapValueSize - 1, ""))
						.flatMap(Collection::stream).collect(toList());
				return matchHeaderKey
						.map(key -> new AbstractMap.SimpleImmutableEntry<>(standardKey, finalCsvDataHeaderAsKey.get(key)))
						.orElseGet(() -> new AbstractMap.SimpleImmutableEntry<>(standardKey, filledColMissingData));
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
		List<String> csvData = CommonHelper.getCsvData(horseFile);
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
				
				return new AbstractMap.SimpleImmutableEntry<>(
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
						CommonHelper.stringContainsIgnoreCase(standardHeader, csvHeader, ",");
				
				Optional<String> matchHeaderKey = csvDataHeaderAsKey.keySet().stream()
						.filter(csvHeaderMatchStandardHeader).findFirst();
				
				String standardKey = standardHeaderMap.get(standardHeader).get(0);
				List<String> filledColMissingData = Stream
						.of(standardHeaderMap.get(standardHeader), Collections.nCopies(maxMapValueSize - 1, ""))
						.flatMap(Collection::stream).collect(toList());
				return matchHeaderKey
						.map(key -> {
							csvDataHeaderAsKey.get(key).set(0, standardKey);
							return new AbstractMap.SimpleImmutableEntry<>(standardKey, csvDataHeaderAsKey.get(key));
						})
						.orElseGet(() -> new AbstractMap.SimpleImmutableEntry<>(standardKey, filledColMissingData));
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
	
	private Map<Integer, List<String>> dataAccumulator(final List<String> firstCsvData,
	                                                   final List<String> secondCsvData) {
		Function<Integer, Map.Entry<Integer, List<String>>> colAccumulatorMapper = index -> {
			//value of cell in row based on its index in row.
			//split csv by the comma using java algorithm is damn fast. Faster a thousand times than regex.
			Function<String, String> valueRowIndexMapper = line -> {
				String[] rowArr = customSplitSpecific(line).toArray(new String[0]);
				return getCsvCellValue(rowArr, index);
			};
			List<String> firstColData = firstCsvData.stream().map(valueRowIndexMapper).collect(toList());
			List<String> secondColData = secondCsvData.stream().map(valueRowIndexMapper).collect(toList());
			return new AbstractMap.SimpleImmutableEntry<>(
					index, (firstColData.stream().distinct().count() == 2) ? secondColData : firstColData
			);
		};
		
		//col has owner data is col has header and has at least two different cell value.
		Predicate<Map.Entry<Integer, List<String>>> colHasOwnerData = colEntry ->
				(colEntry.getValue().stream().distinct().count() > 2 || isNotEmpty(colEntry.getValue().get(0)));
		
		int rowLength = CommonHelper.getMaxRowLength(firstCsvData);
		Map<Integer, List<String>> columnEntries = Stream.iterate(0, n -> n + 1).limit(rowLength)
				.map(colAccumulatorMapper)
				.filter(colHasOwnerData)
				.collect(toMap(Map.Entry::getKey, Map.Entry::getValue));
		
		return columnEntries;
	}
	
	public void mergeHorseFile(MultipartFile first, MultipartFile second, String dirName) throws IOException {
		List<String> firstCsvData = CommonHelper.getCsvData(first);
		List<String> secondCsvData = CommonHelper.getCsvData(second);
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
}
