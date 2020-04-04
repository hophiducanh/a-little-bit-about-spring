package com.tellyouiam.alittlebitaboutspring.rest;

import com.tellyouiam.alittlebitaboutspring.dto.note.Note;
import com.tellyouiam.alittlebitaboutspring.service.NoteService;
import com.tellyouiam.alittlebitaboutspring.utils.CustomException;
import com.tellyouiam.alittlebitaboutspring.utils.HttpReqRespUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.tellyouiam.alittlebitaboutspring.repository.NoteRepository;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.util.Date;
import java.util.List;

/**
 * @author : Ho Anh
 * @since : 02/10/2019, Wed
 **/

@RestController
@RequestMapping(value = "/note")
public class NoteController {
	
	@Autowired
	private NoteRepository noteRepository;
	
	@Autowired
	private NoteService noteService;
	
	@PostMapping
	@ResponseBody
	public final ResponseEntity<Object> createNote(@Valid @RequestBody Note note) {
		note.setCode(1000000011);
		note.setCreatedAt(new Date(0));
		note.setUpdatedAt(new Date(0));
		List<Note> notes = noteRepository.findByCode(null);
		System.out.println(notes.size());
		noteRepository.save(note);
		return new ResponseEntity<Object>(note, new HttpHeaders(), HttpStatus.OK);
	}
	
	@DeleteMapping
	@ResponseBody
	public final ResponseEntity<Object> deleteNote(@Valid @RequestBody Note note) {
		note.setCode(1000000011);
		note.setCreatedAt(new Date(0));
		note.setUpdatedAt(new Date(0));
		noteRepository.delete(note);
		noteRepository.delete(null);
		return new ResponseEntity<Object>(note, new HttpHeaders(), HttpStatus.OK);
	}
	
	@RequestMapping(method = RequestMethod.GET)
	@ResponseBody
	public final ResponseEntity<Object> getDate() {
//		Date timestamp = Objects.requireNonNull(noteRepository.findById(1).orElse(null)).getTestDate();
//		Date date = Objects.requireNonNull(noteRepository.findById(1).orElse(null)).getCreatedAt();
//		List<Date> dates = Arrays.asList(timestamp, date);
		
		List<Note> notes = noteRepository.findByCode(15);
		notes.forEach(n -> System.out.println(n.getCode()));
		return new ResponseEntity<Object>(notes, new HttpHeaders(), HttpStatus.OK);
		
	}
	
	@RequestMapping(value = "/owner/automate-import-owner", method = RequestMethod.POST)
	@ResponseBody
	public final ResponseEntity<Object> automateImportOwner(@RequestPart MultipartFile ownerFile,
	                                                        @RequestParam String dirName) {
		Object result = null;
		try {
			result = noteService.automateImportOwner(ownerFile, dirName);
		} catch (CustomException e) {
			e.printStackTrace();
		}
		
		return new ResponseEntity<Object>(result, new HttpHeaders(), HttpStatus.OK);
	}
	
	@RequestMapping(value = "/owner/automate-import-horse", method = RequestMethod.POST)
	@ResponseBody
	public final ResponseEntity<Object> automateImportHorse(@RequestPart MultipartFile horseFile,
                                                            @RequestPart(required = false) MultipartFile ownershipFile,
	                                                        @RequestParam String dirName) {
		Object result = null;
		try {
			result = noteService.automateImportHorse(horseFile, ownershipFile, dirName);
		} catch (CustomException e) {
			e.printStackTrace();
		}
		
		return new ResponseEntity<Object>(result, new HttpHeaders(), HttpStatus.OK);
	}
	
	@RequestMapping(value = "/owner/automate-import-ownership", method = RequestMethod.POST)
	@ResponseBody
	public final ResponseEntity<Object> automateImportOwnerShip(@RequestPart MultipartFile ownershipFile,
	                                                            @RequestParam(required = false) String filePath) {
		Object result = noteService.automateImportOwnerShip(ownershipFile, filePath);
		
		return new ResponseEntity<Object>(result, new HttpHeaders(), HttpStatus.OK);
	}
	
	@RequestMapping(value = "/owner/prepared-ownership", method = RequestMethod.POST)
	@ResponseBody
	public final ResponseEntity<Object> prepareOwnership(@RequestPart MultipartFile ownershipFile,
	                                                     @RequestParam String dirName) {
		Object result = null;
		try {
			result = noteService.prepareOwnership(ownershipFile, dirName);
		} catch (CustomException e) {
			e.printStackTrace();
		}
		
		return new ResponseEntity<Object>(HttpReqRespUtils.getClientIpAddressIfServletRequestExist(), new HttpHeaders(), HttpStatus.OK);
	}
}
