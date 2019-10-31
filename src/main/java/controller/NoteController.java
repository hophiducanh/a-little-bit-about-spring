package controller;

import dto.Note;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import repository.NoteRepository;

import javax.validation.Valid;

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
	public Note createNote(@Valid @RequestBody Note note) {
		note.setCode(1000000011);
		return noteRepository.save(note);
	}
}
