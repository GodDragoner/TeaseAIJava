package me.goddragon.teaseai.gui.settings;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import me.goddragon.teaseai.TeaseAI;
import me.goddragon.teaseai.api.chat.TypeSpeed;
import me.goddragon.teaseai.api.config.PersonalitiesSettingsHandler;
import me.goddragon.teaseai.gui.main.MainGuiController;

import java.io.IOException;

import com.sun.tools.javac.comp.Check;


/**
 * Created by GodDragon on 27.03.2018.
 */
public class SettingsController {
    private static SettingsController controller;

    protected MediaSettings mediaSettings = new MediaSettings(this);
    protected ContactSettings contactSettings = new ContactSettings(this);
    protected GeneralSettings generalSettings = new GeneralSettings(this);
    protected PersonalitySettings debugSettings = new PersonalitySettings(this);
    protected AppearanceSettings appearanceSettings = new AppearanceSettings(this, MainGuiController.getController());

    protected Stage stage;

    //General
    @FXML
    public TextField preferredTeaseLengthField;

    @FXML
    public ComboBox<Double> fontSizeComboBox;

    @FXML
    public ComboBox<TypeSpeed> defaultTypeSpeedComboBox;

    @FXML
    public ListView urlFilesList;

    @FXML
    public TextField addURLTextField;

    @FXML
    public Button addURLButton;

    @FXML
    public Button deleteURLButton;

    @FXML
    public Button refreshURLButton;

    @FXML
    public Label urlProgressLabel;

    @FXML
    public StackPane urlImageViewStackPane;

    @FXML
    public ImageView urlFileImagePreview;

    @FXML
    public CheckBox useURLForTease;

    @FXML
    public ListView urlFileDragDropList;

    @FXML
    public ListView assignedURLFileList;

    @FXML
    public ListView urlFetishTypeList;

    @FXML
    public GridPane moveMediaURLGridPane;

    @FXML
    public Button assignMediaURLButton;

    @FXML
    public Button removeMediaURLButton;

    //Media Files and Folders
    @FXML
    public ListView mediaFetishTypeList;

    @FXML
    public Button addImagePathButton;

    @FXML
    public TextField addImagePathTextBox;

    @FXML
    public Button addImagePathFileChooserButton;

    @FXML
    public ListView imagePathListView;

    @FXML
    public Button addVideoPathButton;

    @FXML
    public TextField addVideoPathTextBox;

    @FXML
    public Button addVideoPathFileChooserButton;

    @FXML
    public ListView videoPathListView;

    //Contacts
    @FXML
    public ListView domContactListView;

    @FXML
    public ImageView domContactImageView;

    @FXML
    public TextField domContactNameField;

    @FXML
    public StackPane domContactImageStackPane;

    @FXML
    public TextField domContactImageSetPathText;

    @FXML
    public Button domContactImageSetPathButton;

    @FXML
    public Label clickContactAvatarText;

    //Debug
    @FXML
    public ListView variableListView;

    @FXML
    public TextField variableValueTextField;

    @FXML
    public CheckBox onlySupportedVariablesCheckbox;

    //Tagging
    @FXML
    public TextField taggingFolderText;

    @FXML
    public Button taggingFolderButton;

    @FXML
    public Label descriptionLabel;

    @FXML
    public GridPane urlGridPane;

    @FXML
    public GridPane contactGridPane;

    @FXML
    public TabPane PersonalitiesPane;

    @FXML
    public TabPane SettingsPanes;

    @FXML
    public AnchorPane SettingsBackground;

    @FXML
    public AnchorPane GeneralTab;

    @FXML
    public AnchorPane AppearanceTab;

    @FXML
    public AnchorPane PersonalityTab;

    @FXML
    public AnchorPane MediaTab;

    @FXML
    public AnchorPane ContactsTab;

    //Appearance
    @FXML
    public ComboBox selectedThemeComboBox;

    @FXML
    public GridPane appearanceMainGridPane;

    @FXML
    public Button saveThemeButton;

    @FXML
    public Button setThemeNameButton;

    @FXML
    public Button newThemeButton;

    @FXML
    public Button deleteThemeButton;

    @FXML
    public Button updateGUIButton;
    
    @FXML
    public ComboBox<String> textToSpeechComboBox;
    
    @FXML
    public CheckBox debugCheckbox;

    public void initiate() {
        mediaSettings.initiate();
        contactSettings.initiate();
        generalSettings.initiate();
        debugSettings.initiate();

        for (Tab tab : PersonalitiesSettingsHandler.getHandler().getTabsToAdd()) {
            PersonalitiesPane.getTabs().add(0, tab);
        }

        appearanceSettings.initiate();

        this.stage.getIcons().add(new Image(MainGuiController.class.getResourceAsStream("/TAJSYSLOGO.png")));
    }

    public static void openGUI() {
        if (controller != null) {
            if (controller.stage.isShowing()) {
                controller.stage.toFront();
                return;
            }
        }

        controller = new SettingsController();
        FXMLLoader loader = new FXMLLoader(TeaseAI.class.getResource("gui/settings/settings.fxml"));
        loader.setController(SettingsController.getController());

        try {
            Parent root = loader.load();
            controller.stage = new Stage();
            controller.stage.setTitle("Tease-AI Settings");
            Scene newScene = new Scene(root, 1280, 650);

            //So we can apply our themes to all relevant GUIs at once
            MainGuiController.getController().addMainScene(newScene);

            controller.stage.setScene(newScene);
            controller.stage.show();

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

    public AppearanceSettings getAppearanceSettings() {
        return appearanceSettings;
    }
}
