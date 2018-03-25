package me.goddragon.teaseai.api.chat;

/**
 * Created by GodDragon on 23.03.2018.
 */
public enum TypeSpeed {

    SLOW(500), MEDIUM(200), FAST(100), INSTANT(0);

    private final long millisPerLetter;

    TypeSpeed(int millisPerLetter) {
        this.millisPerLetter = millisPerLetter;
    }

    public long getTypeDuration(String message) {
        return message.trim().length()*millisPerLetter;
    }
}
