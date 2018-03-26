package me.goddragon.teaseai.api.picture;

/**
 * Created by GodDragon on 26.03.2018.
 */
public enum DressState {

    FULLY_DRESSED("TagFullyDressed", 0),
    HALF_DRESSED("TagHalfDressed", 1),
    GARMENT_COVERING("TagGarmentCovering", 2),
    HANDS_COVERING("TagHandsCovering", 3),
    SEE_THROUGH("TagSeeThrough", 4),
    NAKED("TagNaked", 5);


    private final String tagName;
    private final int rank;

    DressState(String tagName, int rank) {
        this.tagName = tagName;
        this.rank = rank;
    }

    public DressState getNextLowerRank() {
        for (DressState dressState : values()) {
            if (dressState.rank == this.rank - 1) {
                return dressState;
            }
        }

        return null;
    }

    public static DressState getByTag(String tagName) {
        for (DressState dressState : values()) {
            if (dressState.tagName.equalsIgnoreCase(tagName)) {
                return dressState;
            }
        }

        return null;
    }

    public int getRank() {
        return rank;
    }
}
