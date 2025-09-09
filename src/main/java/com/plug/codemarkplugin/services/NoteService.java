package com.plug.codemarkplugin.services;

import com.intellij.openapi.components.*;
import com.intellij.openapi.project.Project;
import com.plug.codemarkplugin.model.Note;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

@Service(Service.Level.PROJECT) // mark as project service
@State(
        name = "PersonalNotes",
        storages = {@Storage(StoragePathMacros.WORKSPACE_FILE)} // per-project, personal
)
public final class NoteService implements PersistentStateComponent<NoteService.State> {

    public static class State {
        public List<Note> notes = new ArrayList<>();
    }

    private State state = new State();

    public static NoteService getInstance(Project project) {
        return project.getService(NoteService.class);
    }

    @Override
    public @NotNull State getState() {
        return state;
    }

    @Override
    public void loadState(@NotNull State state) {
        this.state = state;
    }

    // ---- API ----
    public void addNote(Note note) {
        state.notes.add(note);
    }

    public List<Note> getNotes() {
        return new ArrayList<>(state.notes);
    }
}