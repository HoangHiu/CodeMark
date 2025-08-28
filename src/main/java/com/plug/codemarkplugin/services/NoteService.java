package com.plug.codemarkplugin.services;

import com.plug.codemarkplugin.model.Note;

import java.util.ArrayList;
import java.util.List;

public class NoteService {
    private static final NoteService INSTANCE = new NoteService();
    private final List<Note> notes = new ArrayList<>();

    private NoteService() {}

    public static NoteService getInstance() {
        return INSTANCE;
    }

    public void addNote(Note note) {
        notes.add(note);
    }

    public List<Note> getNotes() {
        return notes;
    }
}

