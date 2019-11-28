package me.goddragon.teaseai.gui.settings;

import me.goddragon.teaseai.TeaseAI;

public class UrlProgress {
    public static boolean inProgress = false;
    public static int completed = 0;

    public static void incrementCompleted() {
        completed++;

        TeaseAI.application.runOnUIThread(new Runnable() {
            @Override
            public void run() {
                SettingsController.getController().urlProgressLabel.setText("Fetching...  " + completed + "/? files.");
            }
        });
    }
}
