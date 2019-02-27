package me.goddragon.teaseai.api.scripts.nashorn;

import me.goddragon.teaseai.TeaseAI;
import me.goddragon.teaseai.api.config.PersonalitiesSettingsHandler;
import me.goddragon.teaseai.api.config.PersonalitySettingsPanel;
import me.goddragon.teaseai.api.config.PersonalityVariable;
import me.goddragon.teaseai.api.config.SpinnerComponent;
import me.goddragon.teaseai.api.scripts.personality.Personality;
import me.goddragon.teaseai.api.scripts.personality.PersonalityManager;
import me.goddragon.teaseai.utils.MathUtils;
import me.goddragon.teaseai.utils.TeaseLogger;

import java.util.Arrays;
import java.util.logging.Level;

public class AddSpinnerFunction extends CustomFunction {

    public AddSpinnerFunction() {
        super("addSpinner", "addValueRange", "addRange");
    }

    @Override
    public boolean isFunction() {
        return true;
    }

    @Override
    public Object call(Object object, Object... args) {
        super.call(object, args);

        if (MathUtils.isBetweenIncluding(args.length, 4, 5)) {
            int spinnerType = SpinnerComponent.INTEGER_TYPE;

            //Please use format type in the future for performance boost (won't have to check for your type manually)
            if(args.length == 5) {
                if(args[4].toString().equalsIgnoreCase("double")) {
                    spinnerType = SpinnerComponent.DOUBLE_TYPE;
                }
                //Integer is already assigned as default so we can check whether the provided type is NOT an integer and then we can log the warning
                else if(!args[4].toString().equalsIgnoreCase("integer")) {
                    TeaseLogger.getLogger().log(Level.WARNING, "Trying to add gui component '" + args[1] + "' with invalid value type '" + args[4] + "'.");
                }
            }


            Personality personality;

            if (TeaseAI.application.getSession() == null) {
                personality = PersonalityManager.getManager().getLoadingPersonality();
            } else {
                personality = PersonalityManager.getManager().getActivePersonality();
            }

            PersonalitySettingsPanel panel = personality.getSettingsHandler().getPanel((String) args[0]);

            if (panel == null) {
                TeaseLogger.getLogger().log(Level.SEVERE, "Called " + getFunctionName() + " method with invalid panel name " + args[0]);
                return null;
            } else {
                PersonalityVariable variable = personality.getVariableHandler().getVariable((String) args[1]);

                if (variable == null) {
                    TeaseLogger.getLogger().log(Level.SEVERE, "Called " + getFunctionName() + " method with invalid variable name " + args[1]);
                    return null;
                } else {
                    if (!PersonalitiesSettingsHandler.getHandler().hasComponent(variable)) {
                        PersonalitiesSettingsHandler.getHandler().addGuiComponent(variable);
                        if(args.length == 4) {
                            //Check for potential double when type was not given
                            if(args[2] instanceof Double || args[3] instanceof Double || args[2].toString().contains(".") || args[3].toString().contains(".")) {
                                spinnerType = SpinnerComponent.DOUBLE_TYPE;
                            }
                        }

                        if (spinnerType == SpinnerComponent.DOUBLE_TYPE) {
                            double min = 0;
                            double max = 0;

                            try {
                                min = MathUtils.tryParseDouble(args[2]);
                            } catch (NumberFormatException ex) {
                                TeaseLogger.getLogger().log(Level.SEVERE, "Failed to add custom setting. '" + args[2] + "' is not a double.");
                            }

                            try {
                                max = MathUtils.tryParseDouble(args[3]);
                            } catch (NumberFormatException ex) {
                                TeaseLogger.getLogger().log(Level.SEVERE, "Failed to add custom setting. '" + args[3] + "' is not a double.");
                            }

                            panel.addDoubleSpinner(variable, min, max);
                        } else if(spinnerType == SpinnerComponent.INTEGER_TYPE) {
                            int min = 0;
                            int max = 0;

                            try {
                                min = MathUtils.tryParseInteger(args[2]);
                            } catch (NumberFormatException ex) {
                                TeaseLogger.getLogger().log(Level.SEVERE, "Failed to add custom setting. '" + args[2] + "' is not an integer.");
                            }

                            try {
                                max = MathUtils.tryParseInteger(args[3]);
                            } catch (NumberFormatException ex) {
                                TeaseLogger.getLogger().log(Level.SEVERE, "Failed to add custom setting. '" + args[3] + "' is not an integer.");
                            }

                            panel.addIntegerSpinner(variable, min, max);
                        }
                    }
                }
            }
        } else {
            if(args.length > 0) {
                TeaseLogger.getLogger().log(Level.SEVERE, getFunctionName() + " called with invalid args:" + Arrays.asList(args).toString());
                return null;
            } else {
                TeaseLogger.getLogger().log(Level.SEVERE, "Called " + getFunctionName() + " method without parameters.");
                return null;
            }
        }

        return null;
    }

}
