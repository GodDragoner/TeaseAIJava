package me.goddragon.teaseai.api.scripts.nashorn;

import me.goddragon.teaseai.api.chat.vocabulary.VocabularyHandler;
import me.goddragon.teaseai.utils.TeaseLogger;

import java.util.Arrays;
import java.util.logging.Level;

/**
 * Created by GodDragon on 11.04.2018.
 */
public class ReplaceVocabulariesFunction extends CustomFunction {

    public ReplaceVocabulariesFunction() {
        super("replaceVocab", "replaceVocabs", "replaceVocabularies");
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
                return VocabularyHandler.getHandler().replaceAllVocabularies(args[0].toString());
            case 0:
                TeaseLogger.getLogger().log(Level.SEVERE, "Called " + getFunctionName() + " method without parameters.");
                return null;
        }

        TeaseLogger.getLogger().log(Level.SEVERE, getFunctionName() + " called with invalid args:" + Arrays.asList(args).toString());
        return null;
    }
}
