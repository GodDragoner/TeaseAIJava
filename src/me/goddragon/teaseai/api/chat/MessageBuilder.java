package me.goddragon.teaseai.api.chat;

import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by GodDragon on 19.06.2018.
 */
public class MessageBuilder {

    private final List<Text> texts = new ArrayList<>();
    private FontWeight latestMessageFontWeight = FontWeight.NORMAL;

    public MessageBuilder(String text) {
        addText(text);
    }

    public MessageBuilder family(String font) {
        return font(font);
    }

    public MessageBuilder font(String font) {
        Font latestFont = getLatestText().getFont();
        getLatestText().setFont(Font.font(font, latestMessageFontWeight, FontPosture.ITALIC, latestFont.getSize()));
        return this;
    }

    public MessageBuilder size(double size) {
        Font latestFont = getLatestText().getFont();
        getLatestText().setFont(Font.font(latestFont.getFamily(), latestMessageFontWeight, FontPosture.ITALIC, size));
        return this;
    }

    public MessageBuilder italics() {
        return italic();
    }

    public MessageBuilder italic() {
        Font latestFont = getLatestText().getFont();
        getLatestText().setFont(Font.font(latestFont.getFamily(), latestMessageFontWeight, FontPosture.ITALIC, latestFont.getSize()));
        return this;
    }

    public MessageBuilder strikethrough() {
        getLatestText().setStrikethrough(true);
        return this;
    }

    public MessageBuilder underline() {
        getLatestText().setUnderline(true);
        return this;
    }

    public MessageBuilder bold() {
        Font latestFont = getLatestText().getFont();
        getLatestText().setFont(Font.font(latestFont.getFamily(), FontWeight.BOLD, latestFont.getSize()));
        latestMessageFontWeight = FontWeight.BOLD;
        return this;
    }

    public MessageBuilder color(int red, int green, int blue) {
        getLatestText().setFill(Color.rgb(red, green, blue));
        return this;
    }

    public MessageBuilder color(String color) {
        getLatestText().setFill(Color.valueOf(color));
        return this;
    }

    public MessageBuilder color(Color color) {
        getLatestText().setFill(color);
        return this;
    }

    public MessageBuilder then(String text) {
        return append(text);
    }

    public MessageBuilder append(String text) {
        addText(text);
        latestMessageFontWeight = FontWeight.NORMAL;
        return this;
    }

    public String getRawText() {
        String raw = "";
        for (Text text : texts) {
            raw += text.getText();
        }

        return raw;
    }

    private void addText(String text) {
        texts.add(new Text(text));
    }

    public List<Text> getTexts() {
        return texts;
    }

    public Text getLatestText() {
        return texts.get(texts.size());
    }
}
