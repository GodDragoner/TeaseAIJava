package me.goddragon.teaseai.gui.settings;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.scene.control.Alert;
import javafx.scene.control.SelectionMode;
import javafx.scene.input.DragEvent;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.stage.DirectoryChooser;
import me.goddragon.teaseai.TeaseAI;
import me.goddragon.teaseai.api.media.MediaFetishType;
import me.goddragon.teaseai.api.media.MediaFolder;
import me.goddragon.teaseai.api.media.MediaHolder;
import me.goddragon.teaseai.api.media.MediaType;

import java.io.File;
import java.util.List;

/**
 * Created by GodDragon on 28.03.2018.
 */
public class GenreMediaSettings {

    private final SettingsController settingsController;

    public GenreMediaSettings(SettingsController settingsController) {
        this.settingsController = settingsController;
    }

    public void initiate() {
        //Media stuff
        for (MediaFetishType mediaFetishType : MediaFetishType.values()) {
            settingsController.mediaFetishTypeList.getItems().add(mediaFetishType);
        }

        settingsController.mediaFetishTypeList.getSelectionModel().selectFirst();
        settingsController.mediaFetishTypeList.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<MediaFetishType>() {
            @Override
            public void changed(ObservableValue<? extends MediaFetishType> observable, MediaFetishType oldValue, MediaFetishType newValue) {
                if (newValue != null) {
                    updateAssignedPathsList();
                }
            }
        });

        //Images
        settingsController.addImagePathButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                MediaFetishType mediaFetishType = getSelectedMediaFetish();
                if (mediaFetishType == null) {
                    return;
                }

                String path = settingsController.addImagePathTextBox.getText();
                File file = new File(path);

                addMediaFolder(file, MediaType.IMAGE);
            }
        });

        settingsController.addImagePathFileChooserButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                DirectoryChooser chooser = new DirectoryChooser();
                chooser.setTitle("Select Media Folder");

                String dir;
                if (settingsController.addImagePathTextBox.getText() != null && new File(settingsController.addImagePathTextBox.getText()).exists()) {
                    dir = settingsController.addImagePathTextBox.getText();
                } else {
                    dir = System.getProperty("user.dir");
                }

                File defaultDirectory = new File(dir);
                chooser.setInitialDirectory(defaultDirectory);

                if (defaultDirectory != null) {
                    File selectedDirectory = chooser.showDialog(settingsController.stage);
                    settingsController.addImagePathTextBox.setText(selectedDirectory.getPath());
                }
            }
        });

        settingsController.imagePathListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        settingsController.imagePathListView.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                if (event.getCode().toString().equals("DELETE")) {
                    MediaFetishType mediaFetishType = getSelectedMediaFetish();

                    for (Object object : settingsController.imagePathListView.getSelectionModel().getSelectedItems()) {
                        settingsController.imagePathListView.getItems().remove(object);

                        if (mediaFetishType != null) {
                            TeaseAI.application.getMediaCollection().removeMediaHolder((MediaHolder) object, mediaFetishType);
                        }
                    }

                    settingsController.mediaSettings.saveMediaPaths(MediaType.IMAGE, mediaFetishType);
                }
            }
        });

        settingsController.imagePathListView.setOnDragDropped(new EventHandler<DragEvent>() {
            @Override
            public void handle(DragEvent dragEvent) {
                List<File> files = dragEvent.getDragboard().getFiles();
                if (files.isEmpty()) {
                    return;
                }

                boolean foundFile = false;
                for (File file : files) {
                    if (file.isDirectory()) {
                        foundFile = true;
                        addMediaFolder(file, MediaType.IMAGE);
                    }
                }

                if (foundFile) {
                    dragEvent.setDropCompleted(true);
                }
            }
        });

        settingsController.imagePathListView.setOnDragOver(new EventHandler<DragEvent>() {
            @Override
            public void handle(DragEvent dragEvent) {
                List<File> files = dragEvent.getDragboard().getFiles();
                if (files.isEmpty()) {
                    return;
                }

                boolean foundFile = false;
                for (File file : files) {
                    if (file.isDirectory()) {
                        foundFile = true;
                    }
                }

                if (foundFile) {
                    dragEvent.acceptTransferModes(TransferMode.MOVE);
                }
            }
        });

        //Video
        settingsController.addVideoPathButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                MediaFetishType mediaFetishType = getSelectedMediaFetish();
                if (mediaFetishType == null) {
                    return;
                }

                String path = settingsController.addVideoPathTextBox.getText();
                File file = new File(path);

                addMediaFolder(file, MediaType.VIDEO);
            }
        });

        settingsController.addVideoPathFileChooserButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                DirectoryChooser chooser = new DirectoryChooser();
                chooser.setTitle("Select Media Folder");

                String dir;
                if (settingsController.addVideoPathTextBox.getText() != null && new File(settingsController.addVideoPathTextBox.getText()).exists()) {
                    dir = settingsController.addVideoPathTextBox.getText();
                } else {
                    dir = System.getProperty("user.dir");
                }

                File defaultDirectory = new File(dir);
                chooser.setInitialDirectory(defaultDirectory);

                if (defaultDirectory != null) {
                    File selectedDirectory = chooser.showDialog(settingsController.stage);
                    settingsController.addVideoPathTextBox.setText(selectedDirectory.getPath());
                }
            }
        });

        settingsController.videoPathListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        settingsController.videoPathListView.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                if (event.getCode().toString().equals("DELETE")) {
                    MediaFetishType mediaFetishType = getSelectedMediaFetish();

                    for (Object object : settingsController.videoPathListView.getSelectionModel().getSelectedItems()) {
                        settingsController.videoPathListView.getItems().remove(object);

                        if (mediaFetishType != null) {
                            TeaseAI.application.getMediaCollection().removeMediaHolder((MediaHolder) object, mediaFetishType);
                        }
                    }

                    settingsController.mediaSettings.saveMediaPaths(MediaType.IMAGE, mediaFetishType);
                }
            }
        });

        settingsController.videoPathListView.setOnDragOver(new EventHandler<DragEvent>() {
            @Override
            public void handle(DragEvent dragEvent) {
                List<File> files = dragEvent.getDragboard().getFiles();
                if (files.isEmpty()) {
                    return;
                }

                boolean foundFile = false;
                for (File file : files) {
                    if (file.isDirectory()) {
                        foundFile = true;
                    }
                }

                if (foundFile) {
                    dragEvent.acceptTransferModes(TransferMode.MOVE);
                }
            }
        });

        settingsController.videoPathListView.setOnDragDropped(new EventHandler<DragEvent>() {
            @Override
            public void handle(DragEvent dragEvent) {
                List<File> files = dragEvent.getDragboard().getFiles();
                if (files.isEmpty()) {
                    return;
                }

                boolean foundFile = false;
                for (File file : files) {
                    if (file.isDirectory()) {
                        foundFile = true;
                        addMediaFolder(file, MediaType.VIDEO);
                    }
                }

                if (foundFile) {
                    dragEvent.setDropCompleted(true);
                }
            }
        });


        updateAssignedPathsList();
    }


    private void addMediaFolder(File file, MediaType mediaType) {
        MediaFetishType mediaFetishType = getSelectedMediaFetish();
        String path = file.getPath();

        if (path != null && file.exists() && file.isDirectory()) {
            if (TeaseAI.application.getMediaCollection().isPathAssigned(file.getPath(), mediaFetishType, mediaType)) {
                return;
            }

            MediaFolder mediaFolder = new MediaFolder(mediaType, file);
            TeaseAI.application.getMediaCollection().addMediaHolder(mediaFetishType, mediaFolder);

            if (mediaType == MediaType.IMAGE) {
                settingsController.imagePathListView.getItems().add(mediaFolder);
            } else {
                settingsController.videoPathListView.getItems().add(mediaFolder);
            }

            settingsController.mediaSettings.saveMediaPaths(mediaType, mediaFetishType);
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Invalid Path");
            alert.setHeaderText(null);
            alert.setContentText("The given path does either not exist or is no valid folder.");

            alert.showAndWait();
        }
    }


    private MediaFetishType getSelectedMediaFetish() {
        if (settingsController.mediaFetishTypeList.getSelectionModel().getSelectedItems().size() == 1) {
            MediaFetishType mediaFetishType = (MediaFetishType) settingsController.mediaFetishTypeList.getSelectionModel().getSelectedItem();
            return mediaFetishType;
        }

        return null;
    }

    private void updateAssignedPathsList() {
        settingsController.imagePathListView.getItems().clear();
        settingsController.videoPathListView.getItems().clear();

        MediaFetishType mediaFetishType = getSelectedMediaFetish();

        if (mediaFetishType != null) {
            for (MediaType mediaType : MediaType.values()) {
                for (MediaHolder mediaHolder : TeaseAI.application.getMediaCollection().getMediaHolders(mediaFetishType, mediaType)) {
                    if (mediaHolder instanceof MediaFolder) {
                        switch (mediaType) {
                            case VIDEO:
                                settingsController.videoPathListView.getItems().add(mediaHolder);
                                break;
                            case IMAGE:
                                settingsController.imagePathListView.getItems().add(mediaHolder);
                                break;
                        }
                    }
                }
            }
        }
    }
}
