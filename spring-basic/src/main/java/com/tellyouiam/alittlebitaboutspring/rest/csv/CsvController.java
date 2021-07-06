package com.tellyouiam.alittlebitaboutspring.rest.csv;

import com.opencsv.exceptions.CsvDataTypeMismatchException;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;
import com.tellyouiam.alittlebitaboutspring.service.csv.CsvService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
public class CsvController {
	
	@Autowired
	private CsvService csvService;
	
	@PostMapping("/horse")
	public final ResponseEntity<Object> automateImportHorse(@RequestPart(required = false) MultipartFile file,
	                                                        @RequestPart(required = false) MultipartFile ownershipFile,
	                                                        @RequestParam(required = false) String dirName) {
		Object result = csvService.formatHorseFile(file);
		
		return new ResponseEntity<>(result, new HttpHeaders(), HttpStatus.OK);
	}
	
	@PostMapping("/opening-balance")
	public final ResponseEntity<Object> automateImportHorse(@RequestPart(required = false) MultipartFile file) {
		Object result = csvService.formatOpeningBalanceFile(file);
		
		return new ResponseEntity<>(result, new HttpHeaders(), HttpStatus.OK);
	}
	
	@GetMapping("/tax")
	public final ResponseEntity<Object> importTaxCodes(@RequestPart(required = false) MultipartFile file) {
		Object result = null;
		try {
			result = csvService.importTaxCode(file);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return new ResponseEntity<>(result, new HttpHeaders(), HttpStatus.OK);
	}
	
	@PostMapping("/tax")
	public final ResponseEntity<Object> exportTaxCodes() {
		Object result = null;
		try {
			csvService.exportTaxCode();
		} catch (IOException | CsvRequiredFieldEmptyException | CsvDataTypeMismatchException e) {
			e.printStackTrace();
		}
		
		return new ResponseEntity<>(result, new HttpHeaders(), HttpStatus.OK);
	}
}
