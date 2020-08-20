package com.tellyouiam.alittlebitaboutspring.service.csv;

import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import com.tellyouiam.alittlebitaboutspring.entity.csvformat.Horse;
import com.tellyouiam.alittlebitaboutspring.entity.csvformat.OpeningBalance;
import com.tellyouiam.alittlebitaboutspring.entity.csvformat.TaxCodes;
import com.tellyouiam.alittlebitaboutspring.filter.EmptyLineFilter;
import com.tellyouiam.alittlebitaboutspring.filter.ValidLineFilter;
import com.tellyouiam.alittlebitaboutspring.utils.string.StringHelper;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

import static com.tellyouiam.alittlebitaboutspring.utils.string.StringHelper.getMultiMapSingleStringValue;

@Service
public class CsvServiceImpl implements CsvService {
	
	private static final Logger logger = LoggerFactory.getLogger(CsvServiceImpl.class);
	
	private static final String IS_DATE_MONTH_YEAR_FORMAT_PATTERN = "^(?:(?:31([/\\-.])(?:0?[13578]|1[02]))\\1|" +
			"(?:(?:29|30)([/\\-.])(?:0?[13-9]|1[0-2])\\2))(?:(?:1[6-9]|[2-9]\\d)?\\d{2})$|" +
			"^(?:29([/\\-.])0?2\\3(?:(?:1[6-9]|[2-9]\\d)?(?:0[48]|[2468][048]|[13579][26])|(?:(?:16|[2468][048]|[3579][26])00)))$|" +
			"^(?:0?[1-9]|1\\d|2[0-8])([/\\-.])(?:(?:0?[1-9])|(?:1[0-2]))\\4(?:(?:1[6-9]|[2-9]\\d)?\\d{2})$";
	
	private static final String IS_MONTH_DATE_YEAR_FORMAT_PATTERN = "^(?:(?:(?:0?[13578]|1[02])([/\\-.])31)\\1|" +
			"(?:(?:0?[13-9]|1[0-2])([/\\-.])(?:29|30)\\2))(?:(?:1[6-9]|[2-9]\\d)?\\d{2})$|" +
			"^(?:0?2([/\\-.])29\\3(?:(?:1[6-9]|[2-9]\\d)?(?:0[48]|[2468][048]|[13579][26])|(?:(?:16|[2468][048]|[3579][26])00)))$|" +
			"^(?:(?:0?[1-9])|(?:1[0-2]))([/\\-.])(?:0?[1-9]|1\\d|2[0-8])\\4(?:(?:1[6-9]|[2-9]\\d)?\\d{2})$";
	
	@Override
	public Object formatHorseFile(MultipartFile horseFile) {
		String csvHeader = String.format("%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s",
				"ExternalId", "Name", "Foaled", "Sire", "Dam", "Color",
				"Sex", "Avatar", "AddedDate", "ActiveStatus",
				"HorseLocation", "HorseStatus", "Type", "Category", "BonusScheme", "NickName"
		);
		StringBuilder builder = new StringBuilder();
		builder.append(String.join(",", csvHeader)).append("\n");
		
		try (
				InputStream inputStream = horseFile.getInputStream();
				BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
		) {
			CsvToBean<Horse> csvToBean = new CsvToBeanBuilder<Horse>(reader)
					.withType(Horse.class)
					.withFilter(line -> new EmptyLineFilter().allowLine(line))
					.withIgnoreLeadingWhiteSpace(true)
					.build();
			
			// MM/DD/YYYY format
			List<String> mdyFormatList = new ArrayList<>();
			// DD/MM/YYYY format
			List<String> ausFormatList = new ArrayList<>();

			boolean isMDYFormat = false;
			for (Horse horseCsv : csvToBean) {
				
				String date = getMultiMapSingleStringValue(horseCsv.getFoaled()).split("\\p{Z}")[0];
				
				if (date.matches(IS_DATE_MONTH_YEAR_FORMAT_PATTERN)) {
					ausFormatList.add(date);
				} else if (date.matches(IS_MONTH_DATE_YEAR_FORMAT_PATTERN)) {
					mdyFormatList.add(date);
				}
			}

			// if file contains only one date like: 03/27/2019 >> MM/DD/YYYY format.
			// if all date value in the file have format like: D/M/YYYY format (E.g: 5/6/2020) >> recheck in racingAustralia.horse
			if (CollectionUtils.isEmpty(mdyFormatList) && !CollectionUtils.isEmpty(ausFormatList)) {
				isMDYFormat = true;
				logger.info("Type of DATE is DD/MM/YYY format **OR** M/D/Y format >>>>>>>>> Please check.");
				
			} else if (!CollectionUtils.isEmpty(mdyFormatList)) {
				logger.info("Type of DATE is MM/DD/YYY format");
				
			} else {
				logger.info("Type of DATE is UNDEFINED");
			}

			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(horseFile.getInputStream()));
			CsvToBean<Horse> horseCsvToBean = new CsvToBeanBuilder<Horse>(bufferedReader)
					.withType(Horse.class)
					.withFilter(line -> new EmptyLineFilter().allowLine(line))
					.withIgnoreLeadingWhiteSpace(true)
					.build();
			for (Horse horseCsv : horseCsvToBean) {
				builder.append(horseCsv.toStandardObject(isMDYFormat));
			}
			Files.write(Paths.get("./horse.csv"), builder.toString().getBytes());
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	@Override
	public Object formatOpeningBalanceFile(MultipartFile file) {
		String[] csvHeader = new String[]{"OwnerName", "Current", "Over30", "Over60", "Over90"};
		StringBuilder builder = new StringBuilder();
		builder.append(String.join(",", csvHeader)).append("\n");
		
		try (
				Reader reader = new InputStreamReader(file.getInputStream());
		) {
//          Can't write Bean with BeanToCsv, you can only write CsvToBean or BeanToCsv
//			Writer writer = new FileWriter("./abc.csv");
//
//			final CustomMappingStrategy<OpeningBalance> mappingStrategy = new CustomMappingStrategy<>();
//			mappingStrategy.setType(OpeningBalance.class);
//
//			final StatefulBeanToCsv<OpeningBalance> beanToCsv = new StatefulBeanToCsvBuilder<OpeningBalance>(writer)
//					.withMappingStrategy(mappingStrategy)
//					.build();

			CsvToBean<OpeningBalance> csvToBean = new CsvToBeanBuilder<OpeningBalance>(reader)
					.withFilter(line -> new EmptyLineFilter().allowLine(line))
					.withType(OpeningBalance.class)
					.withIgnoreLeadingWhiteSpace(true)
					.build();

			for (OpeningBalance balanceCsv : csvToBean) {
				String ownerName = balanceCsv.getOwnerName();
				String current = String.valueOf(balanceCsv.getBalance());
				String over30 = String.valueOf(balanceCsv.getOver30());
				String over60 = String.valueOf(balanceCsv.getOver60());
				String over90 = String.valueOf(balanceCsv.getOver90());
				String row = String.format("%s,%s,%s,%s,%s%n",
						StringHelper.csvValue(ownerName),
						StringHelper.csvValue(current),
						StringHelper.csvValue(over30),
						StringHelper.csvValue(over60),
						StringHelper.csvValue(over90));
				builder.append(row);
			}
			
			Files.write(Paths.get("/home/logbasex/Desktop/data/POB-479-Wylie Dalziel Racing/submit/opening-balance.csv"), builder.toString().getBytes());
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	@Override
	public Object importTaxCode(MultipartFile file) throws IOException {
		String csvHeader = new StringJoiner(",")
				.add("Tax Code")
				.add("Description")
				.add("Rate")
				.toString();
		
		StringBuilder builder = new StringBuilder(csvHeader).append("\n");
		Reader reader = new InputStreamReader(file.getInputStream());
		List<TaxCodes> csvToBean = new CsvToBeanBuilder<TaxCodes>(reader)
				.withSkipLines(1)
				.withFilter(line -> new ValidLineFilter().allowLine(line))
				.withType(TaxCodes.class)
				.withIgnoreLeadingWhiteSpace(true)
				.build()
				.parse();
		
		for (TaxCodes taxCode : csvToBean) {
			String name = taxCode.getName();
			String description = taxCode.getDescription();
			Double rate = taxCode.getRate();
			String row = String.format("%s,%s,%s%n",
					StringHelper.csvValue(name),
					StringHelper.csvValue(description),
					StringHelper.csvValue(rate));
			builder.append(row);
			Files.write(Paths.get("C:\\Users\\conta\\OneDrive\\Desktop\\tax-codes.csv"), builder.toString().getBytes());
		}
		return null;
	}
}
