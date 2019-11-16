package me.goddragon.teaseai.api.scripts.nashorn;

import me.goddragon.teaseai.api.media.MediaHandler;

/**
 * Created by GodDragon on 21.05.2018.
 */
public class GetCurrentImageURLFunction extends CustomFunction {

    public GetCurrentImageURLFunction() {
        super("getCurrentImageURL");
    }

    @Override
    public boolean isFunction() {
        return true;
    }

    @Override
    public Object call(Object object, Object... args) {
        super.call(object, args);

        return MediaHandler.getHandler().getCurrentImageURL();
    }
}
