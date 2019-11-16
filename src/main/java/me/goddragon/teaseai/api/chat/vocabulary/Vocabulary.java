package me.goddragon.teaseai.api.chat.vocabulary;

import me.goddragon.teaseai.utils.RandomUtils;

import java.util.HashMap;

/**
 * Created by GodDragon on 23.03.2018.
 */
public class Vocabulary {

    private HashMap<Object, Double> synonyms = new HashMap<>();

    public Vocabulary(Object... synonyms) {
        for (Object object : synonyms) {
            this.synonyms.put(object, 1D);
        }
    }

    public Vocabulary(String vocabName) {
        this.synonyms.put("!" + vocabName + "!", 1D);
    }


    public Vocabulary(HashMap<Object, Double> synonyms) {
        this.synonyms = synonyms;
    }

    public HashMap<Object, Double> getSynonyms() {
        return synonyms;
    }

    public String toString() {
        return RandomUtils.getWinner(synonyms).toString();
    }
}
