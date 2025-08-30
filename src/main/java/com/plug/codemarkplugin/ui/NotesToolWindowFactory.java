package com.plug.codemarkplugin.ui;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.components.JBTextArea;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import com.plug.codemarkplugin.model.Note;
import com.plug.codemarkplugin.services.NoteService;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

public class NotesToolWindowFactory implements ToolWindowFactory {

    private static JPanel notesPanel;  // static reference

    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
        JPanel mainPanel = new JPanel(new BorderLayout());

        notesPanel = new JPanel();
        notesPanel.setLayout(new BoxLayout(notesPanel, BoxLayout.Y_AXIS));

        JBScrollPane scrollPane = new JBScrollPane(notesPanel);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // Load initial notes
        refreshNotes();

        ContentFactory contentFactory = ContentFactory.getInstance();
        Content content = contentFactory.createContent(mainPanel, "", false);
        toolWindow.getContentManager().addContent(content);
    }

    /**
     * Static method to refresh the notes panel
     */
    public static void refreshNotes() {
        if (notesPanel == null) return;

        notesPanel.removeAll();

        // Fetch all saved notes
        List<Note> notes = NoteService.getInstance().getNotes();

        for (Note note : notes) {
            JPanel notePanel = new JPanel();
            notePanel.setLayout(new BoxLayout(notePanel, BoxLayout.Y_AXIS));
            notePanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

            // Header
            JBLabel headerLabel = new JBLabel(note.getHeader());
            headerLabel.setToolTipText(note.getFilePath()); // hover shows file path
            headerLabel.setFont(headerLabel.getFont().deriveFont(Font.BOLD));
            headerLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

            // Content (collapsed by default)
            JBTextArea contentArea = new JBTextArea(note.getContent());
            contentArea.setEditable(false);
            contentArea.setLineWrap(true);
            contentArea.setVisible(false);

            // Expand/collapse on header click
            headerLabel.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    contentArea.setVisible(!contentArea.isVisible());
                    notesPanel.revalidate();
                    notesPanel.repaint();
                }
            });

            notePanel.add(headerLabel);
            notePanel.add(contentArea);

            notesPanel.add(notePanel);
        }

        notesPanel.revalidate();
        notesPanel.repaint();
    }
}