package me.goddragon.teaseai.gui.settings;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.*;
import javafx.stage.Stage;
import me.goddragon.teaseai.TeaseAI;
import me.goddragon.teaseai.api.media.*;

import javax.swing.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by GodDragon on 27.03.2018.
 */
public class SettingsController {

    public static final DataFormat LIST_DATA_FORMAT = new DataFormat("listOfURLFiles");

    private static SettingsController controller;

    @FXML
    private ListView urlFilesList;

    @FXML
    private TextField addURLTextField;

    @FXML
    private Button addURLButton;

    @FXML
    private Button deleteURLButton;

    @FXML
    private ImageView urlFileImagePreview;

    @FXML
    private CheckBox useURLForTease;

    @FXML
    private ListView urlFileDragDropList;

    @FXML
    private ListView assignedURLFileList;

    @FXML
    private ListView mediaFetishTypeList;


    public void initiate() {
        updateURLList();

        urlFileDragDropList.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        urlFilesList.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        addURLButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                String url = addURLTextField.getText();

                if (url != null && url.contains("tumblr.com")) {
                    addURLButton.setDisable(true);
                    addURLTextField.setDisable(true);

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
                                    addURLButton.setDisable(false);
                                    addURLTextField.setDisable(false);
                                }
                            });
                        }
                    }.start();
                } else {
                    JOptionPane.showMessageDialog(null, "URL given is not valid. Please only use tumblr urls.", "Invalid Operation", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        //Set it as disabled by default because nothing is selected
        deleteURLButton.setDisable(true);

        //Delete the url on click
        deleteURLButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                for (Object object : urlFilesList.getSelectionModel().getSelectedItems()) {
                    MediaURL mediaURL = (MediaURL) object;
                    mediaURL.deleteFile();
                    TeaseAI.application.getMediaCollection().removeMediaHolder(mediaURL);
                }

                urlFilesList.getSelectionModel().clearSelection();

                updateURLList();
            }
        });

        urlFilesList.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<MediaURL>() {
            @Override
            public void changed(ObservableValue<? extends MediaURL> observable, MediaURL oldValue, MediaURL newValue) {
                if (newValue != null) {
                    useURLForTease.setDisable(false);
                    deleteURLButton.setDisable(false);
                } else {
                    useURLForTease.setDisable(true);
                    deleteURLButton.setDisable(true);
                }

                if (urlFilesList.getSelectionModel().getSelectedItems().size() == 1) {
                    //Fetch the image on a secondary thread
                    new Thread() {
                        @Override
                        public void run() {
                            urlFileImagePreview.setImage(new Image(((MediaURL) urlFilesList.getSelectionModel().getSelectedItem()).getRandomMedia().toURI().toString()));
                        }
                    }.start();

                    useURLForTease.setSelected(newValue.isUseForTease());
                } else {
                    urlFileImagePreview.setImage(null);
                }
            }
        });

        useURLForTease.selectedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                for (Object object : urlFilesList.getSelectionModel().getSelectedItems()) {
                    MediaURL mediaURL = (MediaURL) object;
                    mediaURL.setUseForTease(newValue);
                    mediaURL.saveToFile();
                }
            }
        });

        //Handle drag and drop

        urlFileDragDropList.setOnDragDetected(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                Dragboard dragBoard = urlFileDragDropList.startDragAndDrop(TransferMode.MOVE);

                ClipboardContent content = new ClipboardContent();

                List<String> contentList = new ArrayList<>();
                for (Object mediaURL : urlFileDragDropList.getSelectionModel().getSelectedItems()) {
                    contentList.add(((MediaURL) mediaURL).getUrl());
                }

                content.put(LIST_DATA_FORMAT, contentList);

                dragBoard.setContent(content);
            }
        });

        assignedURLFileList.setOnDragDropped(new EventHandler<DragEvent>() {
            @Override
            public void handle(DragEvent dragEvent) {
                List<String> urls = (List<String>) dragEvent.getDragboard().getContent(LIST_DATA_FORMAT);

                MediaFetishType mediaFetishType = getSelectedMediaFetish();

                for (String url : urls) {
                    MediaURL mediaURL = TeaseAI.application.getMediaCollection().getByURL(url);
                    if (mediaURL != null && !assignedURLFileList.getItems().contains(mediaURL)) {
                        assignedURLFileList.getItems().add(mediaURL);

                        if (mediaFetishType != null) {
                            TeaseAI.application.getMediaCollection().addMediaHolder(mediaFetishType, mediaURL);
                        }

                        saveImageMediaURLs();
                    }
                }

                dragEvent.setDropCompleted(true);
            }
        });

        /*assignedURLFileList.setOnDragEntered(new EventHandler<DragEvent>() {
            @Override
            public void handle(DragEvent dragEvent) {
                assignedURLFileList.setBlendMode(BlendMode.DIFFERENCE);
            }
        });

        assignedURLFileList.setOnDragExited(new EventHandler<DragEvent>() {
            @Override
            public void handle(DragEvent dragEvent) {
                assignedURLFileList.setBlendMode(null);
            }
        });*/

        assignedURLFileList.setOnDragOver(new EventHandler<DragEvent>() {
            @Override
            public void handle(DragEvent dragEvent) {
                dragEvent.acceptTransferModes(TransferMode.MOVE);
            }
        });

        //Allow removing assigned files again by pressing del
        assignedURLFileList.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                if (event.getCode().toString().equals("DELETE")) {
                    MediaFetishType mediaFetishType = getSelectedMediaFetish();

                    for (Object object : assignedURLFileList.getSelectionModel().getSelectedItems()) {
                        assignedURLFileList.getItems().remove(object);

                        if (mediaFetishType != null) {
                            TeaseAI.application.getMediaCollection().removeMediaHolder((MediaHolder) object, mediaFetishType);
                        }

                        saveImageMediaURLs();
                    }
                }
            }
        });

        for (MediaFetishType mediaFetishType : MediaFetishType.values()) {
            mediaFetishTypeList.getItems().add(mediaFetishType);
        }

        mediaFetishTypeList.getSelectionModel().selectFirst();
        mediaFetishTypeList.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<MediaFetishType>() {
            @Override
            public void changed(ObservableValue<? extends MediaFetishType> observable, MediaFetishType oldValue, MediaFetishType newValue) {
                if(newValue != null) {
                    updateAssignedURLList();
                }
            }
        });

        updateAssignedURLList();
    }

    private void saveImageMediaURLs() {
        MediaFetishType mediaFetishType = getSelectedMediaFetish();
        if (mediaFetishType != null) {
            TeaseAI.application.getMediaCollection().saveMediaFetishType(mediaFetishType, MediaType.IMAGE, MediaHolderType.URL);
        }
    }

    private MediaFetishType getSelectedMediaFetish() {
        if (mediaFetishTypeList.getSelectionModel().getSelectedItems().size() == 1) {
            MediaFetishType mediaFetishType = (MediaFetishType) mediaFetishTypeList.getSelectionModel().getSelectedItem();
            return mediaFetishType;
        }

        return null;
    }

    private void updateAssignedURLList() {
        assignedURLFileList.getItems().clear();

        MediaFetishType mediaFetishType = getSelectedMediaFetish();

        if (mediaFetishType != null) {
            for (MediaHolder mediaHolder : TeaseAI.application.getMediaCollection().getMediaHolders(mediaFetishType, MediaType.IMAGE)) {
                if (mediaHolder instanceof MediaURL) {
                    assignedURLFileList.getItems().add(mediaHolder);
                }
            }
        }
    }

    private void updateURLList() {
        urlFilesList.getItems().clear();
        urlFileDragDropList.getItems().clear();

        for (MediaHolder mediaHolder : TeaseAI.application.getMediaCollection().getMediaHolders().get(MediaType.IMAGE)) {
            if (mediaHolder instanceof MediaURL) {
                urlFilesList.getItems().add(mediaHolder);
                urlFileDragDropList.getItems().add(mediaHolder);
            }
        }
    }

    public static void openGUI() {
        controller = new SettingsController();
        FXMLLoader loader = new FXMLLoader(TeaseAI.class.getResource("gui/settings/settings.fxml"));
        loader.setController(SettingsController.getController());

        try {
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Tease-AI Settings");
            stage.setScene(new Scene(root));
            stage.show();

            controller.initiate();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static SettingsController getController() {
        return controller;
    }

    public static void setController(SettingsController controller) {
        SettingsController.controller = controller;
    }
}
