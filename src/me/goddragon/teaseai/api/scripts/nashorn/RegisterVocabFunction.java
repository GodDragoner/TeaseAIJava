package me.goddragon.teaseai.api.scripts.nashorn;

import me.goddragon.teaseai.api.chat.vocabulary.Vocabulary;
import me.goddragon.teaseai.utils.TeaseLogger;

import java.util.Arrays;
import java.util.logging.Level;

/**
 * Created by GodDragon on 25.03.2018.
 */
public class RegisterVocabFunction extends CustomFunction {

    public RegisterVocabFunction() {
        super("registerVocab");
    }

    @Override
    public boolean isFunction() {
        return true;
    }

    @Override
    public Object call(Object object, Object... args) {
        if(args.length > 1) {
            Vocabulary vocabulary = new Vocabulary((String) args[0]);

            //Add all vocabularies
            for(int x = 1; x < args.length; x++) {
                vocabulary.getSynonyms().put(args[1], 1D);
            }
        }

        TeaseLogger.getLogger().log(Level.SEVERE, getFunctionName() + " called with invalid args:" + Arrays.asList(args).toString());
        return null;
    }
}
