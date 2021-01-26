package me.goddragon.teaseai.api.scripts.nashorn;

import java.io.File;

import me.goddragon.teaseai.api.chat.ChatHandler;
import me.goddragon.teaseai.api.media.MediaHandler;
import me.goddragon.teaseai.utils.FileUtils;

/**
 * Created by GodDragon on 25.03.2018.
 */
public class SendMessageFunction extends CustomFunctionExtended {
    public SendMessageFunction() {
        super("sendMessage", "sm", "SM");
    }

    @Override
    public boolean isFunction() {
        return true;
    }

    protected void onCall(String message) {
        ChatHandler.getHandler().getSelectedSender().customMessage(message, -1, true);
    }

    protected void onCall(String message, Double durationSeconds) {
        onCall(message, durationSeconds, true);
    }

    protected void onCall(String message, Integer durationSeconds) {
        onCall(message, durationSeconds, true);
    }

    protected void onCall(String message, String imagePath) {
        ChatHandler.getHandler().getSelectedSender().customMessage(message, 0, true);

        // TODO: Support for urls, video etc.
        final File file = FileUtils.getRandomMatchingFile(imagePath);
        if (file != null) {
            MediaHandler.getHandler().showPicture(
                    file, (int) (ChatHandler.getHandler().getMillisToPause(message) / 1000));
        }
    }

    protected void onCall(String message, Double durationSeconds, Boolean showTyping) {
        onCall(message, durationSeconds.intValue(), showTyping);
    }

    protected void onCall(String message, Integer durationSeconds, Boolean showTyping) {
        ChatHandler.getHandler().getSelectedSender().customMessage(
                message, durationSeconds * 1000L, showTyping);
    }
}
