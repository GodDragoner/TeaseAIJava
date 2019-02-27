package me.goddragon.teaseai.gui.settings;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import me.goddragon.teaseai.TeaseAI;
import me.goddragon.teaseai.api.chat.TypeSpeed;
import me.goddragon.teaseai.api.config.PersonalitiesSettingsHandler;
import me.goddragon.teaseai.api.scripts.personality.Personality;
import me.goddragon.teaseai.api.scripts.personality.PersonalityManager;
import me.goddragon.teaseai.gui.main.MainGuiController;

import java.io.IOException;


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
    protected TextField preferredTeaseLengthField;

    @FXML
    protected ComboBox<Double> fontSizeComboBox;

    @FXML
    protected ComboBox<TypeSpeed> defaultTypeSpeedComboBox;

    @FXML
    protected ListView urlFilesList;

    @FXML
    protected TextField addURLTextField;

    @FXML
    protected Button addURLButton;

    @FXML
    protected Button deleteURLButton;

    @FXML
    protected Button refreshURLButton;

    @FXML
    protected Label urlProgressLabel;

    @FXML
    protected StackPane urlImageViewStackPane;

    @FXML
    protected ImageView urlFileImagePreview;

    @FXML
    protected CheckBox useURLForTease;

    @FXML
    protected ListView urlFileDragDropList;

    @FXML
    protected ListView assignedURLFileList;

    @FXML
    protected ListView urlFetishTypeList;

    @FXML
    protected GridPane moveMediaURLGridPane;

    @FXML
    protected Button assignMediaURLButton;

    @FXML
    protected Button removeMediaURLButton;

    //Media Files and Folders
    @FXML
    protected ListView mediaFetishTypeList;

    @FXML
    protected Button addImagePathButton;

    @FXML
    protected TextField addImagePathTextBox;

    @FXML
    protected Button addImagePathFileChooserButton;

    @FXML
    protected ListView imagePathListView;

    @FXML
    protected Button addVideoPathButton;

    @FXML
    protected TextField addVideoPathTextBox;

    @FXML
    protected Button addVideoPathFileChooserButton;

    @FXML
    protected ListView videoPathListView;

    //Contacts
    @FXML
    protected ListView domContactListView;

    @FXML
    protected ImageView domContactImageView;

    @FXML
    protected TextField domContactNameField;

    @FXML
    protected StackPane domContactImageStackPane;

    @FXML
    protected TextField domContactImageSetPathText;

    @FXML
    protected Button domContactImageSetPathButton;

    @FXML
    protected Label clickContactAvatarText;

    //Debug
    @FXML
    protected ListView variableListView;

    @FXML
    protected TextField variableValueTextField;

    @FXML
    protected CheckBox onlySupportedVariablesCheckbox;

    //Tagging
    @FXML
    protected TextField taggingFolderText;

    @FXML
    protected Button taggingFolderButton;

    @FXML
    protected Label descriptionLabel;

    @FXML
    protected GridPane urlGridPane;

    @FXML
    protected GridPane contactGridPane;
    
    @FXML
    protected TabPane PersonalitiesPane;
    
    @FXML
    protected ComboBox<String> ThemesList;
    
    @FXML
    protected ColorPicker PrimaryColor;
    
    @FXML
    protected ColorPicker ChatWindowColor;
    
    @FXML
    protected ColorPicker ChatColor;
    
    @FXML
    protected ColorPicker ChatBackground;
    
    @FXML
    protected ColorPicker DateColor;
    
    @FXML
    protected ColorPicker SubColor;
    
    @FXML
    protected ColorPicker DomColor;
    
    @FXML
    protected ColorPicker Friend1Color;
    
    @FXML
    protected ColorPicker Friend2Color;
    
    @FXML
    protected ColorPicker Friend3Color;
    
    @FXML
    protected TabPane SettingsPanes;
    
    @FXML
    protected AnchorPane SettingsBackground;
    
    @FXML
    protected AnchorPane GeneralTab;
    
    @FXML
    protected AnchorPane AppearanceTab;
    
    @FXML
    protected AnchorPane PersonalityTab;
    
    @FXML
    protected AnchorPane MediaTab;
    
    @FXML
    protected AnchorPane ContactsTab;

    public void initiate() {
        mediaSettings.initiate();
        contactSettings.initiate();
        generalSettings.initiate();
        debugSettings.initiate();
        for (Tab tab: PersonalitiesSettingsHandler.getHandler().getTabsToAdd())
        {
            PersonalitiesPane.getTabs().add(0, tab);
        }
        appearanceSettings.initiate();
    }

    public static void openGUI() {
        if (controller != null)
        {
            if (controller.stage.isShowing())
            {
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
            newScene.getStylesheets().add(SettingsController.class.getResource("/textFormat.css").toExternalForm());
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
    
    public AppearanceSettings getAppearanceSettings()
    {
        return appearanceSettings;
    }
}
