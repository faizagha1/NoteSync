package com.faiz.NoteTaking.Notes;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("api/v1/notes")
@RequiredArgsConstructor
public class NoteController {
    private final NoteService noteService;

    @PostMapping
    public ResponseEntity<Long> create(
            @RequestBody NoteRequest noteRequest) {
        return ResponseEntity.ok(noteService.create(noteRequest));
    }

    @GetMapping("all/{userid}")
    public ResponseEntity<List<NoteResponse>> getAll(@PathVariable Long userid) {
        List<NoteResponse> notes = noteService.getAll(userid);
        return ResponseEntity.ok(notes);
    }

    @GetMapping("{userid}/{noteid}")
    public ResponseEntity<NoteResponse> getNoteById(
            @PathVariable Long userid,
            @PathVariable Long noteid) {
        NoteResponse note = noteService.getNoteById(userid, noteid);
        return ResponseEntity.ok(note);
    }

    @PutMapping("{userid}/{noteid}")
    public ResponseEntity<NoteResponse> updateNote(
            @PathVariable Long userid,
            @PathVariable Long noteid,
            @RequestBody NoteRequest noteRequest) {

        NoteResponse note = noteService.updateNoteById(userid, noteid, noteRequest);
        return ResponseEntity.ok(note);
    }

    @DeleteMapping("{userid}/{noteid}")
    public ResponseEntity<Void> deleteNote(
            @PathVariable Long userid,
            @PathVariable Long noteid) {
        noteService.delelteNode(userid, noteid);
        return ResponseEntity.noContent().build();
    }
}
