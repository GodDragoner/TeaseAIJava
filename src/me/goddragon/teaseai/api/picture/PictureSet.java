package me.goddragon.teaseai.api.picture;

import me.goddragon.teaseai.utils.RandomUtils;
import me.goddragon.teaseai.utils.TeaseLogger;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;

/**
 * Created by GodDragon on 26.03.2018.
 */
public class PictureSet {

    private final List<TaggedPicture> taggedPictures = new ArrayList<>();
    private final List<TaggedPicture> allPictures = new ArrayList<>();

    public PictureSet(File folder) {
        TagsFile tagFile = TagsFile.getTagsFile(folder);

        /*for (File file : folder.listFiles()) {
            if (file.isFile() && file.getName().endsWith(".txt")) {
                tagFile = file;
                break;
            }
        }*/

        if(tagFile == null) {
            TeaseLogger.getLogger().log(Level.SEVERE, "Folder '" + folder.getAbsolutePath() + "' is missing a tags file.");
            return;
        }

        for(File taggedFile : tagFile.getTaggedFiles()) {
            taggedPictures.add(new TaggedPicture(taggedFile));
        }
        
        File[] files = folder.listFiles(new FilenameFilter() {
            public boolean accept(File dir, String name) {
                return name.toLowerCase().endsWith(".jpg") || name.toLowerCase().endsWith(".png") || name.toLowerCase().endsWith(".gif");
            }
        });
        
        for (File file : files) {
            allPictures.add(new TaggedPicture(file));
        }
    }

    public TaggedPicture getRandomPictureForStates(DressState... dressStates) {
        return getRandomPictureForTagStates(taggedPictures, Arrays.asList(dressStates), new ArrayList<>());
    }

    public TaggedPicture getRandomPicture(DressState dressState, PictureTag... pictureTags) {
        return getRandomPictureForTagStates(Arrays.asList(new DressState[] {dressState}), Arrays.asList(pictureTags));
    }

    public TaggedPicture getRandomPicture(List<TaggedPicture> taggedPictures, PictureTag... pictureTags) {
        return getRandomPicture(taggedPictures, Arrays.asList(pictureTags));
    }

    public TaggedPicture getRandomPicture(List<TaggedPicture> taggedPictures, List<PictureTag> pictureTags) {
        return getRandomPictureForTagStates(taggedPictures, new ArrayList<>(), pictureTags);
    }

    public TaggedPicture getRandomPictureForTagStates(List<DressState> dressStates, List<PictureTag> pictureTags) {
       return getRandomPictureForTagStates(taggedPictures, dressStates, pictureTags);
    }


    public TaggedPicture getRandomPictureForTagStates(List<TaggedPicture> taggedPictures, List<DressState> dressStates, List<PictureTag> pictureTags) {
        List<TaggedPicture> validPictures = new ArrayList<>();
        pictureLoop:
        for (TaggedPicture taggedPicture : taggedPictures) {
            //Remove all pictures that don't have a specific tag
            for (PictureTag pictureTag : pictureTags) {
                if (pictureTag != null && !taggedPicture.hasTag(pictureTag)) {
                    continue pictureLoop;
                }
            }

            //If we are supposed to search for a specific dress state we check whether our picture fits those guidelines
            if(!dressStates.isEmpty() && !dressStates.contains(taggedPicture.getDressState())) {
                continue pictureLoop;
            }

            //If we reach this point the image is good to display
            validPictures.add(taggedPicture);
        }

        if (validPictures.isEmpty()) {
            //Find the dress state in the list which shows least
            DressState lowestDressState = null;
            for (DressState dressState : dressStates) {
                if (lowestDressState == null || lowestDressState.getRank() > dressState.getRank()) {
                    lowestDressState = dressState;
                }
            }

            DressState lowerDressState = lowestDressState.getNextLowerRank();

            //Try finding an alternative image that shows less
            if (lowerDressState != null) {
                return getRandomPictureForTagStates(Arrays.asList(new DressState[] {lowerDressState}), pictureTags);
            }

            //We don't have any pictures to show anyway
            if (taggedPictures.isEmpty()) {
                if(allPictures.isEmpty()) {
                    return null;
                }
                
                return allPictures.get(RandomUtils.randInt(0, allPictures.size() - 1));
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
    
    public Collection<TaggedPicture> getAllPictures() {
        return allPictures;
    }
}
