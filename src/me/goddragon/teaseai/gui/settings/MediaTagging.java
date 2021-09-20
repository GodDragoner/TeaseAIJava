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
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import me.goddragon.teaseai.TeaseAI;
import me.goddragon.teaseai.api.picture.DressState;
import me.goddragon.teaseai.api.picture.PictureHandler;
import me.goddragon.teaseai.api.picture.PictureTag;
import me.goddragon.teaseai.api.picture.PictureTag.TagType;
import me.goddragon.teaseai.api.picture.TaggedPicture;
import me.goddragon.teaseai.utils.FileUtils;
import me.goddragon.teaseai.utils.StringUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public class MediaTagging {

    private final SettingsController settingsController;

    private Parent root;

    private static FXMLLoader loader;

    private Stage imageTaggingWindow;

    private File directory;

    private TaggedPicture[] files;

    private int currentFile;

    @FXML
    private Label fileLabel;

    @FXML
    private Button previousButton;

    @FXML
    private Button nextButton;

    @FXML
    private Button setDefaultButton;

    @FXML
    private ImageView taggedImage;

    @FXML
    private BorderPane imagePane;

    @FXML
    private GridPane testGridPane;

    @FXML
    private ColumnConstraints imageGridWidth;

    @FXML
    private MenuButton bodyPartButton;

    @FXML
    private MenuButton bodyTypeButton;

    @FXML
    private MenuButton accessoriesButton;

    @FXML
    private MenuButton viewButton;

    @FXML
    private MenuButton actionButton;

    @FXML
    private MenuButton peopleInvolvedButton;

    @FXML
    private MenuButton categoryButton;

    @FXML
    private MenuButton testMenuButton;

    @FXML
    private MenuButton dressStateButton;

    @FXML
    private CheckBox bulkTagging;

    @FXML
    private CheckBox onlyUntagged;


    private HashSet<PictureTag> currentImageTags;

    private TaggedPicture currentTaggedPicture;

    private DressState currentDressState;

    private HashMap<PictureTag, CheckBox> checkBoxes = new HashMap<>();

    private List<PictureTag> defaults = new ArrayList<>();

    public MediaTagging(SettingsController settingsController) {
        this.settingsController = settingsController;
    }

    public static void create(SettingsController settingsController) {
        MediaTagging tagger = new MediaTagging(settingsController);
        loader = new FXMLLoader(TeaseAI.class.getResource("gui/settings/MediaTagger.fxml"));
        loader.setController(tagger);

        try {
            tagger.root = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }

        tagger.initiate();
    }

    public void initiate() {
        settingsController.taggingFolderButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                DirectoryChooser chooser = new DirectoryChooser();
                chooser.setTitle("Select Tagging Folder");

                String dir;
                if (settingsController.taggingFolderText.getText() != null && new File(settingsController.taggingFolderText.getText()).exists()) {
                    dir = settingsController.taggingFolderText.getText();
                } else {
                    dir = System.getProperty("user.dir");
                }

                File defaultDirectory = new File(dir);
                chooser.setInitialDirectory(defaultDirectory);
                File selectedDirectory = chooser.showDialog(settingsController.stage);

                if (selectedDirectory != null) {
                    settingsController.taggingFolderText.setText(selectedDirectory.getPath());
                    //Create new stage window
                    if (imageTaggingWindow != null) {

                    } else {
                        imageTaggingWindow = new Stage();
                        imageTaggingWindow.setTitle("Tease-AI Tagger");
                        imageTaggingWindow.setScene(new Scene(root, 1280, 650));
                    }

                    directory = selectedDirectory;

                    if (initiateNewStage()) {
                        imageTaggingWindow.show();
                    }
                }
            }
        });
    }

    public void setUpButtons() {
        currentTaggedPicture = files[currentFile];
        currentImageTags = (HashSet<PictureTag>) currentTaggedPicture.getTags().clone();
        currentDressState = currentTaggedPicture.getDressState();

        if (currentImageTags == null || currentImageTags.isEmpty()) {
            currentImageTags = new HashSet<>();

            //Add all defaults if we have no tags yet
            if(!defaults.isEmpty()) {
                currentImageTags.addAll(defaults);
            }
        }

        addTagsToButton(bodyPartButton, TagType.BODY_PART);
        addTagsToButton(bodyTypeButton, TagType.BODY_TYPE);
        addTagsToButton(categoryButton, TagType.CATEGORY);
        addTagsToButton(peopleInvolvedButton, TagType.PEOPLE_INVOLVED);
        addTagsToButton(actionButton, TagType.ACTION);
        addTagsToButton(accessoriesButton, TagType.ACCESSORIES);
        addTagsToButton(viewButton, TagType.VIEW);

        dressStateButton.getItems().clear();

        DressState[] dressStateTags = DressState.values();
        ArrayList<CheckBox> checkboxes = new ArrayList<>();

        //Update stage if we change status of only untagged
        onlyUntagged.selectedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                saveTags();

                if (newValue) {
                    initiateNewStage();
                } else {
                    initiateNewStage();
                }
            }
        });


        for (int i = 0; i < dressStateTags.length; i++) {
            CheckBox thisCheckBox = new CheckBox(StringUtils.splitCamelCase(dressStateTags[i].getTagName().replace("Tag", "")));
            checkboxes.add(thisCheckBox);

            if (currentDressState != null && currentDressState == dressStateTags[i]) {
                thisCheckBox.setSelected(true);
            }

            DressState thisTag = dressStateTags[i];
            thisCheckBox.selectedProperty().addListener(new ChangeListener<Boolean>() {
                @Override
                public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                    if (newValue) {
                        currentDressState = thisTag;
                        for (CheckBox thisBox : checkboxes) {
                            if (!thisBox.equals(thisCheckBox)) {
                                thisBox.setSelected(false);
                            }
                        }
                    } else {
                        if (currentDressState.equals(thisTag)) {
                            currentDressState = null;
                        }
                    }
                }
            });

            CustomMenuItem thisItem = new CustomMenuItem(thisCheckBox);
            thisItem.setHideOnClick(false);
            dressStateButton.getItems().add(thisItem);
        }
    }

    private void addTagsToButton(MenuButton button, TagType tagType) {
        PictureTag[] viewTags = PictureTag.getPictureTagsByType(tagType);
        if (button.getItems().isEmpty()) {
            for (int i = 0; i < viewTags.length; i++) {
                CheckBox thisCheckBox = new CheckBox(StringUtils.splitCamelCase(viewTags[i].getTagName().replace("Tag", "")));
                checkBoxes.put(viewTags[i], thisCheckBox);

                PictureTag thisTag = viewTags[i];
                thisCheckBox.selectedProperty().addListener(new ChangeListener<Boolean>() {
                    @Override
                    public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                        if (newValue) {
                            currentImageTags.add(thisTag);
                        } else {
                            currentImageTags.remove(thisTag);
                        }
                    }
                });

                CustomMenuItem thisItem = new CustomMenuItem(thisCheckBox);
                thisItem.setHideOnClick(false);
                button.getItems().add(thisItem);
            }
        }

        for (PictureTag pictureTag : viewTags) {
            checkBoxes.get(pictureTag).setSelected(currentImageTags.contains(pictureTag));
        }
    }

    public boolean initiateNewStage() {
        PictureHandler.getHandler().setDefaultFolders();
        taggedImage.setPreserveRatio(true);
        Pane pane = new Pane();
        testGridPane.add(pane, 1, 1);
        taggedImage.fitWidthProperty().bind(pane.widthProperty());
        taggedImage.fitHeightProperty().bind(pane.heightProperty());
        currentFile = 0;

        //File searchingDirectory = directory;

        File[] locFiles = directory.listFiles((file) -> (FileUtils.isSupportedPictureExtension(FileUtils.getExtension(file))));

        ArrayList<TaggedPicture> taggedList = new ArrayList<>();

        for (File thisFile : locFiles) {
            TaggedPicture thisTaggedPic = new TaggedPicture(thisFile, true);

            //Ignore if untagged is checked
            if(onlyUntagged.isSelected() && !thisTaggedPic.getTags().isEmpty()) {
                continue;
            }

            if (thisTaggedPic.getFile() != null) {
                taggedList.add(thisTaggedPic);
            }
        }

        if (taggedList.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("No images found");
            alert.setHeaderText(null);
            alert.setContentText("There were no images found in the given folder.");

            if(onlyUntagged.isSelected()) {
                onlyUntagged.setSelected(false);
            }

            alert.showAndWait();
            return false;
        }

        files = taggedList.toArray(new TaggedPicture[]{});

        fileLabel.setText("File " + (currentFile + 1) + " of " + files.length + " in " + directory.getPath());

        Image image = new Image(files[currentFile].getFile().toURI().toString());

        taggedImage.setImage(image);
        setUpButtons();

        //Store the current tags of the selected image too when closing
        imageTaggingWindow.setOnCloseRequest(event -> {
            saveTags();
        });

        nextButton.setOnMouseClicked(event -> {
            saveTags();

            if ((currentFile + 1) < files.length) {
                currentFile++;
            } else {
                currentFile = 0;
            }

            taggedImage.setImage(new Image(files[currentFile].getFile().toURI().toString()));
            setUpButtons();

            fileLabel.setText("File " + (currentFile + 1) + " of " + files.length + " in " + directory.getPath());
        });

        previousButton.setOnMouseClicked(event -> {
            saveTags();

            if ((currentFile - 1) >= 0) {
                currentFile--;
            } else {
                currentFile = files.length - 1;
            }

            taggedImage.setImage(new Image(files[currentFile].getFile().toURI().toString()));
            setUpButtons();
            fileLabel.setText("File " + (currentFile + 1) + " of " + files.length + " in " + directory.getPath());
        });


        setDefaultButton.setOnMouseClicked(event -> {
            defaults.clear();
            defaults.addAll(currentImageTags);
        });

        return true;
    }

    private void saveTags() {
        if (bulkTagging.isSelected()) {
            for (TaggedPicture taggedPicture : files) {
                taggedPicture.setDressState(currentDressState);

                HashSet<PictureTag> tags = new HashSet<>();
                tags.addAll(taggedPicture.getTags());
                tags.addAll(currentImageTags);

                taggedPicture.setTags(tags);
            }
        } else {
            currentTaggedPicture.setDressState(currentDressState);
            currentTaggedPicture.setTags(currentImageTags);
        }
    }

}