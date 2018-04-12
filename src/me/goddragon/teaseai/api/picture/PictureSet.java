package me.goddragon.teaseai.api.picture;

import me.goddragon.teaseai.utils.RandomUtils;
import me.goddragon.teaseai.utils.TeaseLogger;

import java.io.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;

/**
 * Created by GodDragon on 26.03.2018.
 */
public class PictureSet {

    private final List<TaggedPicture> taggedPictures = new ArrayList<>();

    public PictureSet(File folder) {
        File tagFile = null;
        for (File file : folder.listFiles()) {
            if (file.isFile() && file.getName().endsWith(".txt")) {
                tagFile = file;
                break;
            }
        }

        try {
            // Open the file
            FileInputStream fstream = new FileInputStream(tagFile);
            BufferedReader br = new BufferedReader(new InputStreamReader(fstream));

            String strLine;

            //Read File Line By Line
            while ((strLine = br.readLine()) != null) {
                if (!strLine.contains(" ")) {
                    TeaseLogger.getLogger().log(Level.SEVERE, "Illegal tagged file. Line is '" + strLine + "' in folder " + folder.getAbsolutePath());
                    return;
                }

                int endIndex =  strLine.toLowerCase().indexOf(".jpg") + 4;
                String fileName = strLine.substring(0, endIndex);
                String[] split = strLine.substring(endIndex).trim().split(" ");
                if(split.length < 1) {
                    TeaseLogger.getLogger().log(Level.SEVERE, "Illegal tagged file. Line is '" + strLine + "' in folder " + folder.getAbsolutePath());
                    return;
                }

                taggedPictures.add(new TaggedPicture(fileName, split, folder));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public TaggedPicture getRandomPictureForStates(DressState... dressStates) {
        List<TaggedPicture> validPictures = new ArrayList<>();

        pictureLoop: for(TaggedPicture taggedPicture : taggedPictures) {
            for(DressState dressState : dressStates) {
                if(dressState != null && !taggedPicture.hasDressState(dressState)) {
                    continue pictureLoop;
                }

                //If we reach this point the image is good to display
                validPictures.add(taggedPicture);
            }
        }

        //Find the dress state which shows least
        DressState lowestDressState = null;
        for(DressState dressState : dressStates) {
            if(lowestDressState == null || lowestDressState.getRank() > dressState.getRank()) {
                lowestDressState = dressState;
            }
        }

        if(validPictures.isEmpty()) {
            DressState lowerDressState = lowestDressState.getNextLowerRank();

            //Try finding an alternative image that shows less
            if(lowerDressState != null) {
                return getRandomPictureForStates(lowestDressState);
            }

            //We don't have any pictures to show anyway
            if(taggedPictures.isEmpty()) {
                return null;
            }
            //Okay we have been beaten. Show a random image
            else {
                return taggedPictures.get(RandomUtils.randInt(0, taggedPictures.size() - 1));
            }
        }

        return validPictures.get(RandomUtils.randInt(0, validPictures.size() - 1));
    }

    public TaggedPicture getRandomPicture(DressState dressState, PictureTag... pictureTags) {
        List<TaggedPicture> validPictures = new ArrayList<>();

        pictureLoop: for(TaggedPicture taggedPicture : taggedPictures) {
            if(dressState == null || taggedPicture.hasDressState(dressState)) {
                for(PictureTag pictureTag : pictureTags) {
                    if(pictureTag != null && !taggedPicture.hasTag(pictureTag)) {
                        continue pictureLoop;
                    }
                }

                //If we reach this point the image is good to display
                validPictures.add(taggedPicture);
            }
        }

        if(validPictures.isEmpty()) {
            DressState lowerDressState = dressState.getNextLowerRank();

            //Try finding an alternative image that shows less
            if(lowerDressState != null) {
                return getRandomPicture(dressState, pictureTags);
            }

            //We don't have any pictures to show anyway
            if(taggedPictures.isEmpty()) {
                return null;
            }
            //Okay we have been beaten. Show a random image
            else {
                return taggedPictures.get(RandomUtils.randInt(0, taggedPictures.size() - 1));
            }
        }

        return validPictures.get(RandomUtils.randInt(0, validPictures.size() - 1));
    }

    public Collection<TaggedPicture> getTaggedPictures() {
        return taggedPictures;
    }
}
