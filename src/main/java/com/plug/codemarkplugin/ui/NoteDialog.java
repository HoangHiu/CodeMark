package com.plug.codemarkplugin.ui;

import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.ui.components.JBScrollPane;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;

public class NoteDialog extends DialogWrapper {
    private JTextField headerField;
    private JTextArea contentArea;

    public NoteDialog() {
        super(true); // use current window as parent
        setTitle("Take Note");
        init();
    }

    @Override
    protected @Nullable JComponent createCenterPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));

        // Header
        headerField = new JTextField();
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.add(new JLabel("Header:"), BorderLayout.NORTH);
        headerPanel.add(headerField, BorderLayout.CENTER);

        // Content
        contentArea = new JTextArea(10, 40); // 10 rows, 40 cols
        contentArea.setLineWrap(true);
        contentArea.setWrapStyleWord(true);
        JBScrollPane scrollPane = new JBScrollPane(contentArea);

        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.add(new JLabel("Content:"), BorderLayout.NORTH);
        contentPanel.add(scrollPane, BorderLayout.CENTER);

        // Put together
        panel.add(headerPanel, BorderLayout.NORTH);
        panel.add(contentPanel, BorderLayout.CENTER);

        return panel;
    }

    public String getHeader() {
        return headerField.getText();
    }

    public String getContent() {
        return contentArea.getText();
    }
}

