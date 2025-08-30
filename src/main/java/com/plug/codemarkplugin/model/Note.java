package com.plug.codemarkplugin.model;

public class Note {
    private final String header;
    private final String content;
    private final String codeSnippet;
    private final String filePath;   // NEW

    public Note(String header, String content, String codeSnippet, String filePath) {
        this.header = header;
        this.content = content;
        this.codeSnippet = codeSnippet;
        this.filePath = filePath;
    }

    public String getHeader() { return header; }
    public String getContent() { return content; }
    public String getCodeSnippet() { return codeSnippet; }
    public String getFilePath() { return filePath; }
}

