package com.plug.codemarkplugin.actions;

import com.plug.codemarkplugin.model.Note;
import com.plug.codemarkplugin.services.NoteService;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.editor.Editor;
import com.plug.codemarkplugin.ui.NoteDialog;
import org.jetbrains.annotations.NotNull;

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

            Editor editor = e.getData(CommonDataKeys.EDITOR);
            if (editor != null) {
                String selectedText = editor.getSelectionModel().getSelectedText();
                if (selectedText != null && !selectedText.isEmpty()) {
                    Note note = new Note(header, content, selectedText);
                    NoteService.getInstance().addNote(note);
                }
            }
        }
    }
}


