package com.tellyouiam.alittlebitaboutspring.service;

import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import com.opencsv.bean.CsvToBeanFilter;
import com.tellyouiam.alittlebitaboutspring.dto.csvformat.Horse;
import com.tellyouiam.alittlebitaboutspring.dto.csvformat.OpeningBalance;
import com.tellyouiam.alittlebitaboutspring.utils.StringHelper;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.ParseException;

@Service
public class CsvServiceImpl implements CsvService {
	
//	private static final String SAMPLE_CSV_FILE_PATH =
//			"C:\\Users\\conta\\OneDrive\\Desktop\\data\\POB-429-Meagher Racing\\Horse_List_06_02_20_test.csv";
	
	private static final String SAMPLE_CSV_FILE_PATH =
			"C:\\Users\\conta\\OneDrive\\Desktop\\data\\POB-429-Meagher Racing\\Horse_List_06_02_20.csv";
	
	@Override
	public Object formatHorseFile(MultipartFile horseFile) {
		try (
				Reader reader = Files.newBufferedReader(Paths.get(SAMPLE_CSV_FILE_PATH));
		) {
			CsvToBean<Horse> csvToBean = new CsvToBeanBuilder<Horse>(reader)
					.withType(Horse.class)
					.withIgnoreLeadingWhiteSpace(true)
					.build();
			
			for (Horse horseCsv : csvToBean) {
//				System.out.println("externalId : " + horseCsv.getExternalId().values().toArray(new String[1])[0]);
//				System.out.println("name : " + horseCsv.getName().values().toArray(new String[1])[0]);
//				System.out.println("foaled : " + horseCsv.getFoal().values().toArray(new java.time.LocalDate[0])[0]);
				System.out.println("foaled : " + horseCsv.getFoaled());
//				System.out.println("sire : " + horseCsv.getSire());
//				System.out.println("dam : " + horseCsv.getDam());
//				System.out.println("color : " + horseCsv.getColor());
//				System.out.println("sex : " + horseCsv.getSex().values().toArray(new String[1])[0]);
//				System.out.println("avatar : " + horseCsv.getAvatar());
//				System.out.println("addedDate : " + horseCsv.getAddedDate().values().toArray(new java.time.LocalDate[0])[0]);
//				System.out.println("activeStatus : " + horseCsv.getActiveStatus().values().toArray(new String[1])[0]);
//				System.out.println("currentLocation : " + horseCsv.getHorseLocation().values().toArray(new String[1])[0]);
//				System.out.println("currentStatus : " + horseCsv.getHorseStatus().values().toArray(new String[1])[0]);
//				System.out.println("type : " + horseCsv.getType());
//				System.out.println("category : " + horseCsv.getCategory());
//				System.out.println("bonusScheme : " + horseCsv.getBonusScheme().values().toArray(new String[1])[0]);
//				System.out.println("nickName : " + horseCsv.getNickName().values().toArray(new String[1])[0]);
				System.out.println("==========================");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	@Override
	public Object formatOpeningBalanceFile(MultipartFile file) {
		try (
				Reader reader = new InputStreamReader(file.getInputStream());
		) {
			/*
			 * This filter ignores empty lines from the input
			 */
			CsvToBean<OpeningBalance> csvToBean = new CsvToBeanBuilder<OpeningBalance>(reader)
					.withType(OpeningBalance.class)
					.withFilter(strings -> {
						for (String one : strings) {
							if (one != null && one.length() > 0) {
								return true;
							}
						}
						return false;
					})
					.withIgnoreLeadingWhiteSpace(true)
					.build();
			StringBuilder builder = new StringBuilder();
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
			
			Files.write(Paths.get("./abc.csv"), builder.toString().getBytes());
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
}
