package com.tellyouiam.alittlebitaboutspring.library.apachepoi;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Iterator;
import java.util.List;

public class WriteListToExcelFile {
	private static void writeCountryListToFile(String fileName, List<Country> countryList) throws Exception {
		
		XSSFWorkbook wb = new XSSFWorkbook();
		XSSFFont font = wb.createFont();
		font.setCharSet(XSSFFont.ANSI_CHARSET);
		XSSFSheet sheet = wb.createSheet();
		CellStyle style = wb.createCellStyle();
		style.setWrapText(true);
		
		if(fileName.endsWith("xlsx")){
			wb = new XSSFWorkbook();
		}
		
		wb.setSheetName(0, "Countries");
		
		Iterator<Country> iterator = countryList.iterator();
		
		int rowIndex = 0;
		while(iterator.hasNext()){
			Country country = iterator.next();
			Row row = sheet.createRow(rowIndex++);
			Cell cell0 = row.createCell(0);
			cell0.setCellValue(country.getName() + "\n"+ "VietNam");
			cell0.setCellStyle(style);
			Cell cell1 = row.createCell(1);
			cell1.setCellValue(country.getShortCode());
			cell1.setCellStyle(style);
		}
		
		//lets write the excel data to file now
		FileOutputStream fos = new FileOutputStream(new File(fileName));
		wb.write(fos);
		fos.close();
		System.out.println(fileName + " written successfully");
	}
	
	public static void main(String[] args) throws Exception {
		List<Country> list = ReadExcelFileToList.readExcelData("src/main/resources/xlsx-files/Sample.xlsx");
		WriteListToExcelFile.writeCountryListToFile("src/main/resources/xlsx-files/Countries.xls", list);
	}
}
