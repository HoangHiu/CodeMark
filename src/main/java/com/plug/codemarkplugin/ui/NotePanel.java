package com.plug.codemarkplugin.ui;

import com.intellij.icons.AllIcons;
import com.intellij.ui.components.JBScrollPane;
import com.plug.codemarkplugin.model.Note;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class NotePanel extends JPanel {
    private boolean expanded = false;
    private final JPanel contentWrapper;
    private final JLabel arrowLabel;
    private final JPanel headerPanel;
    private final JScrollPane contentScroll;

    public NotePanel(Note note) {
        // vertical layout so height is only what is needed
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
        setAlignmentX(Component.LEFT_ALIGNMENT);

        // Header: fixed preferred height so it won't shift
        headerPanel = new JPanel(new BorderLayout());
        headerPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        headerPanel.setOpaque(false);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
        headerPanel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        arrowLabel = new JLabel(AllIcons.Actions.ArrowExpand);
        arrowLabel.setBorder(BorderFactory.createEmptyBorder(0, 2, 0, 8));
        JLabel headerLabel = new JLabel(note.getHeader());
        headerLabel.setFont(headerLabel.getFont().deriveFont(Font.BOLD));
        headerLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 2));

        headerPanel.add(arrowLabel, BorderLayout.WEST);
        headerPanel.add(headerLabel, BorderLayout.CENTER);

        // Make header a fixed height so collapsed panel holds minimal vertical space
        Dimension headerPref = headerPanel.getPreferredSize();
        headerPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, headerPref.height));

        // Content area
        JTextArea contentArea = new JTextArea(note.getContent());
        contentArea.setLineWrap(true);
        contentArea.setWrapStyleWord(true);
        contentArea.setEditable(false);
        // match background to avoid looking like a separate block
        contentArea.setBackground(UIManager.getColor("Panel.background"));
        contentArea.setBorder(BorderFactory.createEmptyBorder(6, 0, 6, 0));

        contentScroll = new JBScrollPane(contentArea,
                ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        contentScroll.setBorder(null);
        contentScroll.getViewport().setOpaque(false);
        contentScroll.setOpaque(false);

        // Give the scroll a reasonable preferred height based on content
        int rows = Math.min(10, Math.max(3, contentArea.getLineCount()));
        int approxRowHeight = contentArea.getFontMetrics(contentArea.getFont()).getHeight();
        int prefHeight = rows * approxRowHeight + 12;
        contentScroll.setPreferredSize(new Dimension(200, prefHeight));
        contentScroll.setMaximumSize(new Dimension(Integer.MAX_VALUE, prefHeight));

        // Wrap scroll in a panel to control indentation/padding
        contentWrapper = new JPanel(new BorderLayout());
        contentWrapper.setAlignmentX(Component.LEFT_ALIGNMENT);
        contentWrapper.setBorder(BorderFactory.createEmptyBorder(6, 18, 6, 6)); // slight indent
        contentWrapper.setOpaque(false);
        contentWrapper.add(contentScroll, BorderLayout.CENTER);
        contentWrapper.setVisible(false); // start collapsed

        // Toggle logic
        MouseAdapter toggle = new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                expanded = !expanded;
                arrowLabel.setIcon(expanded ? AllIcons.Actions.ArrowCollapse : AllIcons.Actions.ArrowExpand);
                contentWrapper.setVisible(expanded);

                // When collapsed, limit max height to header only
                if (!expanded) {
                    // collapse: prevent height from reserving more space
                    Dimension hp = headerPanel.getPreferredSize();
                    setMaximumSize(new Dimension(Integer.MAX_VALUE, hp.height + getInsets().top + getInsets().bottom));
                } else {
                    // expand: allow to grow vertically
                    setMaximumSize(new Dimension(Integer.MAX_VALUE, Short.MAX_VALUE));
                    // also expand the contentScroll's maximum so it can show more lines if needed
                    contentScroll.setMaximumSize(new Dimension(Integer.MAX_VALUE, Short.MAX_VALUE));
                }

                revalidate();
                repaint();

                // revalidate upstream parents too (BoxLayout needs parent updates)
                Container p = NotePanel.this.getParent();
                while (p != null) {
                    p.revalidate();
                    p.repaint();
                    p = p.getParent();
                }
            }
        };

        headerPanel.addMouseListener(toggle);
        arrowLabel.addMouseListener(toggle);

        // initial collapsed state: limit maximum size to header height
        Dimension hp = headerPanel.getPreferredSize();
        setMaximumSize(new Dimension(Integer.MAX_VALUE, hp.height + getInsets().top + getInsets().bottom));

        // assemble
        add(headerPanel);
        add(contentWrapper);
    }
}
