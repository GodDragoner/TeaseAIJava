package me.goddragon.teaseai.gui;

import javafx.concurrent.Task;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ProgressIndicator;
import me.goddragon.teaseai.TeaseAI;

/**
 * Created by GodDragon on 17.06.2018.
 */
public class ProgressForm {
    private final Label label = new Label();
    private final ProgressBar pb = new ProgressBar();
    private final ProgressIndicator pin = new ProgressIndicator();

    public ProgressForm(String name) {
        this(name, null);
    }

    public ProgressForm(String name, Task<Void> task) {
        label.setText(name);
        pb.setProgress(-1F);
        pin.setProgress(-1F);

        if(task != null) {
            bindProgressBar(task);
        }
    }

    public void bindProgressBar(Task<?> task)  {
        pb.progressProperty().bind(task.progressProperty());
        pin.progressProperty().bind(task.progressProperty());
    }

    public void setNameSync(String name) {
        TeaseAI.application.runOnUIThread(new Runnable() {
            @Override
            public void run() {
                setName(name);
            }
        });
    }

    public void setName(String name) {
        label.setText(name);
    }

    public Label getLabel() {
        return label;
    }

    public ProgressBar getPb() {
        return pb;
    }

    public ProgressIndicator getPin() {
        return pin;
    }
}