package me.goddragon.teaseai.api.picture;

import me.goddragon.teaseai.utils.TeaseLogger;

import java.io.File;
import java.util.ArrayList;
import java.util.logging.Level;

/**
 * Created by GodDragon on 26.03.2018.
 */
public enum PictureTag {
    /*
     * Sorted in alphabetical order by category in the order of categories
     * BODYPART->BODYTYPE->CATEGORY->VIEW->PEOPLEINVOLVED->ACTION->ACCESSORIES->OTHER
     */

    //BODY PARTS
    ASS("TagAss", TagType.BODY_PART),
    BOOBS("TagBoobs", TagType.BODY_PART),
    COCK("TagCock", TagType.BODY_PART),
    CREAMYPUSSY("TagCreamyPussy", TagType.BODY_PART),
    CUMCOVEREDASS("TagCumCoveredAss", TagType.BODY_PART),
    CUMCOVEREDBOOBS("TagCumCoveredBoobs", TagType.BODY_PART),
    CUMCOVEREDCOCK("TagCumCoveredCock", TagType.BODY_PART),
    CUMCOVEREDFACE("TagCumCoveredFace", TagType.BODY_PART),
    CUMCOVEREDFEET("TagCumCoveredFeet", TagType.BODY_PART),
    CUMCOVEREDFINGERS("TagCumCoveredFingers", TagType.BODY_PART),
    CUMCOVEREDLEGS("TagCumCoveredLegs", TagType.BODY_PART),
    CUMCOVEREDPUSSY("TagCumCoveredPussy", TagType.BODY_PART),
    DRIPPINGPUSSY("TagDrippingPussy", TagType.BODY_PART),
    FACE("TagFace", TagType.BODY_PART),
    FEET("TagFeet", TagType.BODY_PART),
    FINGERS("TagFingers", TagType.BODY_PART),
    LEGS("TagLegs", TagType.BODY_PART),
    PUSSY("TagPussy", TagType.BODY_PART),

    //BODY TYPES
    BLONDE("TagBlonde", TagType.BODY_TYPE),
    BRUNETTE("TagBrunette", TagType.BODY_TYPE),
    REDHEAD("TagRedhead", TagType.BODY_TYPE),
    SLIM("TagSlim", TagType.BODY_TYPE),
    THICK("TagThick", TagType.BODY_TYPE),

    //CATEGORIES
    ALLFOURS("TagAllFours", TagType.CATEGORY),
    BATH("TagBath", TagType.CATEGORY),
    BONDAGE("TagBondage", TagType.CATEGORY),
    CAPTIONS("TagCaptions", TagType.CATEGORY),
    FEMDOM("TagFemDom", TagType.CATEGORY),
    GAY("TagGay", TagType.CATEGORY),
    HARDCORE("TagHardcore", TagType.CATEGORY),
    LESBIAN("TagLesbian", TagType.CATEGORY),
    LEZDOM("TagLezDom", TagType.CATEGORY),
    MALEDOM("TagMaleDom", TagType.CATEGORY),
    OUTDOORS("TagOutdoors", TagType.CATEGORY),
    POINTOFVIEW("TagPointOfView", TagType.CATEGORY),
    SHOWER("TagShower", TagType.CATEGORY),
    SOFTCORE("TagSoftcore", TagType.CATEGORY),

    //VIEW
    SIDE_VIEW("TagSideView", TagType.VIEW),
    CLOSE_UP("TagCloseUp", TagType.VIEW),

    //PEOPLE INVOLVED
    ONEFEMALE("TagOneFemale", TagType.PEOPLE_INVOLVED),
    ONEFEMALETWOMALE("TagOneFemaleTwoMale", TagType.PEOPLE_INVOLVED),
    ONEMALE("TagOneMale", TagType.PEOPLE_INVOLVED),
    ONEMALEONEFEMALE("TagOneMaleOneFemale", TagType.PEOPLE_INVOLVED),
    ONEMALETWOFEMALE("TagOneMaleTwoFemale", TagType.PEOPLE_INVOLVED),
    ORGY("TagOrgy", TagType.PEOPLE_INVOLVED),
    THREEFEMALE("TagThreeFemale", TagType.PEOPLE_INVOLVED),
    THREEMALE("TagThreeMale", TagType.PEOPLE_INVOLVED),
    TWOFEMALE("TagTwoFemale", TagType.PEOPLE_INVOLVED),
    TWOMALE("TagTwoMale", TagType.PEOPLE_INVOLVED),

    //ACTIONS
    ANAL("TagAnal", TagType.ACTION),
    BLOWJOB("TagBlowJob", TagType.ACTION),
    COWGIRL("TagCowGirl", TagType.ACTION),
    DOGGYSTYLE("TagDoggyStyle", TagType.ACTION),
    FACESITTING("TagFaceSitting", TagType.ACTION),
    FINGERING("TagFingering", TagType.ACTION),
    GANGBANG("TagGangBang", TagType.ACTION),
    GLARING("TagGlaring", TagType.ACTION),
    HANDJOB("TagHandJob", TagType.ACTION),
    LICKING("TagLicking", TagType.ACTION),
    MASTURBATING("TagMasturbating", TagType.ACTION),
    MISSIONARY("TagMissionary", TagType.ACTION),
    RUBBING("TagRubbing", TagType.ACTION),
    SMILING("TagSmiling", TagType.ACTION),
    STANDING("TagStanding", TagType.ACTION),
    SUCKING("TagSucking", TagType.ACTION),

    //ACCESSORIES
    BATHING_SUIT("TagBathingSuit", TagType.ACCESSORIES),
    BRA("TagBra", TagType.ACCESSORIES),
    DILDO("TagDildo", TagType.ACCESSORIES),
    PANTIES("TagPanties", TagType.ACCESSORIES),
    PIERCING("TagPiercing", TagType.ACCESSORIES),
    VIBRATOR("TagVibrator", TagType.ACCESSORIES),
    WET_PANTIES("TagWetPanties", TagType.ACCESSORIES);

    private final String tagName;
    private final TagType thisTagType;

    public enum TagType {
        BODY_PART,
        BODY_TYPE,
        CATEGORY,
        VIEW,
        PEOPLE_INVOLVED,
        ACTION,
        ACCESSORIES,
        OTHER
    }

    PictureTag(String tagName, TagType type) {
        this.tagName = tagName;
        this.thisTagType = type;
    }

    public static PictureTag getByTag(File folder, String tagName) {
        for (PictureTag pictureTag : values()) {
            if (pictureTag.tagName.equalsIgnoreCase(tagName)) {
                return pictureTag;
            }
        }

        TeaseLogger.getLogger().log(Level.WARNING, "Invalid tag '" + tagName + "' detected in folder " + folder.getAbsolutePath());

        return null;
    }

    public static PictureTag[] getPictureTagsByType(TagType type) {
        PictureTag[] toReturn;
        ArrayList<PictureTag> tagsList = new ArrayList<>();

        for (PictureTag pictureTag : values()) {
            if (pictureTag.thisTagType.equals(type)) {
                tagsList.add(pictureTag);
            }
        }

        toReturn = new PictureTag[tagsList.size()];
        return tagsList.toArray(toReturn);
    }

    public String getTagName() {
        return tagName;
    }

    public TagType getTagType() {
        return thisTagType;
    }
}