package me.goddragon.teaseai.api.scripts.nashorn;

import javafx.scene.text.Text;
import me.goddragon.teaseai.api.chat.ChatHandler;
import me.goddragon.teaseai.utils.TeaseLogger;

import java.util.Arrays;
import java.util.logging.Level;

/**
 * Created by GodDragon on 10.04.2018.
 */
public class SendCustomMessageFunction extends CustomFunction {

    public SendCustomMessageFunction() {
        super("sendCustomMessage");
    }

    @Override
    public boolean isFunction() {
        return true;
    }

    @Override
    public Object call(Object object, Object... args) {
        super.call(object, args);

        Text[] texts = new Text[args.length];
        for(int x = 0; x < args.length; x++) {
            Object arg = args[x];
            if(arg instanceof Text) {
                texts[x] = (Text) arg;
            } else {
                TeaseLogger.getLogger().log(Level.SEVERE, getFunctionName() + " called with invalid args:" + Arrays.asList(args).toString());
                return null;
            }
        }

        switch(args.length) {
            case 0:
                TeaseLogger.getLogger().log(Level.SEVERE, "Called " + getFunctionName() + " method without parameters.");
                return null;
            default:
                ChatHandler.getHandler().addLine(texts);
                return null;
        }
    }
}
