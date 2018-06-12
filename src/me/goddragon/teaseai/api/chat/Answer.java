package me.goddragon.teaseai.api.chat;

import me.goddragon.teaseai.TeaseAI;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by GodDragon on 23.03.2018.
 */
public class Answer {

    private long millisTimeout = 0;
    private String answer;
    private long startedAt;
    private boolean timeout = false;

    public Answer() {}

    public Answer(long timeoutSeconds) {
        this.millisTimeout = timeoutSeconds*1000;
    }

    public void addOption(String optionMessage) {
        addOption(optionMessage, optionMessage);
    }

    public void addOption(String optionName, String optionMessage) {
        TeaseAI.application.runOnUIThread(new Runnable() {
            @Override
            public void run() {
                TeaseAI.application.getController().getLazySubController().addButton(TeaseAI.application.getController().getLazySubController().createSendMessageButton(optionName, optionMessage));
            }
        });
    }

    public void clearOptions() {
        TeaseAI.application.runOnUIThread(new Runnable() {
            @Override
            public void run() {
                TeaseAI.application.getController().getLazySubController().clear();
                TeaseAI.application.getController().getLazySubController().createDefaults();
            }
        });
    }

    public void loop() {
        this.answer = null;
        this.timeout = false;

        startedAt = System.currentTimeMillis();
        TeaseAI.application.waitPossibleScripThread(millisTimeout);
        checkTimeout();
    }

    public boolean matchesRegex(String regex) {
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(answer);
        return m.find();
    }

    public boolean matchesRegex(String... regexs) {
        for (String regex : regexs) {
            if (matchesRegex(regex)) {
                return true;
            }
        }

        return false;
    }

    public boolean matchesRegexLowerCase(String regex) {
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(answer.toLowerCase());
        return m.find();
    }

    public boolean matchesRegexLowerCase(String... regexs) {
        for (String regex : regexs) {
            if (matchesRegexLowerCase(regex)) {
                return true;
            }
        }

        return false;
    }


    public boolean contains(String string) {
        return answer != null && answer.contains(string);
    }

    public boolean contains(String... strings) {
        for (String string : strings) {
            if (contains(string)) {
                return true;
            }
        }

        return false;
    }

    public boolean isLike(String string) {
        return containsIgnoreCase(string);
    }


    public boolean isLike(String... strings) {
        return containsIgnoreCase(strings);
    }

    public boolean containsIgnoreCase(String string) {
        return answer != null && answer.toLowerCase().contains(string.toLowerCase());
    }

    public boolean containsIgnoreCase(String... strings) {
        for (String string : strings) {
            if (containsIgnoreCase(string)) {
                return true;
            }
        }

        return false;
    }

    public boolean isInteger() {
        try {
            Integer.parseInt(answer);
            return true;
        } catch (NumberFormatException ex) {}

        return false;
    }

    public boolean isInt() {
        try {
            Integer.parseInt(answer);
            return true;
        } catch (NumberFormatException ex) {}

        return false;
    }

    public boolean isDouble() {
        try {
            Double.parseDouble(answer);
            return true;
        } catch (NumberFormatException ex) {}

        return false;
    }

    public int getInteger() {
        return getInt();
    }

    public int getInt() {
        try {
            return Integer.parseInt(answer);
        } catch (NumberFormatException ex) {}

        return 0;
    }

    public double getDouble() {
        try {
            return Double.parseDouble(answer);
        } catch (NumberFormatException ex) {}

        return 0;
    }

    public long getMillisTimeout() {
        return millisTimeout;
    }

    public void setTimeoutSeconds(long timeoutSeconds) {
        this.millisTimeout = timeoutSeconds*1000;
    }

    public void setMillisTimeout(long millisTimeout) {
        this.millisTimeout = millisTimeout;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public void setStartedAt(long startedAt) {
        this.startedAt = startedAt;
    }

    public void checkTimeout() {
        if(System.currentTimeMillis() - startedAt >= millisTimeout - 100 && millisTimeout > 0) {
            timeout = true;
        }
    }

    public boolean isTimeout() {
        return timeout;
    }

    public void setTimeout(boolean timeout) {
        this.timeout = timeout;
    }
}
