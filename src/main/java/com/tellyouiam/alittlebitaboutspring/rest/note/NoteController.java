package com.tellyouiam.alittlebitaboutspring.rest.note;

import com.tellyouiam.alittlebitaboutspring.entity.note.Note;
import com.tellyouiam.alittlebitaboutspring.service.note.v1.NoteService;
import com.tellyouiam.alittlebitaboutspring.exception.CustomException;
import com.tellyouiam.alittlebitaboutspring.utils.io.FileHelper;
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
	//https://www.vojtechruzicka.com/field-dependency-injection-considered-harmful/
	@Autowired
	private NoteRepository noteRepository;
	
	// field injection
//	@Autowired
//	private NoteService noteService;
	
	//constructor injection
//	private final NoteService noteService;
//
//	public NoteController(NoteService noteService) {
//		this.noteService = noteService;
//	}
	
	//setter injection
	private NoteService noteService;
	
	@Autowired
	public void setDependencyA(NoteService noteService) {
		this.noteService = noteService;
	}
	
	@PostMapping
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
	public final ResponseEntity<Object> deleteNote(@Valid @RequestBody Note note) {
		note.setCode(1000000011);
		note.setCreatedAt(new Date(0));
		note.setUpdatedAt(new Date(0));
		noteRepository.delete(note);
		noteRepository.delete(null);
		return new ResponseEntity<Object>(note, new HttpHeaders(), HttpStatus.OK);
	}
	
	@RequestMapping(method = RequestMethod.GET)
	public final ResponseEntity<Object> getDate() {
		
		List<Note> notes = noteRepository.findByCode(15);
		notes.forEach(n -> System.out.println(n.getCode()));
		return new ResponseEntity<Object>(notes, new HttpHeaders(), HttpStatus.OK);
		
	}
	
	@RequestMapping(value = "/owner/automate-import-owner", method = RequestMethod.POST)
	public final ResponseEntity<Object> automateImportOwner(@RequestPart MultipartFile ownerFile,
	                                                        @RequestParam String dirName) {
		Object result = null;
		try {
			result = noteService.automateImportOwner(ownerFile, dirName);
		} catch (CustomException e) {
			e.printStackTrace();
		}
		
		return new ResponseEntity<>(result, new HttpHeaders(), HttpStatus.OK);
	}
	
	@RequestMapping(value = "/owner/automate-import-horse", method = RequestMethod.POST)
	public final ResponseEntity<Object> automateImportHorse(@RequestPart MultipartFile horseFile,
                                                            @RequestPart(required = false) List<MultipartFile> ownershipFiles,
	                                                        @RequestParam String dirName) {
		Object result = null;
		try {
			result = noteService.automateImportHorse(horseFile, ownershipFiles, dirName);
		} catch (CustomException e) {
			e.printStackTrace();
		}
		return new ResponseEntity<>(result, new HttpHeaders(), HttpStatus.OK);
	}
	
	@RequestMapping(value = "/owner/automate-import-ownership", method = RequestMethod.POST)
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
	
	//https://stackoverflow.com/questions/38811606/what-is-the-default-request-method-type-for-the-request-mapping
	//if not specify mapping method >> all mapping method are accepted.
	@RequestMapping(value = "/test")
	public final ResponseEntity<Object> diffRequestMappingAndGetMapping() {
		String result = "Difference request mapping get method and getmapping";
		return new ResponseEntity<>(result, HttpStatus.OK);
	}
	
	//path and value of request mapping are the same.
	@RequestMapping(path = "/test/**")
	public final ResponseEntity<Object> testRequestMapping() {
		String result = "Testing was successful";
		return new ResponseEntity<>(result, HttpStatus.OK);
	}
	
	//@RequestMapping(path = "/testPath", value = "/test1") //can't compiled. path and value must be the same value
	//https://gist.github.com/codeman688/575ce10fb6b6de5fe69fe63dab4ebfcc
	//https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/web/bind/annotation/RequestMapping.html#path--
	@RequestMapping(path = "/testPath", value = "/testPath")
	public final ResponseEntity<Object> testPathAndValue() {
		String result = "Difference path and value in request mapping";
		return new ResponseEntity<>(result, HttpStatus.OK);
	}
}
