package me.goddragon.teaseai.utils.message;

import java.util.ArrayList;
import java.util.List;

public class TextTagElementParser {
    /**
     * Split text containing tags into an array of separated text and tag elements.
     * 
     * For example:
     *   split("Mary <b>had</b> a <i>little</i> lamb");
     * results in:
     *   [ {"Mary "}, {"b"}, {"had"}, {"/b"}, {" a "}, {"i"}, {"little"}, {"/i"}, {" lamb"} ]
     * where isTag() returns true for the following objects:
     *     {"b"}, {"/b"}, {"i"}, {"/i"}
     * 
     * The splitting is impartial of the tag content, finding anything starting and ending with
     * '<' and '>' respectively.
     * 
     * Tag characters can be escaped with a backslash if required, for example:
     *   split("Sad :‑< and angry >:[");
     * results in:
     *   [ {"Sad :‑"}, {" and angry "}, {":["} ]
     * whereas:
     *   split("Sad :‑\\< and angry \\>:[");
     * results in:
     *   [ {"Sad :‑< and angry >:["} ]
     */
    public static List<TextTagElement> split(String text) {
        return new TextTagElementParser(text).listOfElements;
    }

    private TextTagElementParser(String text) {
        for (char c : text.toCharArray()) {
            if (isEscaping) {
                elementText.append(c);
                isEscaping = false;
            } else {
                parseCharacter(c);
            }
        }

        final String remainingText = elementText.toString();
        if (!remainingText.isEmpty())
            listOfElements.add(new TextTagElement(remainingText, TextTagElement.Type.TEXT));
    }

    private void parseCharacter(char c) {
        switch (c) {
            case '\\':
                isEscaping = true;
                break;
            case '<':
                processOpenTag();
                break;
            case '>':
                processCloseTag();
                break;
            default:
                elementText.append(c);
                break;
        }
    }

    private void processOpenTag() {
        if (isInTag) {
            elementText.append('<');
        } else {
            final String contentText = elementText.toString();
            elementText = new StringBuilder();
            if (!contentText.isEmpty())
                listOfElements.add(new TextTagElement(contentText, TextTagElement.Type.TEXT));
            isInTag = true;
        }
    }

    private void processCloseTag() {
        if (isInTag) {
            final String tagText = elementText.toString();
            elementText = new StringBuilder();
            if (!tagText.isEmpty())
                listOfElements.add(new TextTagElement(tagText, TextTagElement.Type.TAG));
            isInTag = false;
        } else {
            elementText.append('>');
        }
    }

    private final List<TextTagElement> listOfElements = new ArrayList<>();
    private StringBuilder elementText = new StringBuilder();
    private boolean isEscaping = false;
    private boolean isInTag = false;
}
