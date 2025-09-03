package com.plug.codemarkplugin.enums;

import com.intellij.ui.JBColor;

import java.awt.*;

public enum NoteTypeEnum {
    TODO("TODO", JBColor.BLUE),
    URGENT("URGENT", JBColor.RED),
    EXPLANATION("EXPLANATION", JBColor.GREEN),
    MISCELLANEOUS("MISCELLANEOUS", JBColor.GRAY);

    private String value;
    private Color color;

    NoteTypeEnum(String value, Color color) {
        this.value = value;
        this.color = color;
    }

    public static NoteTypeEnum[] getEnumList() {
        return NoteTypeEnum.values();
    }

    public Color getColor() {
        return color;
    }
}
