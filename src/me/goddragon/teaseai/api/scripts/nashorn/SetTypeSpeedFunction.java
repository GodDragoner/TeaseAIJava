package me.goddragon.teaseai.api.scripts.nashorn;

import me.goddragon.teaseai.api.chat.ChatHandler;
import me.goddragon.teaseai.api.chat.TypeSpeed;
import me.goddragon.teaseai.utils.TeaseLogger;

import java.util.Arrays;
import java.util.logging.Level;

/**
 * Created by GodDragon on 18.05.2018.
 */
public class SetTypeSpeedFunction extends CustomFunction {

    public SetTypeSpeedFunction() {
        super("setTypeSpeed");
    }

    @Override
    public boolean isFunction() {
        return true;
    }

    @Override
    public Object call(Object object, Object... args) {
        super.call(object, args);

        switch(args.length) {
            case 1:
                String typeSpeedString = args[0].toString();
                try {
                    TypeSpeed typeSpeed = TypeSpeed.valueOf(typeSpeedString.toUpperCase());
                    ChatHandler.getHandler().getSelectedSender().setTypeSpeed(typeSpeed);
                } catch(IllegalArgumentException ex) {
                    TeaseLogger.getLogger().log(Level.SEVERE, "Invalid type speed '" + typeSpeedString + "'.");
                }

                break;
            case 0:
                TeaseLogger.getLogger().log(Level.SEVERE, "Called " + getFunctionName() + " method without parameters.");
                return null;
        }

        TeaseLogger.getLogger().log(Level.SEVERE, getFunctionName() + " called with invalid args:" + Arrays.asList(args).toString());
        return null;
    }
}
