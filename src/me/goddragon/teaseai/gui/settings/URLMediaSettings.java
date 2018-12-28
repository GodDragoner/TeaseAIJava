package me.goddragon.teaseai.gui.settings;

import java.io.File;
import java.net.URL;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.scene.control.Alert;
import javafx.scene.control.SelectionMode;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import me.goddragon.teaseai.TeaseAI;
import me.goddragon.teaseai.api.media.MediaHolder;
import me.goddragon.teaseai.api.media.MediaType;
import me.goddragon.teaseai.api.media.MediaURL;
import me.goddragon.teaseai.utils.libraries.ripme.App;
import me.goddragon.teaseai.utils.libraries.ripme.ripper.AbstractRipper;

/**
 * Created by GodDragon on 28.03.2018.
 */
public class URLMediaSettings {

    private final SettingsController settingsController;

    public URLMediaSettings(SettingsController settingsController) {
        this.settingsController = settingsController;
    }

    @SuppressWarnings("unchecked")
    public void initiate() {
        updateURLList();

        settingsController.urlFilesList.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        settingsController.addURLButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                String url = settingsController.addURLTextField.getText().toLowerCase();
                //Ski23 testing
                
                if (url != null && !url.trim().equals(""))
                {
                    try
                    {
                        URL testUrl = new URL(url);
                        AbstractRipper.getRipper(testUrl);
                    }
                    catch (Exception e)
                    {
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setTitle("Invalid URL");
                        alert.setHeaderText(null);
                        alert.setContentText("The given URL is either invalid or not supported. Please only use supported urls.");

                        alert.showAndWait();
                        return;
                    }
                    settingsController.addURLButton.setDisable(true);
                    settingsController.addURLTextField.setDisable(true);
                    settingsController.refreshURLButton.setDisable(true);
    
                    settingsController.addURLTextField.setText(url);
                    
                    new Thread() {
                        @Override
                        public void run() {
                            File mediaFile;
                            try
                            {
                                mediaFile = App.mediaUrlRip(url, MediaURL.URL_FILE_PATH, false);
                            }
                            catch (Exception e)
                            {
                                return;
                            }
                            MediaURL mediaURL = new MediaURL(MediaType.IMAGE, mediaFile);
                            TeaseAI.application.getMediaCollection().addMediaHolder(null, mediaURL);
    
                            TeaseAI.application.runOnUIThread(new Runnable() {
                                @Override
                                public void run() {
                                    updateURLList();
                                    settingsController.addURLButton.setDisable(false);
                                    settingsController.addURLTextField.setDisable(false);
                                    settingsController.refreshURLButton.setDisable(false);
                                }
                            });
                        }
                    }.start();
                }
                else {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Invalid URL");
                    alert.setHeaderText(null);
                    alert.setContentText("The given URL is either invalid or not supported. Please only use supported urls.");

                    alert.showAndWait();
                }
                return;
            }
        });

        //settingsController.refreshURLButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
            /*@Override
            public void handle(MouseEvent event) {
                new Thread() {
                    @Override
                    public void run() {
                        settingsController.addURLButton.setDisable(true);
                        settingsController.addURLTextField.setDisable(true);
                        settingsController.refreshURLButton.setDisable(true);
                        settingsController.deleteURLButton.setDisable(true);

                        for (Object object : settingsController.urlFilesList.getSelectionModel().getSelectedItems()) {
                            MediaURL mediaURL = (MediaURL) object;
                            //ski23 start
                            settingsController.addURLButton.setDisable(true);
                            settingsController.addURLTextField.setDisable(true);
                            settingsController.refreshURLButton.setDisable(true);
            
                            settingsController.addURLTextField.setText(mediaURL.getUrl());
                            
                            new Thread() {
                                @Override
                                public void run() {
                                    File mediaFile;
                                    try
                                    {
                                        mediaFile = App.mediaUrlRip(mediaURL.getUrl(), MediaURL.URL_FILE_PATH, false);
                                    }
                                    catch (Exception e)
                                    {
                                        return;
                                    }
                                    MediaURL mediaURL = new MediaURL(MediaType.IMAGE, mediaFile);
                                    TeaseAI.application.getMediaCollection().addMediaHolder(null, mediaURL);
            
                                    TeaseAI.application.runOnUIThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            updateURLList();
                                            settingsController.addURLButton.setDisable(false);
                                            settingsController.addURLTextField.setDisable(false);
                                            settingsController.refreshURLButton.setDisable(false);
                                        }
                                    });
                                }
                            }.start();
                            
                            
                            
                            
                            //ski23 end
                            
                            mediaURL.loadImagesFromTumblrURL(settingsController.urlProgressLabel);
                            mediaURL.saveToFile();

                            TeaseAI.application.runOnUIThread(new Runnable() {
                                @Override
                                public void run() {
                                    updateURLList();
                                    settingsController.addURLButton.setDisable(false);
                                    settingsController.addURLTextField.setDisable(false);
                                    settingsController.refreshURLButton.setDisable(false);
                                    settingsController.deleteURLButton.setDisable(false);
                                }
                            });
                        }
                    }
                }.start();
            }*/
        //});

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
                    settingsController.refreshURLButton.setDisable(false);
                } else {
                    settingsController.useURLForTease.setDisable(true);
                    settingsController.deleteURLButton.setDisable(true);
                    settingsController.refreshURLButton.setDisable(true);
                }

                if (settingsController.urlFilesList.getSelectionModel().getSelectedItems().size() == 1) {

                    //Fetch the image on a secondary thread
                    new Thread() {
                        @Override
                        public void run() {
                            //No media found
                            if(((MediaURL) settingsController.urlFilesList.getSelectionModel().getSelectedItem()).getMediaURLs().isEmpty()) {
                                settingsController.urlFileImagePreview.setImage(null);
                                return;
                            }

                            settingsController.urlFileImagePreview.setImage(new Image(((MediaURL) settingsController.urlFilesList.getSelectionModel().getSelectedItem()).getRandomMedia().toURI().toString()));
                        }
                    }.start();

                    settingsController.useURLForTease.setSelected(newValue.isUseForTease());
                    settingsController.urlProgressLabel.setText("Contains " + newValue.getMediaURLs().size() + " files.");
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
        Pane pane = new Pane();
        settingsController.urlGridPane.add(pane, 0, 1);
        settingsController.urlFileImagePreview.fitWidthProperty().bind(pane.widthProperty());
        settingsController.urlFileImagePreview.fitHeightProperty().bind(pane.heightProperty());
    }

    private void updateURLList() {
        settingsController.urlFilesList.getItems().clear();
        settingsController.urlFileDragDropList.getItems().clear();

        //No images to check
        if (!TeaseAI.application.getMediaCollection().getMediaHolders().containsKey(MediaType.IMAGE)) {
            return;
        }

        for (MediaHolder mediaHolder : TeaseAI.application.getMediaCollection().getMediaHolders().get(MediaType.IMAGE)) {
            if (mediaHolder instanceof MediaURL) {
                settingsController.urlFilesList.getItems().add(mediaHolder);
                settingsController.urlFileDragDropList.getItems().add(mediaHolder);
            }
        }
    }
}