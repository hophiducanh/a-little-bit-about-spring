package com.tellyouiam.alittlebitaboutspring.rest;

import com.tellyouiam.alittlebitaboutspring.dto.Note;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import com.tellyouiam.alittlebitaboutspring.repository.NoteRepository;

import javax.validation.Valid;
import java.util.Date;

/**
 * @author : Ho Anh
 * @since : 02/10/2019, Wed
 **/

@RestController
@RequestMapping(value = "/note")
public class NoteController {

	@Autowired
	private NoteRepository noteRepository;

	@RequestMapping(value = "/create", method = RequestMethod.POST)
	@ResponseBody
	public final ResponseEntity<Object> createNote(@Valid @RequestBody Note note) {
		note.setCode(1000000011);
		note.setCreatedAt(new Date(0));
		note.setUpdatedAt(new Date(0));
		noteRepository.save(note);
		return new ResponseEntity<Object>(note, new HttpHeaders(), HttpStatus.OK);
	}
}
