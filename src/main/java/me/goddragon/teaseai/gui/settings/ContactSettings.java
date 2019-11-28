package me.goddragon.teaseai.gui.settings;


import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.scene.control.Alert;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import me.goddragon.teaseai.api.chat.ChatHandler;
import me.goddragon.teaseai.api.chat.ChatParticipant;
import me.goddragon.teaseai.gui.main.MainGuiController;
import me.goddragon.teaseai.utils.FileUtils;
import me.goddragon.teaseai.utils.media.ImageUtils;

import java.io.File;

/**
 * Created by GodDragon on 31.03.2018.
 */
public class ContactSettings {

    private final SettingsController settingsController;

    public ContactSettings(SettingsController settingsController) {
        this.settingsController = settingsController;
    }

    public void initiate() {
        updateContactList();
        settingsController.domContactListView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<ChatParticipant>() {
            @Override
            public void changed(ObservableValue<? extends ChatParticipant> observable, ChatParticipant oldValue, ChatParticipant newValue) {
                if (newValue != null) {
                    settingsController.domContactNameField.setDisable(false);
                    updateContactData();
                } else {
                    settingsController.domContactImageView.setImage(null);
                    settingsController.domContactNameField.setDisable(true);
                }
            }
        });

        settingsController.domContactListView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        settingsController.domContactListView.getSelectionModel().selectFirst();

        settingsController.domContactImageView.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                FileChooser chooser = new FileChooser();
                chooser.setTitle("Select Picture");

                String dir;
                if (new File(getSelectedContact().getContact().IMAGE_PATH.getValue()).exists()) {
                    dir = getSelectedContact().getContact().IMAGE_PATH.getValue();
                    //Get parent folder
                    dir = dir.substring(0, dir.lastIndexOf(File.separator));
                } else {
                    dir = System.getProperty("user.dir");
                }

                File defaultDirectory = new File(dir);
                chooser.setInitialDirectory(defaultDirectory);

                chooser.getExtensionFilters().addAll(
                        new FileChooser.ExtensionFilter("JPG", "*.jpg"),
                        new FileChooser.ExtensionFilter("PNG", "*.png")
                );

                File image = chooser.showOpenDialog(settingsController.stage);

                if (image != null && image.exists()) {
                    String extension = FileUtils.getExtension(image);
                    if ((extension.equalsIgnoreCase("jpg") || extension.equalsIgnoreCase("png") || extension.equalsIgnoreCase("jpeg"))) {
                        getSelectedContact().getContact().IMAGE_PATH.setValue(image.getPath());
                        getSelectedContact().getContact().IMAGE_PATH.save();
                        getSelectedContact().getContact().save();
                        updateContactData();
                    } else {
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setTitle("Invalid File");
                        alert.setHeaderText(null);
                        alert.setContentText("The given file is not a supported image file.");

                        alert.showAndWait();
                    }
                }
            }
        });

        settingsController.domContactNameField.textProperty().addListener((observable, oldValue, newValue) -> {
            ChatParticipant contact = getSelectedContact();

            if (contact != null) {
                contact.getContact().NAME.setValue(newValue);

                contact.setName(contact.getContact().NAME.getValue());
                updateContactList();
                if (contact.getId() == 1) {
                    MainGuiController.getController().getDomNameTextField().setText(contact.getContact().NAME.getValue());
                }
            }
        });

        settingsController.domContactImageView.setPreserveRatio(true);
        Pane pane = new Pane();
        pane.setVisible(false);
        settingsController.contactGridPane.add(pane, 0, 0);
        settingsController.domContactImageView.fitWidthProperty().bind(pane.widthProperty());
        settingsController.domContactImageView.fitHeightProperty().bind(pane.heightProperty());

        settingsController.domContactImageSetPathButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                DirectoryChooser chooser = new DirectoryChooser();
                chooser.setTitle("Select Image Set Folder");

                String dir;
                if (settingsController.domContactImageSetPathText.getText() != null && new File(settingsController.domContactImageSetPathText.getText()).exists()) {
                    dir = settingsController.domContactImageSetPathText.getText();
                } else {
                    dir = System.getProperty("user.dir");
                }

                File defaultDirectory = new File(dir);
                chooser.setInitialDirectory(defaultDirectory);
                File selectedDirectory = chooser.showDialog(settingsController.stage);

                if (selectedDirectory != null) {
                    settingsController.domContactImageSetPathText.setText(selectedDirectory.getPath());
                    getSelectedContact().getContact().IMAGE_SET_PATH.setValue(selectedDirectory.getPath());
                    getSelectedContact().getContact().IMAGE_SET_PATH.save();

                    //Update the picture set
                    getSelectedContact().choosePictureSet();

                    updateContactData();
                }
            }
        });
    }

    public void updateContactData() {
        ChatParticipant participant = getSelectedContact();

        if (participant != null) {
            File image = participant.getContact().getImage();

            if (image != null && image.exists() && image.isFile() && (image.getName().toLowerCase().endsWith(".jpg") || image.getName().toLowerCase().endsWith(".png") || image.getName().toLowerCase().endsWith(".jpeg"))) {
                ImageUtils.setImageInView(image, settingsController.domContactImageView);
                settingsController.clickContactAvatarText.setText("");
            } else {
                settingsController.domContactImageView.setImage(null);
                settingsController.clickContactAvatarText.setText("Click here to set the contact avatar");
            }

            settingsController.domContactNameField.setText(participant.getContact().NAME.getValue());

            if (participant.getContact().getImageFolder() != null && participant.getContact().getImageFolder().exists()) {
                settingsController.domContactImageSetPathText.setText(participant.getContact().getImageFolder().getPath());
            } else {
                settingsController.domContactImageSetPathText.setText("");
            }
        }
    }

    public void updateContactList() {
        int currentSelected = settingsController.domContactListView.getSelectionModel().getSelectedIndex();
        ListView listView = settingsController.domContactListView;
        listView.getItems().clear();

        for (ChatParticipant chatParticipant : ChatHandler.getHandler().getParticipants()) {
            if (chatParticipant.getId() >= 1 && chatParticipant.getContact() != null) {
                listView.getItems().add(chatParticipant);
            }
        }

        settingsController.domContactListView.getSelectionModel().select(currentSelected);
    }

    private ChatParticipant getSelectedContact() {
        if (settingsController.domContactListView.getSelectionModel().getSelectedItems().size() == 1) {
            ChatParticipant participant = (ChatParticipant) settingsController.domContactListView.getSelectionModel().getSelectedItem();
            return participant;
        }

        return null;
    }
}