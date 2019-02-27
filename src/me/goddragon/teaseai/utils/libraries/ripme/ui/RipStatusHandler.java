package me.goddragon.teaseai.utils.libraries.ripme.ui;

import me.goddragon.teaseai.utils.libraries.ripme.ripper.AbstractRipper;

/**
 * @author Mads
 */
public interface RipStatusHandler {

    void update(AbstractRipper ripper, RipStatusMessage message);

}