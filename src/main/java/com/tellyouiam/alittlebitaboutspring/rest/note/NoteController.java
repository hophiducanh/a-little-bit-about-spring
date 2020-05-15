package com.tellyouiam.alittlebitaboutspring.rest.note;

import com.tellyouiam.alittlebitaboutspring.entity.note.Note;
import com.tellyouiam.alittlebitaboutspring.service.note.NoteService;
import com.tellyouiam.alittlebitaboutspring.exception.CustomException;
import com.tellyouiam.alittlebitaboutspring.utils.FileHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.tellyouiam.alittlebitaboutspring.repository.note.NoteRepository;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

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
		note.setCode(null);
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
		
		return new ResponseEntity<>(result, new HttpHeaders(), HttpStatus.OK);
	}
	
	@RequestMapping(value = "/owner/automate-import-ownership", method = RequestMethod.POST)
	@ResponseBody
	public final ResponseEntity<Object> automateImportOwnerShip(@RequestPart final List<MultipartFile> ownershipFiles,
	                                                            @RequestParam(required = false) String dirName) {
		
		Map<Object, Object> result;
		try {
			result = noteService.automateImportOwnerShips(ownershipFiles);
			StringBuilder extractedNameData = (StringBuilder) result.get("extractedName");
			StringBuilder csvData = (StringBuilder) result.get("csvData");
			
			String extractedNamePath = FileHelper.getOutputFolder(dirName) + File.separator + "extracted-name-ownership.csv";
			Files.write(Paths.get(extractedNamePath), Collections.singleton(extractedNameData));
			
			String formattedPath = FileHelper.getOutputFolder(dirName) + File.separator + "formatted-ownership.csv";
			Files.write(Paths.get(formattedPath), Collections.singleton(csvData));
			
		} catch (CustomException | IOException e) {
			e.printStackTrace();
		}
		
		return new ResponseEntity<>(null, new HttpHeaders(), HttpStatus.OK);
	}
}
