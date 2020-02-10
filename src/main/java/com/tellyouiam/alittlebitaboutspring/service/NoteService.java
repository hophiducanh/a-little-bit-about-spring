package com.tellyouiam.alittlebitaboutspring.service;

import com.tellyouiam.alittlebitaboutspring.utils.CustomException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public interface NoteService {
	
	Object automateImportOwner(MultipartFile ownerFile, String dirName);
	
	Object automateImportHorse(MultipartFile horseFile, MultipartFile ownershipFile, String dirName) throws CustomException;
	
	Object automateImportOwnerShip(MultipartFile horseFile, String filePath);
	
	Object prepareOwnership(MultipartFile ownershipFile, String dirName) throws CustomException;
}
