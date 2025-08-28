package com.plug.codemarkplugin.actions;

import com.plug.codemarkplugin.model.Note;
import com.plug.codemarkplugin.services.NoteService;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.ui.Messages;

public class TakeNoteAction extends AnAction {

    @Override
    public void actionPerformed(AnActionEvent e) {
        Editor editor = e.getData(CommonDataKeys.EDITOR);
        if (editor == null) return;

        String selectedText = editor.getSelectionModel().getSelectedText();
        if (selectedText == null || selectedText.isEmpty()) {
            Messages.showInfoMessage("Please select code before taking a note.", "No Code Selected");
            return;
        }

        // Ask for header and content
        String header = Messages.showInputDialog("Enter Note Header:", "Take Note", Messages.getQuestionIcon());
        if (header == null) return;

        String content = Messages.showInputDialog("Enter Note Content:", "Take Note", Messages.getQuestionIcon());
        if (content == null) return;

        Note note = new Note(header, content, selectedText);
        NoteService.getInstance().addNote(note);
    }
}

