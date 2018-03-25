package me.goddragon.teaseai.gui;

import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.StackPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import me.goddragon.teaseai.api.chat.ChatHandler;

import java.io.File;
import java.net.MalformedURLException;

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

    public Controller() {
    }

    public void initiate() {
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

    public void showPicture() {
        MediaPlayer mediaPlayer;
        try {
            mediaPlayer = new MediaPlayer(new Media(new File("D:\\Downloads\\I'm gonna get you off in 3 seconds.mp4").toURI().toURL().toExternalForm()));
            mediaPlayer.setAutoPlay(true);
            mediaView.setPreserveRatio(true);
            mediaView.fitWidthProperty().bind(mediaViewBox.widthProperty());
            mediaView.fitHeightProperty().bind(mediaViewBox.heightProperty());
            mediaView.setMediaPlayer(mediaPlayer);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        for(int x = 0; x < 30; x++) {
            chatWindow.getChildren().add(new Text("test \n"));
        }
    }
}
