package me.goddragon.teaseai.gui;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.stage.Stage;
import me.goddragon.teaseai.TeaseAI;

/**
 * Created by GodDragon on 18.06.2018.
 */
public class StartupProgressPane {

    private final Stage dialogStage;
    private final FlowPane flowPane;

    public StartupProgressPane() {
        this.dialogStage = new Stage();
        this.flowPane = new FlowPane();
        flowPane.setAlignment(Pos.CENTER);

        Scene scene = new Scene(flowPane, 400, 200);
        dialogStage.setScene(scene);
        dialogStage.setTitle("Loading Tease-AI " + TeaseAI.VERSION + "...");
        dialogStage.setOnCloseRequest(event -> {
            try {
                System.exit(0);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public void addProgressBar(ProgressForm progressForm) {
        flowPane.getChildren().addAll(progressForm.getLabel());

        //We need this to split lines
        Region lineSplit = new Region();
        lineSplit.setPrefSize(Double.MAX_VALUE, 0.0);
        flowPane.getChildren().add(lineSplit);

        flowPane.getChildren().addAll(progressForm.getPb(), progressForm.getPin());
        progressForm.getPb().setPrefWidth(flowPane.getWidth() - 50);
        progressForm.getPin().setPrefWidth(50);
    }

    public void removeProgressBar(ProgressForm progressForm) {
        flowPane.getChildren().removeAll(progressForm.getPb(), progressForm.getPin(), progressForm.getLabel());
    }

    public void show() {
        dialogStage.show();
    }

    public void close() {
        dialogStage.close();
    }

    public Stage getDialogStage() {
        return dialogStage;
    }

    public Pane getFlowPane() {
        return flowPane;
    }
}
