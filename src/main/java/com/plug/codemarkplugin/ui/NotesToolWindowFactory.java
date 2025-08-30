package com.plug.codemarkplugin.ui;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import com.plug.codemarkplugin.model.Note;
import com.plug.codemarkplugin.services.NoteService;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;

public class NotesToolWindowFactory implements ToolWindowFactory {

    // static so other parts of the plugin (e.g. actions) can call refreshNotes()
    private static JPanel notesPanel;

    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
        JPanel mainPanel = new JPanel(new BorderLayout());

        notesPanel = new JPanel();
        notesPanel.setLayout(new BoxLayout(notesPanel, BoxLayout.Y_AXIS));
        notesPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        notesPanel.setBorder(BorderFactory.createEmptyBorder(6, 6, 6, 6));

        JBScrollPane scrollPane = new JBScrollPane(notesPanel,
                ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setBorder(null);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // initial population
        refreshNotes();

        ContentFactory contentFactory = ContentFactory.getInstance();
        Content content = contentFactory.createContent(mainPanel, "", false);
        toolWindow.getContentManager().addContent(content);
    }

    /**
     * Rebuilds the notes list. Safe to call from any thread (will schedule UI work on EDT).
     * Place this method inside NotesToolWindowFactory (as shown).
     */
    public static void refreshNotes() {
        if (notesPanel == null) return;

        Runnable uiUpdate = () -> {
            notesPanel.removeAll();

            java.util.List<Note> notes = NoteService.getInstance().getNotes();

            if (notes.isEmpty()) {
                JLabel empty = new JLabel("No notes yet");
                empty.setAlignmentX(Component.LEFT_ALIGNMENT);
                empty.setFont(empty.getFont().deriveFont(Font.ITALIC));
                notesPanel.add(empty);
            } else {
                for (Note note : notes) {
                    NotePanel notePanel = new NotePanel(note);

                    // Ensure left alignment; DO NOT override the panel's maximum height here.
                    notePanel.setAlignmentX(Component.LEFT_ALIGNMENT);

                    notesPanel.add(notePanel);
                    notesPanel.add(Box.createVerticalStrut(4)); // small gap
                }

                // push content to top
                notesPanel.add(Box.createVerticalGlue());
            }

            notesPanel.revalidate();
            notesPanel.repaint();
        };

        if (SwingUtilities.isEventDispatchThread()) {
            uiUpdate.run();
        } else {
            SwingUtilities.invokeLater(uiUpdate);
        }
    }
}