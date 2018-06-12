package me.goddragon.teaseai.api.chat.response;

import java.util.Collection;
import java.util.HashSet;
import java.util.regex.Pattern;

/**
 * Created by GodDragon on 24.03.2018.
 */
public abstract class Response {

    private final Collection<String> indicators = new HashSet<>();
    private final Collection<String> lowerCaseIndicators = new HashSet<>();

    private final Collection<Pattern> regexPatterns = new HashSet<>();

    private String message;
    private boolean disabled = false;

    public Response() {
    }

    public Response(String... indicators) {
        for(String indicator : indicators) {
            addIndicator(indicator);
        }
    }

    public Response(Pattern... patterns) {
        for(Pattern pattern : patterns) {
            this.regexPatterns.add(pattern);
        }
    }

    public abstract boolean trigger();

    public boolean checkPattern(Pattern pattern, String message) {
        return pattern.matcher(message).find();
    }

    public boolean checkPatterns(String message) {
        for(Pattern pattern : regexPatterns) {
            if(checkPattern(pattern, message)) {
                return true;
            }
        }

        return false;
    }

    public boolean containsLike(String message) {
        message = message.toLowerCase();
        for(String indicator : lowerCaseIndicators) {
            if(message.contains(indicator)) {
                return true;
            }
        }

        return checkPatterns(message);
    }

    public boolean containsEqual(String message) {
        for(String indicator : indicators) {
            if(message.contains(indicator)) {
                return true;
            }
        }

        return checkPatterns(message);
    }

    public void addIndicator(String indicator) {
        indicator = indicator.trim();
        indicators.add(indicator);
        lowerCaseIndicators.add(indicator.toLowerCase());
    }

    public void addIndicators(String... indicators) {
        for(String indicator : indicators) {
            addIndicator(indicator);
        }
    }

    public void addRegexPatterns(String... patterns) {
        for(String pattern : patterns) {
            addRegexPatterns(Pattern.compile(pattern));
        }
    }

    public void addRegexPattern(Pattern pattern) {
        regexPatterns.add(pattern);
    }

    public void addRegexPatterns(Pattern... patterns) {
        for(Pattern pattern : patterns) {
            addRegexPattern(pattern);
        }
    }

    public Collection<String> getIndicators() {
        return indicators;
    }

    public Collection<Pattern> getRegexPatterns() {
        return regexPatterns;
    }

    public boolean isDisabled() {
        return disabled;
    }

    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
