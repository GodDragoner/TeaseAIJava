package me.goddragon.teaseai.api.picture;

import me.goddragon.teaseai.TeaseAI;
import me.goddragon.teaseai.api.chat.ChatParticipant;
import me.goddragon.teaseai.api.session.Session;
import me.goddragon.teaseai.utils.TeaseLogger;

import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

/**
 * Created by GodDragon on 26.03.2018.
 */
public class PictureSelector {

    public TaggedPicture getPicture(Session session, ChatParticipant participant) {
        if(participant.getPictureSet().getTaggedPictures().isEmpty() && participant.getPictureSet().getAllPictures().isEmpty()) {
            TeaseLogger.getLogger().log(Level.INFO, "pictureset tagged and all pics are empty");
            return null;
        }
        long minutesPassed = TimeUnit.MILLISECONDS.toMinutes(session.getRuntime());
        int preferredSessionDuration = TeaseAI.application.PREFERRED_SESSION_DURATION.getInt();
        double percentage = minutesPassed/preferredSessionDuration*100D;

        if(percentage > 80) {
            return participant.getPictureSet().getRandomPictureForStates(DressState.NAKED, DressState.SEE_THROUGH);
        } else if(percentage > 60) {
            return participant.getPictureSet().getRandomPictureForStates(DressState.HANDS_COVERING);
        } else if(percentage > 40) {
            return participant.getPictureSet().getRandomPictureForStates(DressState.GARMENT_COVERING);
        } else if(percentage > 20) {
            return participant.getPictureSet().getRandomPictureForStates(DressState.HALF_DRESSED);
        } else if(percentage > 10) {
            return participant.getPictureSet().getRandomPictureForStates(DressState.FULLY_DRESSED, DressState.HALF_DRESSED);
        } else {
            return participant.getPictureSet().getRandomPicture(DressState.FULLY_DRESSED, PictureTag.FACE);
        }
    }
}
