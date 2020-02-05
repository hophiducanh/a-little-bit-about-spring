package com.tellyouiam.alittlebitaboutspring.service;

import com.tellyouiam.alittlebitaboutspring.utils.CustomException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public interface NoteService {
	
	Object automateImportOwner(MultipartFile ownerFile);
	
	Object automateImportHorse(MultipartFile horseFile, MultipartFile ownershipFile) throws CustomException;
	
	Object automateImportOwnerShip(MultipartFile horseFile);
	
	Object prepareOwnership(MultipartFile ownershipFile) throws CustomException;
}
