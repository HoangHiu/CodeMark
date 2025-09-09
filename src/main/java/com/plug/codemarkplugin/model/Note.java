package com.plug.codemarkplugin.model;

import com.plug.codemarkplugin.enums.NoteTypeEnum;

public class Note {
    private String header;
    private String content;
    private String codeSnippet;
    private NoteTypeEnum noteType;
    private String filePath;

    public Note() { }

    public Note(String header, String content, String codeSnippet, NoteTypeEnum noteType, String filePath) {
        this.header = header;
        this.content = content;
        this.codeSnippet = codeSnippet;
        this.noteType = noteType;
        this.filePath = filePath;
    }

    // --- Getters ---
    public String getHeader() { return header; }
    public String getContent() { return content; }
    public String getCodeSnippet() { return codeSnippet; }
    public String getFilePath() { return filePath; }
    public NoteTypeEnum getNoteType() { return noteType; }

    // --- Setters (needed for persistence) ---
    public void setHeader(String header) { this.header = header; }
    public void setContent(String content) { this.content = content; }
    public void setCodeSnippet(String codeSnippet) { this.codeSnippet = codeSnippet; }
    public void setFilePath(String filePath) { this.filePath = filePath; }
    public void setNoteType(NoteTypeEnum noteType) { this.noteType = noteType; }
}
