package me.goddragon.teaseai.api.scripts.nashorn;

import me.goddragon.teaseai.api.chat.response.ResponseHandler;

/**
 * Created by GodDragon on 25.03.2018.
 */
public class AddResponseRegexFunction extends CustomFunction {

    public AddResponseRegexFunction() {
        super("addResponseRegex");
    }

    @Override
    public boolean isFunction() {
        return true;
    }

    @Override
    public Object call(Object object, Object... args) {
        for (Object arg : args) {
            ResponseHandler.getHandler().getCurrentLoadingResponse().addRegexPatterns(arg.toString());
        }

        return null;
    }
}

