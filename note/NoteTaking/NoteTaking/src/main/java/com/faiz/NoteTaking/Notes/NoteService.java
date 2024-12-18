package com.faiz.NoteTaking.Notes;

import java.util.List;

import org.springframework.stereotype.Service;

import com.faiz.NoteTaking.User.User;
import com.faiz.NoteTaking.User.UserNotFoundException;
import com.faiz.NoteTaking.User.UserRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class NoteService {
    private final NoteRepository noteRepository;
    private final UserRepository userRepository;

    @Transactional
    public Long create(NoteRequest noteRequest) {
        if (noteRequest == null || noteRequest.ownerId() == null || noteRequest.title() == null
                || noteRequest.content() == null) {
            throw new NoteValidationException("Invalid note request");
        }

        User owner = userRepository.findById(noteRequest.ownerId())
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        Note note = Note.builder()
                .title(noteRequest.title())
                .content(noteRequest.content())
                .owner(owner)
                .build();

        owner.getNotes().add(note);
        userRepository.save(owner); 

        noteRepository.save(note); 
        return note.getId();
    }

    public List<NoteResponse> getAll(Long id) {
        List<Note> notes = noteRepository.findByOwnerId(id);
        return notes
                .stream()
                .map(NoteResponse::fromNote)
                .toList();
    }

    public NoteResponse getNoteById(Long userid, Long noteid) {
        Note note = noteRepository.findByIdAndOwnerId(noteid, userid);
        return NoteResponse.fromNote(note);
    }

    public NoteResponse updateNoteById(Long userid, Long noteid, NoteRequest noteRequest) {
        Note note = noteRepository.findByIdAndOwnerId(noteid, userid);

        if (noteRequest.title() != null) {
            note.setTitle(noteRequest.title());
        }
        if (noteRequest.content() != null) {
            note.setContent(noteRequest.content());
        }
        noteRepository.save(note);
        return NoteResponse.fromNote(note);
    }

    public void delelteNode(Long userid, Long noteid) {
        Note note = noteRepository.findByIdAndOwnerId(noteid, userid);
        noteRepository.delete(note);
    }

}
