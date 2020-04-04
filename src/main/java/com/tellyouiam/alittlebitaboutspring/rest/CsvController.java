package com.tellyouiam.alittlebitaboutspring.rest;

import com.tellyouiam.alittlebitaboutspring.service.CsvService;
import com.tellyouiam.alittlebitaboutspring.utils.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
public class CsvController {
	
	@Autowired
	private CsvService csvService;
	
	@PostMapping("/horse")
	public final ResponseEntity<Object> automateImportHorse(@RequestPart(required = false) MultipartFile file,
	                                                        @RequestPart(required = false) MultipartFile ownershipFile,
	                                                        @RequestParam(required = false) String dirName) {
		Object result = csvService.formatHorseFile(file);
		
		return new ResponseEntity<Object>(result, new HttpHeaders(), HttpStatus.OK);
	}
	
	@PostMapping("/opening-balance")
	public final ResponseEntity<Object> automateImportHorse(@RequestPart(required = false) MultipartFile file) {
		Object result = csvService.formatOpeningBalanceFile(file);
		
		return new ResponseEntity<Object>(result, new HttpHeaders(), HttpStatus.OK);
	}
}
