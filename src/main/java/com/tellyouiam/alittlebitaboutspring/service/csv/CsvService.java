package com.tellyouiam.alittlebitaboutspring.service.csv;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public interface CsvService {
	
	Object formatHorseFile(MultipartFile horseFile);
	
	Object formatOpeningBalanceFile(MultipartFile file);
}
