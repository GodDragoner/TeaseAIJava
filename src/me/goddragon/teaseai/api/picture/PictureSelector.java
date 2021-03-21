package me.goddragon.teaseai.api.picture;

import me.goddragon.teaseai.TeaseAI;
import me.goddragon.teaseai.api.chat.ChatParticipant;
import me.goddragon.teaseai.api.session.Session;
import me.goddragon.teaseai.utils.MathUtils;
import me.goddragon.teaseai.utils.TeaseLogger;

import java.io.File;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

/**
 * Created by GodDragon on 26.03.2018.
 */
public class PictureSelector {

    private final Random random = new Random();

    public TaggedPicture getPicture(Session session, ChatParticipant participant) {
        if (participant.getPictureSet().getTaggedPictures().isEmpty() && participant.getPictureSet().getPicturesInFolder().length == 0) {
            return null;
        }

        long minutesPassed = TimeUnit.MILLISECONDS.toMinutes(session.getRuntime());
        int preferredSessionDuration = TeaseAI.application.PREFERRED_SESSION_DURATION.getInt();
        double percentage = 100.0 * minutesPassed / preferredSessionDuration;
        if (percentage > 100.0) {
            percentage = 100.0;
        }

        TaggedPicture toReturn = null;
        if (percentage <= 10) {
            toReturn = participant.getPictureSet().getRandomPicture(DressState.FULLY_DRESSED, PictureTag.FACE);
        }

        if (MathUtils.isBetweenIncluding(percentage, 11, 20) || toReturn == null && percentage <= 10) {
            toReturn = participant.getPictureSet().getRandomPictureForStates(DressState.FULLY_DRESSED, DressState.HALF_DRESSED);
        }

        if (MathUtils.isBetweenIncluding(percentage, 21, 40) || toReturn == null && percentage <= 20) {
            toReturn = participant.getPictureSet().getRandomPictureForStates(DressState.HALF_DRESSED);
        }

        if (MathUtils.isBetweenIncluding(percentage, 41, 60) || toReturn == null && percentage <= 40) {
            toReturn = participant.getPictureSet().getRandomPictureForStates(DressState.GARMENT_COVERING);
        }

        if (MathUtils.isBetweenIncluding(percentage, 61, 80) || toReturn == null && percentage <= 60) {
            toReturn = participant.getPictureSet().getRandomPictureForStates(DressState.HANDS_COVERING);
        }

        if (MathUtils.isBetweenIncluding(percentage, 81, 100) || toReturn == null && percentage <= 80) {
            toReturn = participant.getPictureSet().getRandomPictureForStates(DressState.NAKED, DressState.SEE_THROUGH);
        }

        if (toReturn != null) {
            return toReturn;
        } else {
            File[] locFiles = participant.getPictureSet().getPicturesInFolder();

            if (locFiles.length == 0) {
                TeaseLogger.getLogger().log(Level.SEVERE, "Image set selected has no images!");
                return null;
            }

            return new TaggedPicture(locFiles[random.nextInt(locFiles.length)]);
        }
    }
}
