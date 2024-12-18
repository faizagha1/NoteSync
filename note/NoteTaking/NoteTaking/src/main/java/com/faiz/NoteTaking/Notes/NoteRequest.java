package com.faiz.NoteTaking.Notes;


public record NoteRequest(
                String title,
                String content,
                Long ownerId) {

}
