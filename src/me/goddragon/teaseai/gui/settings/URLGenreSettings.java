package me.goddragon.teaseai.gui.settings;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.scene.control.SelectionMode;
import javafx.scene.input.*;
import me.goddragon.teaseai.TeaseAI;
import me.goddragon.teaseai.api.media.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by GodDragon on 28.03.2018.
 */
public class URLGenreSettings {
    public static final DataFormat LIST_DATA_FORMAT = new DataFormat("listOfURLFiles");

    private final SettingsController settingsController;

    public URLGenreSettings(SettingsController settingsController) {
        this.settingsController = settingsController;
    }

    public void initiate() {
        settingsController.urlFileDragDropList.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        //Handle drag and drop
        settingsController.urlFileDragDropList.setOnDragDetected(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                Dragboard dragBoard = settingsController.urlFileDragDropList.startDragAndDrop(TransferMode.MOVE);

                ClipboardContent content = new ClipboardContent();

                List<String> contentList = new ArrayList<>();
                for (Object mediaURL : settingsController.urlFileDragDropList.getSelectionModel().getSelectedItems()) {
                    contentList.add(((MediaURL) mediaURL).getUrl());
                }

                content.put(LIST_DATA_FORMAT, contentList);

                dragBoard.setContent(content);
            }
        });

        settingsController.assignedURLFileList.setOnDragDropped(new EventHandler<DragEvent>() {
            @Override
            public void handle(DragEvent dragEvent) {
                List<String> urls = (List<String>) dragEvent.getDragboard().getContent(LIST_DATA_FORMAT);

                MediaFetishType mediaFetishType = settingsController.mediaSettings.getSelectedURLMediaFetish();

                for (String url : urls) {
                    MediaURL mediaURL = TeaseAI.application.getMediaCollection().getByURL(url);
                    if (mediaURL != null && !settingsController.assignedURLFileList.getItems().contains(mediaURL)) {
                        settingsController.assignedURLFileList.getItems().add(mediaURL);

                        if (mediaFetishType != null) {
                            TeaseAI.application.getMediaCollection().addMediaHolder(mediaFetishType, mediaURL);
                        }
                    }
                }

                if (!urls.isEmpty()) {
                    saveImageMediaURLs();
                }

                dragEvent.setDropCompleted(true);
            }
        });

        settingsController.assignedURLFileList.setOnDragOver(new EventHandler<DragEvent>() {
            @Override
            public void handle(DragEvent dragEvent) {
                dragEvent.acceptTransferModes(TransferMode.MOVE);
            }
        });

        //Allow removing assigned files again by pressing del
        settingsController.assignedURLFileList.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                if (event.getCode().toString().equals("DELETE")) {
                    MediaFetishType mediaFetishType = settingsController.mediaSettings.getSelectedURLMediaFetish();

                    boolean changed = false;
                    for (Object object : settingsController.assignedURLFileList.getSelectionModel().getSelectedItems()) {
                        settingsController.assignedURLFileList.getItems().remove(object);

                        if (mediaFetishType != null) {
                            TeaseAI.application.getMediaCollection().removeMediaHolder((MediaHolder) object, mediaFetishType);
                        }

                        changed = true;
                    }

                    if (changed) {
                        saveImageMediaURLs();
                    }
                }
            }
        });

        for (MediaFetishType mediaFetishType : MediaFetishType.values()) {
            settingsController.urlFetishTypeList.getItems().add(mediaFetishType);
        }

        settingsController.urlFetishTypeList.getSelectionModel().selectFirst();
        settingsController.urlFetishTypeList.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<MediaFetishType>() {
            @Override
            public void changed(ObservableValue<? extends MediaFetishType> observable, MediaFetishType oldValue, MediaFetishType newValue) {
                if (newValue != null) {
                    updateAssignedURLList();
                }
            }
        });


        settingsController.removeMediaURLButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if (settingsController.assignedURLFileList.getSelectionModel().getSelectedItems().isEmpty()) {
                    return;
                }

                MediaFetishType mediaFetishType = settingsController.mediaSettings.getSelectedURLMediaFetish();

                boolean changed = false;
                for (Object object : settingsController.assignedURLFileList.getSelectionModel().getSelectedItems()) {
                    if (!settingsController.assignedURLFileList.getItems().contains(object)) {
                        continue;
                    }

                    settingsController.assignedURLFileList.getItems().remove(object);

                    if (mediaFetishType != null) {
                        TeaseAI.application.getMediaCollection().removeMediaHolder((MediaHolder) object, mediaFetishType);
                    }

                    changed = true;
                }

                if (changed) {
                    saveImageMediaURLs();
                }
            }
        });

        settingsController.assignMediaURLButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                MediaFetishType mediaFetishType = settingsController.mediaSettings.getSelectedURLMediaFetish();

                for (Object mediaURL : settingsController.urlFileDragDropList.getSelectionModel().getSelectedItems()) {
                    if (mediaURL != null && !settingsController.assignedURLFileList.getItems().contains(mediaURL)) {
                        settingsController.assignedURLFileList.getItems().add(mediaURL);

                        if (mediaFetishType != null) {
                            TeaseAI.application.getMediaCollection().addMediaHolder(mediaFetishType, (MediaHolder) mediaURL);
                        }
                    }
                }

                if (!settingsController.urlFileDragDropList.getSelectionModel().getSelectedItems().isEmpty()) {
                    saveImageMediaURLs();
                }
            }
        });

        updateAssignedURLList();
    }

    private void saveImageMediaURLs() {
        MediaFetishType mediaFetishType = settingsController.mediaSettings.getSelectedURLMediaFetish();
        if (mediaFetishType != null) {
            TeaseAI.application.getMediaCollection().saveMediaFetishType(mediaFetishType, MediaType.IMAGE, MediaHolderType.URL);
        }
    }

    private void updateAssignedURLList() {
        settingsController.assignedURLFileList.getItems().clear();

        MediaFetishType mediaFetishType = settingsController.mediaSettings.getSelectedURLMediaFetish();

        if (mediaFetishType != null) {
            for (MediaHolder mediaHolder : TeaseAI.application.getMediaCollection().getMediaHolders(mediaFetishType, MediaType.IMAGE)) {
                if (mediaHolder instanceof MediaURL) {
                    settingsController.assignedURLFileList.getItems().add(mediaHolder);
                }
            }
        }
    }
}
