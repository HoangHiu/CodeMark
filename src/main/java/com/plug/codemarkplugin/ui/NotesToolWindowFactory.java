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
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class NotesToolWindowFactory implements ToolWindowFactory {

    private static final Map<Project, JPanel> projectPanels = new ConcurrentHashMap<>();

    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
        JPanel mainPanel = new JPanel(new BorderLayout());

        JPanel notesPanel = new JPanel();
        notesPanel.setLayout(new BoxLayout(notesPanel, BoxLayout.Y_AXIS));
        notesPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        notesPanel.setBorder(BorderFactory.createEmptyBorder(6, 6, 6, 6));

        JBScrollPane scrollPane = new JBScrollPane(
                notesPanel,
                ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER
        );
        scrollPane.setBorder(null);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // Save the panel for this project
        projectPanels.put(project, notesPanel);

        // Initial population
        refreshNotes(project);

        ContentFactory contentFactory = ContentFactory.getInstance();
        Content content = contentFactory.createContent(mainPanel, "", false);
        toolWindow.getContentManager().addContent(content);
    }

    /**
     * Rebuilds the notes list for a specific project.
     */
    public static void refreshNotes(Project project) {
        JPanel notesPanel = projectPanels.get(project);
        if (notesPanel == null) return;

        Runnable uiUpdate = () -> {
            notesPanel.removeAll();

            List<Note> notes = project.getService(NoteService.class).getNotes();

            if (notes.isEmpty()) {
                JLabel empty = new JLabel("No notes yet");
                empty.setAlignmentX(Component.LEFT_ALIGNMENT);
                empty.setFont(empty.getFont().deriveFont(Font.ITALIC));
                notesPanel.add(empty);
            } else {
                for (Note note : notes) {
                    NotePanel notePanel = new NotePanel(note);
                    notePanel.setAlignmentX(Component.LEFT_ALIGNMENT);
                    notesPanel.add(notePanel);
                    notesPanel.add(Box.createVerticalStrut(4));
                }
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

    /**
     * Remove panel when project closes to prevent memory leaks.
     */
    public static void disposeProject(Project project) {
        projectPanels.remove(project);
    }
}