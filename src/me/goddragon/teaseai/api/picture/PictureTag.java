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
    ASS("TagAss", TagType.BODYPART),
    BOOBS("TagBoobs", TagType.BODYPART),
    COCK("TagCock", TagType.BODYPART),
    CREAMYPUSSY("TagCreamyPussy", TagType.BODYPART),
    CUMCOVEREDASS("TagCumCoveredAss", TagType.BODYPART),
    CUMCOVEREDBOOBS("TagCumCoveredBoobs", TagType.BODYPART),
    CUMCOVEREDCOCK("TagCumCoveredCock", TagType.BODYPART),
    CUMCOVEREDFACE("TagCumCoveredFace", TagType.BODYPART),
    CUMCOVEREDFEET("TagCumCoveredFeet", TagType.BODYPART),
    CUMCOVEREDFINGERS("TagCumCoveredFingers", TagType.BODYPART),
    CUMCOVEREDLEGS("TagCumCoveredLegs", TagType.BODYPART),
    CUMCOVEREDPUSSY("TagCumCoveredPussy", TagType.BODYPART),
    DRIPPINGPUSSY("TagDrippingPussy", TagType.BODYPART),
    FACE("TagFace", TagType.BODYPART),
    FEET("TagFeet", TagType.BODYPART),
    FINGERS("TagFingers", TagType.BODYPART),
    LEGS("TagLegs", TagType.BODYPART),
    PUSSY("TagPussy", TagType.BODYPART),
    //BODY TYPES
    BLONDE("TagBlonde", TagType.BODYTYPE),
    BRUNETTE("TagBrunette", TagType.BODYTYPE),
    REDHEAD("TagRedhead", TagType.BODYTYPE),
    SLIM("TagSlim", TagType.BODYTYPE),
    THICK("TagThick", TagType.BODYTYPE),
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
    ONEFEMALE("TagOneFemale", TagType.PEOPLEINVOLVED),
    ONEFEMALETWOMALE("TagOneFemaleTwoMale", TagType.PEOPLEINVOLVED),
    ONEMALE("TagOneMale", TagType.PEOPLEINVOLVED),
    ONEMALEONEFEMALE("TagOneMaleOneFemale", TagType.PEOPLEINVOLVED),
    ONEMALETWOFEMALE("TagOneMaleTwoFemale", TagType.PEOPLEINVOLVED),
    ORGY("TagOrgy", TagType.PEOPLEINVOLVED),
    THREEFEMALE("TagThreeFemale", TagType.PEOPLEINVOLVED),
    THREEMALE("TagThreeMale", TagType.PEOPLEINVOLVED),
    TWOFEMALE("TagTwoFemale", TagType.PEOPLEINVOLVED),
    TWOMALE("TagTwoMale", TagType.PEOPLEINVOLVED),
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
    BATHINGSUIT("TagBathingSuit", TagType.ACCESSORIES),
    BRA("TagBra", TagType.ACCESSORIES),
    DILDO("TagDildo", TagType.ACCESSORIES),
    PANTIES("TagPanties", TagType.ACCESSORIES),
    PIERCING("TagPiercing", TagType.ACCESSORIES),
    VIBRATOR("TagVibrator", TagType.ACCESSORIES),
    WETPANTIES("TagWetPanties", TagType.ACCESSORIES);

    private final String tagName;
    private final TagType thisTagType;
    public static enum TagType
    {
    	BODYPART,
    	BODYTYPE,
    	CATEGORY,
    	VIEW,
    	PEOPLEINVOLVED,
    	ACTION,
    	ACCESSORIES,
    	OTHER;
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
    
    public static PictureTag[] getPictureTagsByType(TagType type)
    {
    	PictureTag[] toReturn;
    	ArrayList<PictureTag> tagsList = new ArrayList<PictureTag>();
        for (PictureTag pictureTag : values()) {
            if (pictureTag.thisTagType.equals(type)) {
            	tagsList.add(pictureTag);
            }
        }
        toReturn = new PictureTag[tagsList.size()];
        return tagsList.toArray(toReturn);
    }

    public String tagName()
    {
    	return tagName;
    }
    
    public TagType getTagType()
    {
    	return thisTagType;
    }
}
