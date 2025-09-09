package com.plug.codemarkplugin.actions;

import com.plug.codemarkplugin.enums.NoteTypeEnum;
import com.plug.codemarkplugin.model.Note;
import com.plug.codemarkplugin.services.NoteService;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.editor.Editor;
import com.plug.codemarkplugin.ui.NoteDialog;
import com.plug.codemarkplugin.ui.NotesToolWindowFactory;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class TakeNoteAction extends AnAction {

    public TakeNoteAction() {
        super("Take Note");
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        NoteDialog dialog = new NoteDialog();
        if (dialog.showAndGet()) {
            String header = dialog.getHeader();
            String content = dialog.getContent();
            NoteTypeEnum type =  dialog.getType();

            Editor editor = e.getData(CommonDataKeys.EDITOR);
            if (editor != null) {
                String selectedText = editor.getSelectionModel().getSelectedText();
                if (selectedText != null && !selectedText.isEmpty()) {
                    // Get file path
                    String filePath = e.getData(CommonDataKeys.VIRTUAL_FILE) != null
                            ? Objects.requireNonNull(e.getData(CommonDataKeys.VIRTUAL_FILE)).getPath()
                            : "Unknown File";


                    Note note = new Note(header, content, selectedText, type, filePath);
                    NoteService noteService = NoteService.getInstance(Objects.requireNonNull(e.getProject()));
                    noteService.addNote(note);

                    NotesToolWindowFactory.refreshNotes(e.getProject());
                }
            }
        }
    }
}


