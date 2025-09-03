package com.plug.codemarkplugin.model;

import com.plug.codemarkplugin.enums.NoteTypeEnum;

public class Note {
    private final String header;
    private final String content;
    private final String codeSnippet;

    private final NoteTypeEnum noteType;
    private final String filePath;   // NEW

    public Note(String header, String content, String codeSnippet, NoteTypeEnum noteType, String filePath) {
        this.header = header;
        this.content = content;
        this.codeSnippet = codeSnippet;
        this.noteType = noteType;
        this.filePath = filePath;
    }

    public String getHeader() { return header; }
    public String getContent() { return content; }
    public String getCodeSnippet() { return codeSnippet; }
    public String getFilePath() { return filePath; }
    public NoteTypeEnum getNoteType() { return noteType; }
}

