package com.tellyouiam.alittlebitaboutspring.service.csv;

import com.opencsv.exceptions.CsvDataTypeMismatchException;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
public interface CsvService {
	
	Object formatHorseFile(MultipartFile horseFile);
	
	Object formatOpeningBalanceFile(MultipartFile file);
	
	Object importTaxCode(MultipartFile file) throws IOException;
	
	void exportTaxCode() throws IOException, CsvDataTypeMismatchException, CsvRequiredFieldEmptyException;
}
