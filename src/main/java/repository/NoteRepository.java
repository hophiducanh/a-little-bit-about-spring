package repository;

import dto.Note;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author : Ho Anh
 * @since : 02/10/2019, Wed
 **/

@Repository
public interface NoteRepository extends JpaRepository<Note, Long> {

}
