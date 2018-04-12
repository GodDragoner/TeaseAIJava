package me.goddragon.teaseai.api.scripts.nashorn;

import me.goddragon.teaseai.api.chat.response.ResponseHandler;

/**
 * Created by GodDragon on 25.03.2018.
 */
public class AddResponseIndicatorFunction extends CustomFunction {

    public AddResponseIndicatorFunction() {
        super("addResponseIndicator");
    }

    @Override
    public boolean isFunction() {
        return true;
    }

    @Override
    public Object call(Object object, Object... args) {
        super.call(object, args);

        for(Object arg : args) {
            ResponseHandler.getHandler().getCurrentLoadingResponse().addIndicator(arg.toString());
        }

        return null;
    }
}
