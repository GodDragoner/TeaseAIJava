package me.goddragon.teaseai.utils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import javafx.scene.Node;

import me.goddragon.teaseai.utils.message.FormattedTextGenerator;
import me.goddragon.teaseai.utils.message.TextTagElement;
import me.goddragon.teaseai.utils.message.TextTagElementParser;

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
        if (name.length() > 1 && Character.isUpperCase(name.charAt(1))
                && Character.isUpperCase(name.charAt(0))) {
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

    public static List<Node> processString(String toProcess) {
        final List<TextTagElement> listOfTextElements = TextTagElementParser.split(toProcess);
        return FormattedTextGenerator.createListOfNodes(listOfTextElements);
    }

    public static String urlEncodeValue(String value) {
        try {
            return URLEncoder.encode(value, StandardCharsets.UTF_8.toString());
        } catch (UnsupportedEncodingException ex) {
            throw new RuntimeException(ex.getCause());
        }
    }
}
