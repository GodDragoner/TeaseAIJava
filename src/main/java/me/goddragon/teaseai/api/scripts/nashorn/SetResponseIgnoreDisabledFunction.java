package me.goddragon.teaseai.api.scripts.nashorn;

import me.goddragon.teaseai.api.chat.response.ResponseHandler;
import me.goddragon.teaseai.utils.TeaseLogger;

import java.util.Arrays;
import java.util.logging.Level;

public class SetResponseIgnoreDisabledFunction extends CustomFunction {

    public SetResponseIgnoreDisabledFunction() {
        super("setResponseIgnoreDisabled");
    }

    @Override
    public boolean isFunction() {
        return true;
    }

    @Override
    public Object call(Object object, Object... args) {
        super.call(object, args);

        switch (args.length) {
            case 1:
                if (args[0] instanceof Boolean) {
                    ResponseHandler.getHandler().getCurrentLoadingResponse().setIgnoreDisabledResponses((Boolean) args[0]);
                    return null;
                }

                break;
            case 0:
                TeaseLogger.getLogger().log(Level.SEVERE, getFunctionName() + " called with no args");
                return null;
        }

        TeaseLogger.getLogger().log(Level.SEVERE, getFunctionName() + " called with invalid args:" + Arrays.asList(args).toString());
        return null;
    }
}
