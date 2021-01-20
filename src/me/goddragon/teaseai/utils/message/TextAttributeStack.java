package me.goddragon.teaseai.utils.message;

import java.util.ArrayDeque;
import java.util.Deque;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

import me.goddragon.teaseai.TeaseAI;
import me.goddragon.teaseai.api.chat.ChatHandler;

/**
 * Maintain a stack of text attributes for creating JavaFX Text Nodes.
 * 
 * This utility is useful when processing tags within text where an opening tag can push a text
 * attribute and a closing tag can drop a text attribute, thereby supporting the stacking of
 * attributes. At any point, createFormattedText() can be used to create a JavaFX Text Node based
 * on the currently active attributes.
 */
class TextAttributeStack {
    private static final FontPosture DEFAULT_FONT_POSTURE = FontPosture.REGULAR;
    private static final FontWeight DEFAULT_FONT_WEIGHT = FontWeight.NORMAL;

    public void pushPosture(FontPosture posture) {
        stackOfPostures.addLast(posture);
    }

    public void dropPosture() {
        if (!stackOfPostures.isEmpty()) {
            stackOfPostures.removeLast();
        }
    }

    public void pushWeight(FontWeight weight) {
        stackOfWeights.addLast(weight);
    }

    public void dropWeight() {
        if (!stackOfWeights.isEmpty()) {
            stackOfWeights.removeLast();
        }
    }

    public void pushUnderline() {
        ++countOfUnderline;
    }

    public void dropUnderline() {
        if (countOfUnderline > 0) {
            --countOfUnderline;
        }
    }

    public void pushStrikethrough() {
        ++countOfStrikethrough;
    }

    public void dropStrikethrough() {
        if (countOfStrikethrough > 0) {
            --countOfStrikethrough;
        }
    }

    public void pushColor(Color color) {
        stackOfColors.addLast(color);
    }

    public void dropColor() {
        if (!stackOfColors.isEmpty()) {
            stackOfColors.removeLast();
        }
    }

    public void pushFontSize(double fontSize) {
        stackOfSizes.addLast(fontSize);
    }

    public void dropFontSize() {
        if (!stackOfSizes.isEmpty()) {
            stackOfSizes.removeLast();
        }
    }

    public void pushFont(String familyName) {
        stackOfFontFamiles.addLast(familyName);
    }

    public void dropFont() {
        if (!stackOfFontFamiles.isEmpty()) {
            stackOfFontFamiles.removeLast();
        }
    }

    public Text createFormattedText(String content) {
        final Text text = new Text(content);
        text.setFont(Font.font(getFontFamily(text), getWeight(), getPosture(), getPointSize()));
        text.setFill(getColor());
        text.setStrikethrough(isStrikethrough());
        text.setUnderline(isUnderline());
        return text;
    }

    private FontWeight getWeight() {
        if (stackOfWeights.isEmpty()) {
            return DEFAULT_FONT_WEIGHT;
        } else {
            return stackOfWeights.getLast();
        }
    }

    private FontPosture getPosture() {
        if (stackOfPostures.isEmpty()) {
            return DEFAULT_FONT_POSTURE;
        } else {
            return stackOfPostures.getLast();
        }
    }

    private double getPointSize() {
        if (stackOfSizes.isEmpty()) {
            return TeaseAI.application.CHAT_TEXT_SIZE.getDouble();
        } else {
            return stackOfSizes.getLast();
        }
    }

    private String getFontFamily(Text text) {
        if (stackOfFontFamiles.isEmpty()) {
            return text.getFont().getFamily();
        } else {
            return stackOfFontFamiles.getLast();
        }
    }

    private Color getColor() {
        if (stackOfColors.isEmpty()) {
            return ChatHandler.getHandler().getDefaultChatColor();
        } else {
            return stackOfColors.getLast();
        }
    }

    private boolean isUnderline() {
        return countOfUnderline > 0;
    }

    private boolean isStrikethrough() {
        return countOfStrikethrough > 0;
    }

    private final Deque<FontPosture> stackOfPostures = new ArrayDeque<>();
    private final Deque<FontWeight> stackOfWeights = new ArrayDeque<>();
    private int countOfUnderline = 0;
    private int countOfStrikethrough = 0;
    private final Deque<Double> stackOfSizes = new ArrayDeque<>();
    private final Deque<String> stackOfFontFamiles = new ArrayDeque<>();
    private final Deque<Color> stackOfColors = new ArrayDeque<>();
}
