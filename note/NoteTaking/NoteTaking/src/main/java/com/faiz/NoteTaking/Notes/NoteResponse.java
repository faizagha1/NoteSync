package com.faiz.NoteTaking.Notes;

public record NoteResponse(
                String title,
                String content) {
        public static NoteResponse fromNote(Note note) {
                return new NoteResponse(
                                note.getTitle(),
                                note.getContent());
        }

}
