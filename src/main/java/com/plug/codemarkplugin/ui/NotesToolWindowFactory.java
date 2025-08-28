package com.plug.codemarkplugin.ui;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.components.JBList;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import com.plug.codemarkplugin.model.Note;
import com.plug.codemarkplugin.services.NoteService;
import com.intellij.ui.components.JBScrollPane;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.util.List;

public class NotesToolWindowFactory implements ToolWindowFactory {
    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
        List<Note> notes = NoteService.getInstance().getNotes();

        DefaultListModel<String> listModel = new DefaultListModel<>();
        for (Note note : notes) {
            listModel.addElement(note.getHeader() + " - " + note.getCodeSnippet());
        }

        JBList<String> noteList = new JBList<>(listModel);
        JBScrollPane scrollPane = new JBScrollPane(noteList);

        ContentFactory contentFactory = ApplicationManager.getApplication().getService(ContentFactory.class);
        Content content = contentFactory.createContent(scrollPane, "", false);
        toolWindow.getContentManager().addContent(content);
    }
}

