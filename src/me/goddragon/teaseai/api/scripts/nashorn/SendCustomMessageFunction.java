package me.goddragon.teaseai.api.scripts.nashorn;

import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import me.goddragon.teaseai.api.chat.ChatHandler;
import me.goddragon.teaseai.utils.TeaseLogger;

import java.util.Arrays;
import java.util.logging.Level;

/**
 * Created by GodDragon on 10.04.2018.
 */
public class SendSystemMessageFunction extends CustomFunction {

    public SendSystemMessageFunction() {
        super("sendSystemMessage");
    }

    @Override
    public boolean isFunction() {
        return true;
    }

    @Override
    public Object call(Object object, Object... args) {
        switch(args.length) {
            case 1:
                Text messageText = new Text(args[0].toString());
                messageText.setFill(Color.BLUE);
                ChatHandler.getHandler().addLine(messageText);
                return null;
            case 0:
                TeaseLogger.getLogger().log(Level.SEVERE, "Called sendMessage method without parameters.");
                return null;
        }

        TeaseLogger.getLogger().log(Level.SEVERE, getFunctionName() + " called with invalid args:" + Arrays.asList(args).toString());
        return null;
    }
}
