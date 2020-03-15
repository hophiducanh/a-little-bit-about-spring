package com.tellyouiam.alittlebitaboutspring.service;

import com.opencsv.CSVReader;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;
import com.opencsv.exceptions.CsvDataTypeMismatchException;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;
import com.tellyouiam.alittlebitaboutspring.converter.CustomMappingStrategy;
import com.tellyouiam.alittlebitaboutspring.dto.csvformat.Horse;
import com.tellyouiam.alittlebitaboutspring.dto.csvformat.OpeningBalance;
import com.tellyouiam.alittlebitaboutspring.filter.EmptyLineFilter;
import com.tellyouiam.alittlebitaboutspring.utils.StringHelper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;

@Service
public class CsvServiceImpl implements CsvService {
	
	@Override
	public Object formatHorseFile(MultipartFile horseFile) {
		String csvHeader = String.format("%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s",
				"ExternalId", "Name", "Foaled", "Sire", "Dam", "Color",
				"Sex", "Avatar", "AddedDate", "ActiveStatus/Status",
				"CurrentLocation/HorseLocation", "CurrentStatus/HorseStatus",
				"Type", "Category", "BonusScheme", "NickName"
		);
		StringBuilder builder = new StringBuilder();
		builder.append(String.join(",", csvHeader)).append("\n");

		try (
				Reader reader = new InputStreamReader(horseFile.getInputStream());
		) {
			CsvToBean<Horse> csvToBean = new CsvToBeanBuilder<Horse>(reader)
					.withType(Horse.class)
					.withFilter(line -> new EmptyLineFilter().allowLine(line))
					.withIgnoreLeadingWhiteSpace(true)
					.build();

			for (Horse horseCsv : csvToBean) {
				builder.append(horseCsv.toStandardObject());
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
			/*
			 * This filter ignores empty lines from the input
			 */
			CsvToBean<OpeningBalance> csvToBean = new CsvToBeanBuilder<OpeningBalance>(reader)
					.withType(OpeningBalance.class)
					.withFilter(line -> new EmptyLineFilter().allowLine(line))
					.withIgnoreLeadingWhiteSpace(true)
					.build();

//          Can't write Bean with BeanToCsv, you can only write CsvToBean or BeanToCsv
//			Writer writer = new FileWriter("./abc.csv");
//
//			final CustomMappingStrategy<OpeningBalance> mappingStrategy = new CustomMappingStrategy<>();
//			mappingStrategy.setType(OpeningBalance.class);
//
//			final StatefulBeanToCsv<OpeningBalance> beanToCsv = new StatefulBeanToCsvBuilder<OpeningBalance>(writer)
//					.withMappingStrategy(mappingStrategy)
//					.build();
			
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
			
			Files.write(Paths.get("./opening-balance.csv"), builder.toString().getBytes());
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
}
