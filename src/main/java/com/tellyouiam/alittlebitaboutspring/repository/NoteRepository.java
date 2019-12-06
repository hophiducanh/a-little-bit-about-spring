package com.tellyouiam.alittlebitaboutspring.repository;

import com.tellyouiam.alittlebitaboutspring.dto.Note;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author : Ho Anh
 * @since : 02/10/2019, Wed
 **/

@Repository
public interface NoteRepository extends JpaRepository<Note, Long> {
	List<Note> findNoteByTitleContaining();
}
