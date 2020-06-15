package com.tellyouiam.alittlebitaboutspring.rest.note;

import com.tellyouiam.alittlebitaboutspring.service.note.v2.NoteServiceV2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping(value = "/note")
public class NoteControllerV2 {
	
	@Autowired
	private NoteServiceV2 noteServiceV2;
	
	@PostMapping(value = "/owner/v2")
	public final ResponseEntity<Object> formatOwnerV2(@RequestPart MultipartFile ownerFile,
	                                                  @RequestParam String dirName) {
		Object result = null;
		try {
			result = noteServiceV2.formatOwnerV2(ownerFile, dirName);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return new ResponseEntity<>(result, new HttpHeaders(), HttpStatus.OK);
	}
	
	@PostMapping(value = "/horse/v2")
	public final ResponseEntity<Object> formatHorseV2(@RequestPart MultipartFile horseFile,
	                                                  @RequestParam String dirName) {
		Object result = null;
		try {
			result = noteServiceV2.formatHorseV2(horseFile, dirName);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return new ResponseEntity<>(result, new HttpHeaders(), HttpStatus.OK);
	}
	
	//union data two horse file.
	@PostMapping(value = "/horse/v2/merge")
	public final void mergeTwoHorseFile(@RequestPart MultipartFile first,
	                                    @RequestPart MultipartFile second,
	                                    @RequestParam String dirName) {
		try {
			noteServiceV2.mergeHorseFile(first, second, dirName);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
