package com.tellyouiam.alittlebitaboutspring.service.note.v2;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
public interface NoteServiceV2 {
	
	Object formatOwnerV2(MultipartFile ownerFile, String dirName) throws IOException;
	
	Object formatHorseV2(MultipartFile horseFile, String dirName) throws IOException;
	
	void mergeHorseFile(MultipartFile first, MultipartFile second, String dirName) throws IOException;
}
