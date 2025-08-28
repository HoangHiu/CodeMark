package com.plug.codemarkplugin.model;

public class Note {
    private final String header;
    private final String content;
    private final String codeSnippet;

    public Note(String header, String content, String codeSnippet) {
        this.header = header;
        this.content = content;
        this.codeSnippet = codeSnippet;
    }

    public String getHeader() { return header; }
    public String getContent() { return content; }
    public String getCodeSnippet() { return codeSnippet; }
}

