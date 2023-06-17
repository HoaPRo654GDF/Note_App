package com.example.note_app.listeners;

import com.example.note_app.entities.Note;

public interface NotesListener {

    void onNoteClicked(Note note, int position);
}
