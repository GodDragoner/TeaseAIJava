package me.goddragon.teaseai.api.picture;

import me.goddragon.teaseai.TeaseAI;
import me.goddragon.teaseai.api.chat.ChatParticipant;
import me.goddragon.teaseai.api.session.Session;
import me.goddragon.teaseai.utils.TeaseLogger;

import java.io.File;
import java.io.FilenameFilter;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

/**
 * Created by GodDragon on 26.03.2018.
 */
public class PictureSelector {

    public TaggedPicture getPicture(Session session, ChatParticipant participant) {
        if(participant.getPictureSet().getTaggedPictures().isEmpty() && participant.getPictureSet().getFolder().listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return (name.toLowerCase().endsWith(".jpg") || name.toLowerCase().endsWith(".png") || name.toLowerCase().endsWith(".gif"));
            }
        }).length == 0) {
            return null;
        }
        
        long minutesPassed = TimeUnit.MILLISECONDS.toMinutes(session.getRuntime());
        int preferredSessionDuration = TeaseAI.application.PREFERRED_SESSION_DURATION.getInt();
        double percentage = minutesPassed/preferredSessionDuration*100D;

        if (percentage <= 10)
        {
            TaggedPicture toReturn = participant.getPictureSet().getRandomPicture(DressState.FULLY_DRESSED, PictureTag.FACE);
            if (toReturn != null)
            {
                return toReturn;
            }
        }
        if (percentage <= 20)
        {
            TaggedPicture toReturn = participant.getPictureSet().getRandomPictureForStates(DressState.FULLY_DRESSED, DressState.HALF_DRESSED);
            if (toReturn != null)
            {
                return toReturn;
            }
        }
        if (percentage <= 40)
        {
            TaggedPicture toReturn = participant.getPictureSet().getRandomPictureForStates(DressState.HALF_DRESSED);
            if (toReturn != null)
            {
                return toReturn;
            }
        }
        if (percentage <= 60)
        {
            TaggedPicture toReturn = participant.getPictureSet().getRandomPictureForStates(DressState.GARMENT_COVERING);
            if (toReturn != null)
            {
                return toReturn;
            }
        }
        if (percentage <= 80)
        {
            TaggedPicture toReturn = participant.getPictureSet().getRandomPictureForStates(DressState.HANDS_COVERING);
            if (toReturn != null)
            {
                return toReturn;
            }
        }
        TaggedPicture toReturn = participant.getPictureSet().getRandomPictureForStates(DressState.NAKED, DressState.SEE_THROUGH);
        if (toReturn != null)
        {
            return toReturn;
        }
        else
        {
            File setFolder = participant.getPictureSet().getFolder();
            File[] locFiles = setFolder.listFiles(new FilenameFilter() {
                @Override
                public boolean accept(File dir, String name) {
                    return (name.toLowerCase().endsWith(".jpg") || name.toLowerCase().endsWith(".png") || name.toLowerCase().endsWith(".gif"));
                }
            });
            if (locFiles.length == 0)
            {
                TeaseLogger.getLogger().log(Level.SEVERE, "Image set selected has no images!!");
                return null;
            }
            
            Random random = new Random();
            return new TaggedPicture(locFiles[random.nextInt(locFiles.length)]);
        }
    }
}
