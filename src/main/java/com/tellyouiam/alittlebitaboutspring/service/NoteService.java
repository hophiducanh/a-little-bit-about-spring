package com.tellyouiam.alittlebitaboutspring.service;

import com.tellyouiam.alittlebitaboutspring.exception.CustomException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@Service
public interface NoteService {
	
	Object automateImportOwner(MultipartFile ownerFile, String dirName) throws CustomException;
	
	Object automateImportHorse(MultipartFile horseFile, MultipartFile ownershipFile, String dirName) throws CustomException;
	
	Map<Object, Object> automateImportOwnerShip(MultipartFile ownershipFile, String filePath) throws CustomException;
}
