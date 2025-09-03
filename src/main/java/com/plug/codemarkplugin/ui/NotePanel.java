package com.plug.codemarkplugin.ui;

import com.intellij.icons.AllIcons;
import com.intellij.ui.Gray;
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
    private Timer animationTimer;

    // animation config
    private static final int ANIMATION_DURATION_MS = 220;
    private static final int ANIMATION_FRAMES = 20;

    private int targetContentHeight;
    private int currentContentHeight;

    public NotePanel(Note note) {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
        setAlignmentX(Component.LEFT_ALIGNMENT);

        // Header
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

        Color typeColor = note.getNoteType().getColor();
        headerLabel.setForeground(typeColor);

        headerPanel.add(arrowLabel, BorderLayout.WEST);
        headerPanel.add(headerLabel, BorderLayout.CENTER);

        // Start by clamping NotePanel max height to header (so collapsed items don't reserve space)
        Dimension headerPref = headerPanel.getPreferredSize();
        int headerTotalHeight = headerPref.height + getInsets().top + getInsets().bottom;
        setMaximumSize(new Dimension(Integer.MAX_VALUE, headerTotalHeight));

        // Content area (text inside rounded box)
        JTextArea contentArea = new JTextArea(note.getContent());
        contentArea.setLineWrap(true);
        contentArea.setWrapStyleWord(true);
        contentArea.setEditable(false);
        contentArea.setForeground(UIManager.getColor("Label.foreground"));
        contentArea.setBackground(Gray._43);
        contentArea.setBorder(BorderFactory.createEmptyBorder(6, 6, 6, 6));

        contentScroll = new JBScrollPane(
                contentArea,
                ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER
        );
        contentScroll.setBorder(new RoundedBorder(note.getNoteType().getColor(), 8));
        contentScroll.getViewport().setOpaque(false);
        contentScroll.setOpaque(false);

        // Pre-calc content preferred size (used for animation target and sensible start width)
        int approxRowHeight = contentArea.getFontMetrics(contentArea.getFont()).getHeight();
        int rows = Math.min(10, Math.max(3, Math.max(1, contentArea.getLineCount())));
        int contentPrefHeight = rows * approxRowHeight + 12; // padding
        int contentPrefWidth = 200; // sensible default width if container not measured yet

        contentScroll.setPreferredSize(new Dimension(contentPrefWidth, contentPrefHeight));
        contentScroll.setMaximumSize(new Dimension(Integer.MAX_VALUE, contentPrefHeight));

        // Wrapper with indent (the rounded box will surround the scroll)
        contentWrapper = new JPanel(new BorderLayout());
        contentWrapper.setAlignmentX(Component.LEFT_ALIGNMENT); // left-align with header
        contentWrapper.setBorder(BorderFactory.createEmptyBorder(6, 18, 6, 6));
        contentWrapper.setOpaque(false);
        contentWrapper.add(contentScroll, BorderLayout.CENTER);

        // compute wrapper insets to include in target height
        Insets wrapperInsets = contentWrapper.getInsets();
        targetContentHeight = contentPrefHeight + wrapperInsets.top + wrapperInsets.bottom;

        // Start collapsed: preferred width = contentPrefWidth (not zero!), height = 0
        contentWrapper.setPreferredSize(new Dimension(contentPrefWidth, 0));
        contentWrapper.setMinimumSize(new Dimension(0, 0));
        contentWrapper.setMaximumSize(new Dimension(Integer.MAX_VALUE, 0));
        currentContentHeight = 0;

        // keep content scroll hidden until expansion begins to avoid clipped painting
        contentScroll.setVisible(false);

        // Toggle expand/collapse
        MouseAdapter toggle = new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (animationTimer != null && animationTimer.isRunning()) {
                    return; // ignore clicks while animating
                }
                expanded = !expanded;
                arrowLabel.setIcon(expanded ? AllIcons.Actions.ArrowCollapse : AllIcons.Actions.ArrowExpand);
                animateToggle(expanded);
            }
        };

        headerPanel.addMouseListener(toggle);
        arrowLabel.addMouseListener(toggle);

        // assemble
        add(headerPanel);
        add(contentWrapper);
    }

    private void animateToggle(boolean expand) {
        // stop previous animation if running
        if (animationTimer != null && animationTimer.isRunning()) {
            animationTimer.stop();
        }

        // compute sensible start width (avoid zero width) — prefer parent width if available
        final int startWidth;
        int sw = contentWrapper.getWidth();
        if (sw <= 0) {
            Container parent = getParent();
            if (parent != null && parent.getWidth() > 0) {
                sw = parent.getWidth();
            } else {
                Dimension ps = contentScroll.getPreferredSize();
                sw = (ps != null && ps.width > 0) ? ps.width : 200;
            }
        }
        startWidth = sw;

        final int startHeight = contentWrapper.getHeight();
        final int endHeight = expand ? targetContentHeight : 0;

        // When expanding, show the scroll so the border paints while growing
        if (expand) {
            contentScroll.setVisible(true);
            // DO NOT change NotePanel maximumSize here — leave it clamped so BoxLayout doesn't redistribute space.
            // Only operate on the wrapper's preferred/max sizes.
            contentWrapper.setMaximumSize(new Dimension(Integer.MAX_VALUE, endHeight));
        } else {
            // While collapsing, ensure wrapper can shrink
            contentWrapper.setMaximumSize(new Dimension(Integer.MAX_VALUE, startHeight));
        }

        final long startTime = System.currentTimeMillis();
        final int delay = Math.max(12, ANIMATION_DURATION_MS / ANIMATION_FRAMES); // ~60-80 FPS

        animationTimer = new Timer(delay, null);
        animationTimer.addActionListener(ev -> {
            float elapsed = System.currentTimeMillis() - startTime;
            float progress = Math.min(1f, elapsed / (float) ANIMATION_DURATION_MS);

            // ease-in-out (cosine)
            float eased = (float) (-0.5 * (Math.cos(Math.PI * progress) - 1));

            currentContentHeight = (int) (startHeight + (endHeight - startHeight) * eased);

            // clamp to valid bounds
            currentContentHeight = Math.max(0, Math.min(endHeight, currentContentHeight));

            // update only preferred height (keep width sensible)
            contentWrapper.setPreferredSize(new Dimension(startWidth, currentContentHeight));
            // while animating keep a max height to avoid BoxLayout distributing extra space to other components
            contentWrapper.setMaximumSize(new Dimension(Integer.MAX_VALUE, Math.max(1, currentContentHeight)));

            revalidateAndRepaintUpTree();

            if (progress >= 1f) {
                animationTimer.stop();
                animationTimer = null;

                if (expand) {
                    // finished expanding:
                    // allow wrapper to use natural preferred height but KEEP a sensible maximum (targetContentHeight) so BoxLayout won't stretch it
                    contentWrapper.setPreferredSize(null);
                    contentWrapper.setMaximumSize(new Dimension(Integer.MAX_VALUE, targetContentHeight));
                    // NOTE: do not change this NotePanel's max height here — leave it unclamped so it can be placed naturally by parent layout
                } else {
                    // finished collapsing: hide scroll, clamp wrapper and panel to header height
                    contentScroll.setVisible(false);

                    contentWrapper.setPreferredSize(new Dimension(startWidth, 0));
                    contentWrapper.setMaximumSize(new Dimension(Integer.MAX_VALUE, 0));

                    Dimension headerPref = headerPanel.getPreferredSize();
                    int headerTotalHeight = headerPref.height + getInsets().top + getInsets().bottom;
                    setMaximumSize(new Dimension(Integer.MAX_VALUE, headerTotalHeight));
                }

                revalidateAndRepaintUpTree();
            }
        });

        animationTimer.start();
    }

    private void revalidateAndRepaintUpTree() {
        revalidate();
        repaint();
        Container p = NotePanel.this.getParent();
        while (p != null) {
            p.revalidate();
            p.repaint();
            p = p.getParent();
        }
    }
}
