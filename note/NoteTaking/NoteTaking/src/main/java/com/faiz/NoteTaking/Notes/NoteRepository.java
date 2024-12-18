package com.faiz.NoteTaking.Notes;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface NoteRepository extends JpaRepository<Note, Long> {

        List<Note> findByOwnerId(Long id);

        Note findByIdAndOwnerId(Long noteid, Long userid);

}
