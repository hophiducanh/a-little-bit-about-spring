package com.tellyouiam.alittlebitaboutspring.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public interface NoteService {
	
	Object automateImportOwner(MultipartFile ownerFile);
	
	Object automateImportHorse(MultipartFile horseFile);
	
	Object automateImportOwnerShip(MultipartFile horseFile);
	
	Object prepareOwnership(MultipartFile ownershipFile);
}
