package me.goddragon.teaseai.utils.message;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

import me.goddragon.teaseai.utils.FileUtils;
import me.goddragon.teaseai.utils.TeaseLogger;
import me.goddragon.teaseai.utils.media.AnimatedGif;

public class FormattedTextGenerator {
    private static final Pattern TAG_PATTERN =
            Pattern.compile("(?<tagClose>/)?(?<tagName>\\w+)(?:(?:[=:])(?<tagArgument>.+))?");

    /**
     * Create JavaFX Nodes for each corresponding TextTagElement.
     *
     * For example:
     *   createListOfNodes( [
     *     {"Mary "}, {"b"}, {"loves"}, {"/b"}, {" her little lamb "}, {"img=Heart.gif"} ] )
     * where {"b"}, {"/b"}, and {"img=Heart.gif"} are tags
     * produces the following list: [
     *     Text("Mary "),              // normal formatting
     *     Text("loves"),              // bold formatting
     *     Text(" her little lamb "),  // normal formatting
     *     ImageView()                 // containing image of Heart.gif
     *   ]
     */
    public static List<Node> createListOfNodes(List<TextTagElement> listOfElements) {
        return new FormattedTextGenerator(listOfElements).listOfNodes;
    }

    private FormattedTextGenerator(List<TextTagElement> listOfElements) {
        final List<TagHandler> listOfTagHandlers = new ArrayList<>();
        listOfTagHandlers.add(createItalicsTagHandler());
        listOfTagHandlers.add(createBoldTagHandler());
        listOfTagHandlers.add(createUnderlineTagHandler());
        listOfTagHandlers.add(createStrikethroughTagHandler());
        listOfTagHandlers.add(createColorTagHandler());
        listOfTagHandlers.add(createWeightTagHandler());
        listOfTagHandlers.add(createFontSizeTagHandler());
        listOfTagHandlers.add(createFontTagHandler());
        listOfTagHandlers.add(createImageTagHandler());

        for (TextTagElement element : listOfElements) {
            if (element.isTag()) {
                processTag(element.getContent(), listOfTagHandlers);
            } else {
                final Text text = textAttributeStack.createFormattedText(element.getContent());
                listOfNodes.add(text);
            }
        }
    }

    private TagHandler createItalicsTagHandler() {
        return new TagHandler(ExpectingArgument.NO, "i", "italics") {
            @Override
            protected void onOpenTag(String tagArgument) {
                textAttributeStack.pushPosture(FontPosture.ITALIC);
            }

            @Override
            protected void onCloseTag() {
                textAttributeStack.dropPosture();
            }
        };
    }

    private TagHandler createBoldTagHandler() {
        return new TagHandler(ExpectingArgument.NO, "b", "bold") {
            @Override
            protected void onOpenTag(String tagArgument) {
                textAttributeStack.pushWeight(FontWeight.BOLD);
            }

            @Override
            protected void onCloseTag() {
                textAttributeStack.dropWeight();
            }
        };
    }

    private TagHandler createUnderlineTagHandler() {
        return new TagHandler(ExpectingArgument.NO, "u", "under", "underline") {
            @Override
            protected void onOpenTag(String tagArgument) {
                textAttributeStack.pushUnderline();
            }

            @Override
            protected void onCloseTag() {
                textAttributeStack.dropUnderline();
            }
        };
    }

    private TagHandler createStrikethroughTagHandler() {
        return new TagHandler(ExpectingArgument.NO, "s", "strike", "strikethrough") {
            @Override
            protected void onOpenTag(String tagArgument) {
                textAttributeStack.pushStrikethrough();
            }

            @Override
            protected void onCloseTag() {
                textAttributeStack.dropStrikethrough();
            }
        };
    }

    private TagHandler createColorTagHandler() {
        return new TagHandler(ExpectingArgument.YES, "c", "col", "color") {
            @Override
            protected void onOpenTag(String tagArgument) {
                try {
                    textAttributeStack.pushColor(Color.valueOf(tagArgument.toUpperCase()));
                } catch (IllegalArgumentException ex) {
                    logError("Unrecognized color '%s'."
                                    + "Please use a color from this list or use an rgb or hex color as shown here: "
                                    + "https://docs.oracle.com/javase/8/javafx/api/javafx/scene/paint/Color.html",
                            tagArgument);
                }
            }

            @Override
            protected void onCloseTag() {
                textAttributeStack.dropColor();
            }
        };
    }

    private TagHandler createWeightTagHandler() {
        return new TagHandler(ExpectingArgument.YES, "w", "weight", "fontweight") {
            @Override
            protected void onOpenTag(String tagArgument) {
                try {
                    textAttributeStack.pushWeight(FontWeight.valueOf(tagArgument.toUpperCase()));
                } catch (IllegalArgumentException ex) {
                    logError("Unrecognized font weight '%s'", tagArgument);
                }
            }

            @Override
            protected void onCloseTag() {
                textAttributeStack.dropWeight();
            }
        };
    }

    private TagHandler createFontSizeTagHandler() {
        return new TagHandler(ExpectingArgument.YES, "fs", "fontsize") {
            @Override
            protected void onOpenTag(String tagArgument) {
                try {
                    textAttributeStack.pushFontSize(Double.parseDouble(tagArgument));
                } catch (NumberFormatException ex) {
                    logError("Unrecognized font size '%s'", tagArgument);
                }
            }

            @Override
            protected void onCloseTag() {
                textAttributeStack.dropFontSize();
            }
        };
    }

    private TagHandler createFontTagHandler() {
        return new TagHandler(ExpectingArgument.YES, "f", "font") {
            @Override
            protected void onOpenTag(String tagArgument) {
                textAttributeStack.pushFont(tagArgument);
            }

            @Override
            protected void onCloseTag() {
                textAttributeStack.dropFont();
            }
        };
    }

    private TagHandler createImageTagHandler() {
        return new TagHandler(ExpectingArgument.YES, "img", "image", "picture", "gif") {
            @Override
            protected void onOpenTag(String tagArgument) {
                File file = FileUtils.getRandomMatchingFile(tagArgument);
                if (file != null) {
                    if (file.getName().endsWith(".gif")) {
                        AnimatedGif currentAnimation = new AnimatedGif(file.toURI().toString());
                        currentAnimation.setCycleCount(Integer.MAX_VALUE);
                        final ImageView imageView = new ImageView();
                        currentAnimation.play(imageView);
                        listOfNodes.add(imageView);
                    } else {
                        try (InputStream inputStream = new FileInputStream(file)) {
                            final ImageView imageView = new ImageView(new Image(inputStream));
                            listOfNodes.add(imageView);
                        } catch (IOException ex) {
                            logError("Unable to load image '%s' referenced from <img> tag",
                                    file.getPath());
                        }
                    }
                }
            }

            @Override
            protected void onCloseTag() {
                // nothing required for closing tag
            }
        };
    }

    private void processTag(String tag, List<TagHandler> listOfTagHandlers) {
        final Matcher matcher = TAG_PATTERN.matcher(tag);
        if (matcher.matches()) {
            final TagType tagType =
                    (matcher.group("tagClose") != null) ? TagType.CLOSING : TagType.OPENING;
            final String tagName = matcher.group("tagName");
            final String tagArgument = matcher.group("tagArgument");

            boolean didHandle = false;

            for (TagHandler tagHandler : listOfTagHandlers) {
                didHandle = tagHandler.tryHandle(tagType, tagName, tagArgument);
                if (didHandle) {
                    break;
                }
            }

            if (!didHandle) {
                logError("Unrecognised tag <%s> was discarded", tag);
            }
        } else {
            logError("Tag <%s> cannot be matched as a valid tag", tag);
        }
    }

    private enum TagType { OPENING, CLOSING }
    private enum ExpectingArgument { YES, NO }

    private abstract class TagHandler {
        public TagHandler(ExpectingArgument expectingArgument, String... listOfTagNames) {
            this.expectingArgument = expectingArgument;
            this.listOfTagNames = listOfTagNames;
        }

        public boolean tryHandle(TagType tagType, String tagName, String tagArgument) {
            if (canHandle(tagName)) {
                if (tagType == TagType.OPENING) {
                    callOnOpenTag(tagName, tagArgument);
                } else {
                    callOnCloseTag(tagName, tagArgument);
                }
                return true;
            }
            return false;
        }

        private boolean canHandle(String tagName) {
            for (String knownTagName : listOfTagNames) {
                if (tagName.compareToIgnoreCase(knownTagName) == 0) {
                    return true;
                }
            }
            return false;
        }

        private void callOnOpenTag(String tagName, String tagArgument) {
            if ((expectingArgument == ExpectingArgument.YES) && (tagArgument == null)) {
                logError("Tag <%s> was specified without a mandatory argument", tagName);
            } else {
                if ((expectingArgument == ExpectingArgument.NO) && (tagArgument != null)) {
                    logWarning("Tag <%s> was specified with argument '%s' which has been ignored",
                            tagName, tagArgument);
                }
                onOpenTag(tagArgument);
            }
        }

        private void callOnCloseTag(String tagName, String tagArgument) {
            if (tagArgument != null) {
                logWarning("Tag </%s> was specified with argument '%s' which has been ignored",
                        tagName, tagArgument);
            }
            onCloseTag();
        }

        protected abstract void onOpenTag(String tagArgument);
        protected abstract void onCloseTag();

        private final ExpectingArgument expectingArgument;
        private final String[] listOfTagNames;
    }

    private static void logWarning(String format, Object... args) {
        TeaseLogger.getLogger().log(Level.WARNING, String.format(format, args));
    }

    private static void logError(String format, Object... args) {
        TeaseLogger.getLogger().log(Level.SEVERE, String.format(format, args));
    }

    private final List<Node> listOfNodes = new ArrayList<>();
    private final TextAttributeStack textAttributeStack = new TextAttributeStack();
}
