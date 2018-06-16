package me.goddragon.teaseai.api.chat;

/**
 * Created by GodDragon on 23.03.2018.
 */
public enum TypeSpeed {

    SLOWEST(230), SLOWER(180), SLOW(140), MEDIUM(120), FAST(100), FASTER(80), FASTEST(50), INSTANT(0);

    private final long millisPerLetter;

    TypeSpeed(int millisPerLetter) {
        this.millisPerLetter = millisPerLetter;
    }

    public long getTypeDuration(String message) {
        return message.trim().length()*millisPerLetter;
    }
}
