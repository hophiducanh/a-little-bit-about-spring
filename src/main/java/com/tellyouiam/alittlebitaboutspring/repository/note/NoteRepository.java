package com.tellyouiam.alittlebitaboutspring.repository.note;

import com.tellyouiam.alittlebitaboutspring.dto.note.Note;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author : Ho Anh
 * @since : 02/10/2019, Wed
 **/

@Repository
public interface NoteRepository extends JpaRepository<Note, Integer>, JpaSpecificationExecutor<Note> {
	@Query("select n from Note n where n.code = ?1")
	List<Note> findByCode(Integer code);
}
