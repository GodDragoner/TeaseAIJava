package me.goddragon.teaseai.utils.message;

public class TextTagElement {
    public enum Type { TEXT, TAG }

    public TextTagElement(String content, TextTagElement.Type type) {
        this.content = content;
        this.isTag = (type == Type.TAG);
    }

    public String getContent() {
        return content;
    }

    public boolean isTag() {
        return isTag;
    }

    private String content;
    private boolean isTag;
}
