package com.tellyouiam.alittlebitaboutspring.service.note.v1;

import com.stackify.apm.Trace;
import com.tellyouiam.alittlebitaboutspring.exception.CustomException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Service
public interface NoteService {
	
	Object automateImportOwner(MultipartFile ownerFile, String dirName) throws CustomException;
	
	Object automateImportHorse(MultipartFile horseFile, List<MultipartFile> ownershipFiles, String dirName) throws CustomException;
	
	Map<Object, Object> automateImportOwnerShips(List<MultipartFile> ownershipFiles) throws CustomException;
	
	void reformatName(MultipartFile file, String dirname) throws CustomException, IOException;
	
	Map<Object, Object> reformatOwnership(MultipartFile file, String dirName);
	
	Object transformMultipartFile(MultipartFile file) throws IOException;
	
	String test();
}
