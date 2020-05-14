package me.goddragon.teaseai.utils;

import javafx.scene.Node;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import me.goddragon.teaseai.TeaseAI;
import me.goddragon.teaseai.api.chat.ChatHandler;
import me.goddragon.teaseai.utils.media.AnimatedGif;
import me.goddragon.teaseai.utils.media.ImageUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by GodDragon on 11.04.2018.
 */
public class StringUtils {

    /**
     * Utility method to take a string and convert it to normal Java variable
     * name capitalization.  This normally means converting the first
     * character from upper case to lower case, but in the (unusual) special
     * case when there is more than one character and both the first and
     * second characters are upper case, we leave it alone.
     * <p>
     * Thus "FooBah" becomes "fooBah" and "X" becomes "x", but "URL" stays
     * as "URL".
     *
     * @param name The string to be decapitalized.
     * @return The decapitalized version of the string.
     */
    public static String decapitalize(String name) {
        if (name == null || name.length() == 0) {
            return name;
        }
        if (name.length() > 1 &&
                Character.isUpperCase(name.charAt(1)) &&
                Character.isUpperCase(name.charAt(0))) {

            return name;
        }
        char chars[] = name.toCharArray();
        chars[0] = Character.toLowerCase(chars[0]);
        return new String(chars);
    }

    /**
     * Utility method to take a string and convert it to normal a string
     * with the first character in upper case.
     * <p>
     * Thus "fooBah" becomes "FooBah" and "x" becomes "X".\
     *
     * @param name The string to be capitalized.
     * @return The capitalized version of the string.
     */
    public static String capitalize(String name) {
        if (name == null || name.length() == 0) {
            return name;
        }
        char chars[] = name.toCharArray();
        chars[0] = Character.toUpperCase(chars[0]);
        return new String(chars);
    }


    public static List<Node> processString2(String toProcess) {
        ArrayList<Node> toReturn = new ArrayList<>();
        Pattern formatter = Pattern.compile("([^<]*)(<([\\w =,.\\/]*)>([^<]*)(<>|<\\/>))([^<]*)");
        Matcher matcher = formatter.matcher(toProcess);

        while (matcher.find()) {
            String fullMatch = matcher.group(0);
            String unformattedPrevious = matcher.group(1);

            if (unformattedPrevious.length() > 0) {
                toReturn.add(ChatHandler.getHandler().getDefaultFormatText(unformattedPrevious));
            }

            String tags = matcher.group(3);
            String content = matcher.group(4);
            String close = matcher.group(5);

            String unformattedAfter = matcher.group(6);

            if (content.length() > 0) {
                Text thisText = new Text(content);

                boolean toItalicize = false;
                String colorToSet = null;
                FontWeight fontWeight = null;
                double fontSize = TeaseAI.application.CHAT_TEXT_SIZE.getDouble();
                String font = null;

                if (!tags.equals("")) {
                    String[] subFormatters = tags.split("[ ,]");
                    for (String subFormatter : subFormatters) {
                        if(subFormatter.length() == 0) {
                            continue;
                        }

                        if (subFormatter.equalsIgnoreCase("i") || subFormatter.equalsIgnoreCase("italics")) {
                            toItalicize = true;
                        } else if (subFormatter.equalsIgnoreCase("b") || subFormatter.equalsIgnoreCase("bold")) {
                            fontWeight = FontWeight.BOLD;
                        } else if (subFormatter.equalsIgnoreCase("s") || subFormatter.equalsIgnoreCase("strike") || subFormatter.equalsIgnoreCase("strikethrough")) {
                            thisText.setStrikethrough(true);
                        } else if (subFormatter.equalsIgnoreCase("u") || subFormatter.equalsIgnoreCase("under") || subFormatter.equalsIgnoreCase("underline")) {
                            thisText.setUnderline(true);
                        } else {
                            Matcher thisMatcher = Pattern.compile("(\\w+)(=|:)(\\w+)").matcher(subFormatter);
                            if (thisMatcher.matches()) {
                                switch (thisMatcher.group(1).toLowerCase()) {
                                    case "c":
                                    case "color":
                                        colorToSet = thisMatcher.group(3);
                                        break;
                                    case "w":
                                    case "weight":
                                    case "fontweight":
                                        try {
                                            fontWeight = FontWeight.valueOf(thisMatcher.group(3).toUpperCase());
                                        } catch (IllegalArgumentException e) {
                                            TeaseLogger.getLogger().log(Level.SEVERE, "Invalid font-weight:" + e.getMessage());
                                        }

                                        break;
                                    case "fs":
                                    case "fontsize":
                                        try {
                                            fontSize = Double.parseDouble(thisMatcher.group(3));
                                        } catch (NumberFormatException e) {
                                            TeaseLogger.getLogger().log(Level.SEVERE, "Invalid font-size! Must be a double:" + e.getMessage());
                                        }
                                        break;
                                    case "f":
                                    case "font":
                                        font = thisMatcher.group(3);
                                        break;
                                }
                            } else {
                                TeaseLogger.getLogger().log(Level.SEVERE, "Unrecognized formatter format:" + subFormatter);
                            }
                        }
                    }

                    if (font != null || fontSize != -1 || fontWeight != null || toItalicize != false) {
                        String fontToSet = font;
                        double fontSizeToSet = fontSize;

                        FontPosture fontPosture = FontPosture.REGULAR;
                        FontWeight fontWeightToSet = fontWeight;

                        if (toItalicize) {
                            fontPosture = FontPosture.ITALIC;
                        }

                        if (fontToSet == null) {
                            fontToSet = thisText.getFont().getFamily();
                        }

                        if (fontSizeToSet == -1) {
                            fontSizeToSet = thisText.getFont().getSize();
                        }

                        if (fontWeightToSet == null) {
                            fontWeightToSet = FontWeight.NORMAL;
                        }

                        thisText.setFont(Font.font(fontToSet, fontWeightToSet, fontPosture, fontSizeToSet));
                    }

                    if (colorToSet != null) {
                        try {
                            Color toFill = Color.valueOf(colorToSet);
                            thisText.setFill(toFill);
                        } catch (IllegalArgumentException e) {
                            TeaseLogger.getLogger().log(Level.SEVERE, "Unrecognized color! Please use a color from this list or use an rgb or hex color as shown here: https://docs.oracle.com/javase/8/javafx/api/javafx/scene/paint/Color.html :" + colorToSet);
                        }
                    } else {
                        thisText.setFill(ChatHandler.getHandler().getDefaultChatColor());
                    }

                    toReturn.add(thisText);
                }
            } else {
                Node toAdd = null;

                Matcher thisMatcher = Pattern.compile("(\\w+)(=|:)([\\w =,.\\/]*)").matcher(tags);
                if (thisMatcher.matches()) {
                    String tag = thisMatcher.group(1);
                    String tagValue = thisMatcher.group(3);

                    switch (tag) {
                        case "img":
                        case "image":
                        case "picture":
                        case "gif":
                            String path = tagValue;
                            toAdd = new ImageView();

                            File file = FileUtils.getRandomMatchingFile(path);

                            if (file.getName().endsWith(".gif")) {
                                AnimatedGif currentAnimation = new AnimatedGif(file.toURI().toString());
                                currentAnimation.setCycleCount(Integer.MAX_VALUE);
                                currentAnimation.play((ImageView) toAdd);
                            } else {
                                ImageUtils.setImageInView(file, (ImageView) toAdd);
                            }

                            break;
                    }
                }

                if (toAdd != null) {
                    toReturn.add(toAdd);
                }
            }

            if (unformattedAfter.length() > 0) {
                toReturn.add(ChatHandler.getHandler().getDefaultFormatText(unformattedAfter));
            }
        }

        return toReturn;
    }

    public static List<Node> processString(String toProcess) {
        //return processString2(toProcess);
        ArrayList<Node> toReturn = new ArrayList<>();

        Pattern formatter = Pattern.compile("<[\\w =,.]*>");
        Matcher matcher = formatter.matcher(toProcess);

        ArrayList<String> formatters = new ArrayList<>();

        while (matcher.find()) {
            formatters.add(matcher.group());
        }

        //Find stuff in the form of <?>, <?=...>, <?:...> etc. and also </> for closing
        String[] messageFragments = toProcess.split("<[\\w =,.]*>");

        for (int i = 0; i < messageFragments.length; i++) {
            
            if (!messageFragments[i].equals("")) {
                Text thisText = new Text(messageFragments[i]);
                if (i != 0) {
                    //if i is greater than 1 here, then there must be always be at least i-1 elements in the formatters list so we don't need to check its size
                    String thisFormatter = formatters.get(i - 1);
                    thisFormatter = thisFormatter.replaceAll("[<>]", "");
                    boolean toItalicize = false;
                    String colorToSet = null;
                    FontWeight fontWeight = null;
                    double fontSize = TeaseAI.application.CHAT_TEXT_SIZE.getDouble();
                    String font = null;
                    if (!thisFormatter.equals("")) {
                        String[] subFormatters = thisFormatter.split("[ ,]");
                        for (String subFormatter : subFormatters) {
                            if (subFormatter.equalsIgnoreCase("i") || subFormatter.equalsIgnoreCase("italics")) {
                                toItalicize = true;
                            } else if (subFormatter.equalsIgnoreCase("b") || subFormatter.equalsIgnoreCase("bold")) {
                                fontWeight = FontWeight.BOLD;
                            } else if (subFormatter.equalsIgnoreCase("s") || subFormatter.equalsIgnoreCase("strike") || subFormatter.equalsIgnoreCase("strikethrough")) {
                                thisText.setStrikethrough(true);
                            } else if (subFormatter.equalsIgnoreCase("u") || subFormatter.equalsIgnoreCase("under") || subFormatter.equalsIgnoreCase("underline")) {
                                thisText.setUnderline(true);
                            } else {
                                Matcher thisMatcher = Pattern.compile("(\\w+)(=|:)(\\w+)").matcher(subFormatter);
                                if (thisMatcher.matches()) {
                                    switch (thisMatcher.group(1).toLowerCase()) {
                                        case "c":
                                        case "color":
                                            colorToSet = thisMatcher.group(3);
                                            break;
                                        case "w":
                                        case "weight":
                                        case "fontweight":
                                            try {
                                                fontWeight = FontWeight.valueOf(thisMatcher.group(3).toUpperCase());
                                            } catch (IllegalArgumentException e) {
                                                TeaseLogger.getLogger().log(Level.SEVERE, "Invalid font-weight:" + e.getMessage());
                                            }

                                            break;
                                        case "fs":
                                        case "fontsize":
                                            try {
                                                fontSize = Double.parseDouble(thisMatcher.group(3));
                                            } catch (NumberFormatException e) {
                                                TeaseLogger.getLogger().log(Level.SEVERE, "Invalid font-size! Must be a double:" + e.getMessage());
                                            }
                                            break;
                                        case "f":
                                        case "font":
                                            font = thisMatcher.group(3);
                                            break;
                                    }
                                } else {
                                    TeaseLogger.getLogger().log(Level.SEVERE, "Unrecognized formatter format:" + subFormatter);
                                }
                            }
                        }

                        if (font != null || fontSize != -1 || fontWeight != null || toItalicize != false) {
                            String fontToSet = font;
                            double fontSizeToSet = fontSize;

                            FontPosture fontPosture = FontPosture.REGULAR;
                            FontWeight fontWeightToSet = fontWeight;

                            if (toItalicize) {
                                fontPosture = FontPosture.ITALIC;
                            }

                            if (fontToSet == null) {
                                fontToSet = thisText.getFont().getFamily();
                            }

                            if (fontSizeToSet == -1) {
                                fontSizeToSet = thisText.getFont().getSize();
                            }

                            if (fontWeightToSet == null) {
                                fontWeightToSet = FontWeight.NORMAL;
                            }

                            thisText.setFont(Font.font(fontToSet, fontWeightToSet, fontPosture, fontSizeToSet));
                        }

                        if (colorToSet != null) {
                            try {
                                Color toFill = Color.valueOf(colorToSet);
                                thisText.setFill(toFill);
                            } catch (IllegalArgumentException e) {
                                TeaseLogger.getLogger().log(Level.SEVERE, "Unrecognized color! Please use a color from this list or use an rgb or hex color as shown here: https://docs.oracle.com/javase/8/javafx/api/javafx/scene/paint/Color.html :" + colorToSet);
                            }
                        } else {
                            thisText.setFill(ChatHandler.getHandler().getDefaultChatColor());
                        }
                    }
                } else {
                    thisText.setFont(Font.font(null, FontWeight.MEDIUM, TeaseAI.application.CHAT_TEXT_SIZE.getDouble()));
                    thisText.setFill(ChatHandler.getHandler().getDefaultChatColor());
                }
            } else {
                /*Node toAdd = null;

                Matcher thisMatcher = Pattern.compile("(\\w+)(=|:)(\\w+)").matcher(thisFormatter);

                if (thisMatcher.matches()) {
                    switch (thisMatcher.group(1).toLowerCase()) {
                        case "img":
                        case "image":
                        case "picture":
                        case "gif":
                            String path = thisMatcher.group(3);
                            toAdd = new ImageView();

                            File file = FileUtils.getRandomMatchingFile(path);

                            if (file.getName().endsWith(".gif")) {
                                AnimatedGif currentAnimation = new AnimatedGif(file.toURI().toString());
                                currentAnimation.setCycleCount(Integer.MAX_VALUE);
                                currentAnimation.play((ImageView) toAdd);
                            } else {
                                ImageUtils.setImageInView(file, (ImageView) toAdd);
                            }

                            break;
                    }
                }

                if (toAdd != null) {
                    toReturn.add(toAdd);
                }*/
            }
        }

        return toReturn;
    }
}
