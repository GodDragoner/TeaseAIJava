package me.goddragon.teaseai.gui.main;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.StackPane;
import javafx.scene.media.MediaView;
import javafx.scene.paint.Color;
import javafx.scene.text.TextFlow;
import me.goddragon.teaseai.TeaseAI;
import me.goddragon.teaseai.api.chat.ChatHandler;
import me.goddragon.teaseai.api.scripts.personality.Personality;
import me.goddragon.teaseai.api.scripts.personality.PersonalityManager;
import me.goddragon.teaseai.gui.settings.SettingsController;

public class Controller {

    @FXML
    private MediaView mediaView;

    @FXML
    private ImageView imageView;

    @FXML
    private StackPane mediaViewBox;

    @FXML
    private TextFlow chatWindow;

    @FXML
    private TextField chatTextField;

    @FXML
    private ScrollPane chatScrollPane;

    @FXML
    private ChoiceBox personalityChoiceBox;

    @FXML
    private Button startChatButton;

    @FXML
    private Menu menuSettingsButton;

    public Controller() {
    }

    public void initiate() {
        personalityChoiceBox.setTooltip(new Tooltip("Select the personality"));

        mediaView.setPreserveRatio(true);
        mediaView.fitWidthProperty().bind(mediaViewBox.widthProperty());
        mediaView.fitHeightProperty().bind(mediaViewBox.heightProperty());

        imageView.setPreserveRatio(true);
        imageView.fitWidthProperty().bind(mediaViewBox.widthProperty());
        imageView.fitHeightProperty().bind(mediaViewBox.heightProperty());
        mediaViewBox.setBackground(new Background(new BackgroundFill(Color.BLACK, CornerRadii.EMPTY, Insets.EMPTY)));

        chatTextField.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent ke) {
                if (ke.getCode().equals(KeyCode.ENTER)) {
                    String currentText = chatTextField.getText();

                    if(currentText.length() == 0 || currentText == null) {
                        return;
                    }

                    currentText = currentText.trim();

                    ChatHandler.getHandler().getSubParticipant().sendMessage(currentText);

                    //Clear the chat field
                    chatTextField.setText("");

                    //Call the sub message event
                    ChatHandler.getHandler().onSubMessage(currentText);
                }
            }
        });

        startChatButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                if(PersonalityManager.getManager().getActivePersonality() != null) {
                    return;
                }

                PersonalityManager.getManager().setActivePersonality((Personality) getPersonalityChoiceBox().getSelectionModel().getSelectedItem());

                synchronized (TeaseAI.application.getScriptThread()) {
                    TeaseAI.application.getScriptThread().notify();
                }

                personalityChoiceBox.setDisable(true);
                startChatButton.setDisable(true);
            }
        });

        Label settingsLabel = new Label("Settings");
        menuSettingsButton.setGraphic(settingsLabel);
        settingsLabel.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent  e) {
                SettingsController.openGUI();
            }
        });
    }

    public TextField getChatTextField() {
        return chatTextField;
    }

    public MediaView getMediaView() {
        return mediaView;
    }

    public ImageView getImageView() {
        return imageView;
    }

    public StackPane getMediaViewBox() {
        return mediaViewBox;
    }

    public TextFlow getChatWindow() {
        return chatWindow;
    }

    public ScrollPane getChatScrollPane() {
        return chatScrollPane;
    }

    public ChoiceBox getPersonalityChoiceBox() {
        return personalityChoiceBox;
    }
}
