package me.goddragon.teaseai.gui.settings;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.scene.control.Alert;
import javafx.scene.control.SelectionMode;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import me.goddragon.teaseai.TeaseAI;
import me.goddragon.teaseai.api.media.MediaHolder;
import me.goddragon.teaseai.api.media.MediaType;
import me.goddragon.teaseai.api.media.MediaURL;

/**
 * Created by GodDragon on 28.03.2018.
 */
public class URLMediaSettings {

    private final SettingsController settingsController;

    public URLMediaSettings(SettingsController settingsController) {
        this.settingsController = settingsController;
    }

    public void initiate() {
        updateURLList();

        settingsController.urlFilesList.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        settingsController.addURLButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                String url = settingsController.addURLTextField.getText();

                if (url != null && url.contains("tumblr.com")) {
                    settingsController.addURLButton.setDisable(true);
                    settingsController.addURLTextField.setDisable(true);

                    new Thread() {
                        @Override
                        public void run() {
                            MediaURL mediaURL = new MediaURL(MediaType.IMAGE, url);
                            mediaURL.saveToFile();
                            TeaseAI.application.getMediaCollection().addMediaHolder(null, mediaURL);

                            TeaseAI.application.runOnUIThread(new Runnable() {
                                @Override
                                public void run() {
                                    updateURLList();
                                    settingsController.addURLButton.setDisable(false);
                                    settingsController.addURLTextField.setDisable(false);
                                }
                            });
                        }
                    }.start();
                } else {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Invalid URL");
                    alert.setHeaderText(null);
                    alert.setContentText("The given URL is either invalid or not supported. Please only use tumblr urls.");

                    alert.showAndWait();
                }
            }
        });

        //Set it as disabled by default because nothing is selected
        settingsController.deleteURLButton.setDisable(true);

        //Delete the url on click
        settingsController.deleteURLButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                for (Object object : settingsController.urlFilesList.getSelectionModel().getSelectedItems()) {
                    MediaURL mediaURL = (MediaURL) object;
                    mediaURL.deleteFile();
                    TeaseAI.application.getMediaCollection().removeMediaHolder(mediaURL);
                }

                settingsController.urlFilesList.getSelectionModel().clearSelection();

                updateURLList();
            }
        });

        settingsController.urlFilesList.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<MediaURL>() {
            @Override
            public void changed(ObservableValue<? extends MediaURL> observable, MediaURL oldValue, MediaURL newValue) {
                if (newValue != null) {
                    settingsController.useURLForTease.setDisable(false);
                    settingsController.deleteURLButton.setDisable(false);
                } else {
                    settingsController. useURLForTease.setDisable(true);
                    settingsController.deleteURLButton.setDisable(true);
                }

                if (settingsController.urlFilesList.getSelectionModel().getSelectedItems().size() == 1) {
                    //Fetch the image on a secondary thread
                    new Thread() {
                        @Override
                        public void run() {
                            settingsController.urlFileImagePreview.setImage(new Image(((MediaURL) settingsController.urlFilesList.getSelectionModel().getSelectedItem()).getRandomMedia().toURI().toString()));
                        }
                    }.start();

                    settingsController.useURLForTease.setSelected(newValue.isUseForTease());
                } else {
                    settingsController.urlFileImagePreview.setImage(null);
                }
            }
        });


        settingsController.useURLForTease.selectedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                for (Object object : settingsController.urlFilesList.getSelectionModel().getSelectedItems()) {
                    MediaURL mediaURL = (MediaURL) object;
                    mediaURL.setUseForTease(newValue);
                    mediaURL.saveToFile();
                }
            }
        });


        settingsController.urlFileImagePreview.setPreserveRatio(true);
        settingsController.urlFileImagePreview.fitWidthProperty().bind(settingsController.urlImageViewStackPane.widthProperty());
        settingsController.urlFileImagePreview.fitHeightProperty().bind(settingsController.urlImageViewStackPane.heightProperty());
    }

    private void updateURLList() {
        settingsController.urlFilesList.getItems().clear();
        settingsController.urlFileDragDropList.getItems().clear();

        for (MediaHolder mediaHolder : TeaseAI.application.getMediaCollection().getMediaHolders().get(MediaType.IMAGE)) {
            if (mediaHolder instanceof MediaURL) {
                settingsController.urlFilesList.getItems().add(mediaHolder);
                settingsController.urlFileDragDropList.getItems().add(mediaHolder);
            }
        }
    }
}
