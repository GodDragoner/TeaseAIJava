package me.goddragon.teaseai.gui.settings;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;

import javafx.beans.binding.Bindings;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
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
import me.goddragon.teaseai.api.picture.PictureHandler;
import me.goddragon.teaseai.api.picture.PictureTag;
import me.goddragon.teaseai.api.picture.PictureTag.TagType;
import me.goddragon.teaseai.api.picture.TaggedPicture;
import me.goddragon.teaseai.utils.TeaseLogger;

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
    
    private HashSet<PictureTag> currentImageTags;
    
    private TaggedPicture currentTaggedPicture;

    public MediaTagging(SettingsController settingsController) {
        this.settingsController = settingsController;
    }
    public static void create(SettingsController settingsController)
    {
    	MediaTagging tagger = new MediaTagging(settingsController);
        loader = new FXMLLoader(TeaseAI.class.getResource("gui/settings/MediaTagger.fxml"));
        loader.setController(tagger);
    	try {
			tagger.root = loader.load();
		} catch (IOException e) {
			// TODO Auto-generated catch block
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

                if(selectedDirectory != null) {
                	
                    settingsController.taggingFolderText.setText(selectedDirectory.getPath());
                    //Create new stage window
                    if (imageTaggingWindow != null)
                    {

                    }
                    else
                    {
	                    imageTaggingWindow = new Stage();                        
	                    imageTaggingWindow.setTitle("Tease-AI Tagger");
	                    imageTaggingWindow.setScene(new Scene(root, 1280, 650));
                    }
                    imageTaggingWindow.show();
                    directory = selectedDirectory;
                    initiateNewStage();                  
                }
            }
        });
    }
    @SuppressWarnings("unchecked")
	public void setUpButtons()
    {
		currentTaggedPicture = files[currentFile];
		currentImageTags = (HashSet<PictureTag>) currentTaggedPicture.getTags().clone();
		    	
		if (currentImageTags == null)
		{
			currentImageTags = new HashSet<PictureTag>();
		}
		
		bodyPartButton.getItems().clear();
    	PictureTag[] bodyTags = PictureTag.getPictureTagsByType(TagType.BODYPART);
    	for (int i = 0; i < bodyTags.length; i++)
    	{
    		CheckBox thisCheckBox = new CheckBox(bodyTags[i].tagName().replace("Tag", ""));
    		if (currentImageTags.contains(bodyTags[i]))
    		{
    			thisCheckBox.setSelected(true);
    		}
    		PictureTag thisTag = bodyTags[i];
    		thisCheckBox.selectedProperty().addListener(new ChangeListener<Boolean>() {

    	        @Override
    	        public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
    	            // TODO Auto-generated method stub
    	            if(newValue)
    	            {
    	            	currentImageTags.add(thisTag);
    	            }
    	            else
    	            {
    	            	currentImageTags.remove(thisTag);
    	            }
    	        }
    	    });
    		CustomMenuItem thisItem = new CustomMenuItem(thisCheckBox);
    		thisItem.setHideOnClick(false);
    		bodyPartButton.getItems().add(thisItem);
    	}
    	
    	bodyTypeButton.getItems().clear();
    	PictureTag[] bodyTypeTags = PictureTag.getPictureTagsByType(TagType.BODYTYPE);
    	for (int i = 0; i < bodyTypeTags.length; i++)
    	{
    		CheckBox thisCheckBox = new CheckBox(bodyTypeTags[i].tagName().replace("Tag", ""));
    		if (currentImageTags.contains(bodyTypeTags[i]))
    		{
    			thisCheckBox.setSelected(true);
    		}
    		PictureTag thisTag = bodyTypeTags[i];
    		thisCheckBox.selectedProperty().addListener(new ChangeListener<Boolean>() {

    	        @Override
    	        public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
    	            // TODO Auto-generated method stub
    	            if(newValue)
    	            {
    	            	currentImageTags.add(thisTag);
    	            }
    	            else
    	            {
    	            	currentImageTags.remove(thisTag);
    	            }
    	        }
    	    });
    		CustomMenuItem thisItem = new CustomMenuItem(thisCheckBox);
    		thisItem.setHideOnClick(false);
    		bodyTypeButton.getItems().add(thisItem);
    	}
    	
    	categoryButton.getItems().clear();
    	PictureTag[] categoryTags = PictureTag.getPictureTagsByType(TagType.CATEGORY);
    	for (int i = 0; i < categoryTags.length; i++)
    	{
    		CheckBox thisCheckBox = new CheckBox(categoryTags[i].tagName().replace("Tag", ""));
    		if (currentImageTags.contains(categoryTags[i]))
    		{
    			thisCheckBox.setSelected(true);
    		}
    		PictureTag thisTag = categoryTags[i];
    		thisCheckBox.selectedProperty().addListener(new ChangeListener<Boolean>() {

    	        @Override
    	        public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
    	            // TODO Auto-generated method stub
    	            if(newValue)
    	            {
    	            	currentImageTags.add(thisTag);
    	            }
    	            else
    	            {
    	            	currentImageTags.remove(thisTag);
    	            }
    	        }
    	    });
    		CustomMenuItem thisItem = new CustomMenuItem(thisCheckBox);
    		thisItem.setHideOnClick(false);
    		categoryButton.getItems().add(thisItem);
    	}
    	
    	peopleInvolvedButton.getItems().clear();
    	PictureTag[] peopleInvolvedTags = PictureTag.getPictureTagsByType(TagType.PEOPLEINVOLVED);
    	for (int i = 0; i < peopleInvolvedTags.length; i++)
    	{
    		CheckBox thisCheckBox = new CheckBox(peopleInvolvedTags[i].tagName().replace("Tag", ""));
    		if (currentImageTags.contains(peopleInvolvedTags[i]))
    		{
    			thisCheckBox.setSelected(true);
    		}
    		PictureTag thisTag = peopleInvolvedTags[i];
    		thisCheckBox.selectedProperty().addListener(new ChangeListener<Boolean>() {

    	        @Override
    	        public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
    	            // TODO Auto-generated method stub
    	            if(newValue)
    	            {
    	            	currentImageTags.add(thisTag);
    	            }
    	            else
    	            {
    	            	currentImageTags.remove(thisTag);
    	            }
    	        }
    	    });
    		CustomMenuItem thisItem = new CustomMenuItem(thisCheckBox);
    		thisItem.setHideOnClick(false);
    		peopleInvolvedButton.getItems().add(thisItem);
    	}
    	
    	actionButton.getItems().clear();
    	PictureTag[] actionTags = PictureTag.getPictureTagsByType(TagType.ACTION);
    	for (int i = 0; i < actionTags.length; i++)
    	{
    		CheckBox thisCheckBox = new CheckBox(actionTags[i].tagName().replace("Tag", ""));
    		if (currentImageTags.contains(actionTags[i]))
    		{
    			thisCheckBox.setSelected(true);
    		}
    		PictureTag thisTag = actionTags[i];
    		thisCheckBox.selectedProperty().addListener(new ChangeListener<Boolean>() {

    	        @Override
    	        public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
    	            // TODO Auto-generated method stub
    	            if(newValue)
    	            {
    	            	currentImageTags.add(thisTag);
    	            }
    	            else
    	            {
    	            	currentImageTags.remove(thisTag);
    	            }
    	        }
    	    });
    		CustomMenuItem thisItem = new CustomMenuItem(thisCheckBox);
    		thisItem.setHideOnClick(false);
    		actionButton.getItems().add(thisItem);
    	}
    	
    	accessoriesButton.getItems().clear();
    	PictureTag[] accessoriesTags = PictureTag.getPictureTagsByType(TagType.ACCESSORIES);
    	for (int i = 0; i < accessoriesTags.length; i++)
    	{
    		CheckBox thisCheckBox = new CheckBox(accessoriesTags[i].tagName().replace("Tag", ""));
    		if (currentImageTags.contains(accessoriesTags[i]))
    		{
    			thisCheckBox.setSelected(true);
    		}
    		PictureTag thisTag = accessoriesTags[i];
    		thisCheckBox.selectedProperty().addListener(new ChangeListener<Boolean>() {

    	        @Override
    	        public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
    	            // TODO Auto-generated method stub
    	            if(newValue)
    	            {
    	            	currentImageTags.add(thisTag);
    	            }
    	            else
    	            {
    	            	currentImageTags.remove(thisTag);
    	            }
    	        }
    	    });
    		CustomMenuItem thisItem = new CustomMenuItem(thisCheckBox);
    		thisItem.setHideOnClick(false);
    		accessoriesButton.getItems().add(thisItem);
    	}
    	
    	viewButton.getItems().clear();
    	PictureTag[] viewTags = PictureTag.getPictureTagsByType(TagType.VIEW);
    	for (int i = 0; i < viewTags.length; i++)
    	{
    		CheckBox thisCheckBox = new CheckBox(viewTags[i].tagName().replace("Tag", ""));
    		if (currentImageTags.contains(viewTags[i]))
    		{
    			thisCheckBox.setSelected(true);
    		}
    		PictureTag thisTag = viewTags[i];
    		thisCheckBox.selectedProperty().addListener(new ChangeListener<Boolean>() {

    	        @Override
    	        public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
    	            // TODO Auto-generated method stub
    	            if(newValue)
    	            {
    	            	currentImageTags.add(thisTag);
    	            }
    	            else
    	            {
    	            	currentImageTags.remove(thisTag);
    	            }
    	        }
    	    });
    		CustomMenuItem thisItem = new CustomMenuItem(thisCheckBox);
    		thisItem.setHideOnClick(false);
    		viewButton.getItems().add(thisItem);
    	}
    }
    
    public void initiateNewStage()
    {
    	PictureHandler.getHandler().setDefaultFolders();
    	taggedImage.setPreserveRatio(true);
    	Pane pane = new Pane();
    	testGridPane.add(pane, 1, 1);
    	taggedImage.fitWidthProperty().bind(pane.widthProperty());
    	taggedImage.fitHeightProperty().bind(pane.heightProperty());
    	currentFile = 0;
    	File[] locFiles = directory.listFiles(new FilenameFilter() {
    		@Override
    	    public boolean accept(File dir, String name) {
    	        return (name.toLowerCase().endsWith(".jpg") || name.toLowerCase().endsWith(".png") || name.toLowerCase().endsWith(".gif"));
    	    }
    	});
    	ArrayList<TaggedPicture> taggedList = new ArrayList<TaggedPicture>();
    	for (File thisFile: locFiles)
    	{
    		TaggedPicture thisTaggedPic = new TaggedPicture(thisFile);
    		if (thisTaggedPic.getFile() != null)
    		{
    			taggedList.add(thisTaggedPic);
    		}
    	}
    	files = new TaggedPicture[taggedList.size()];
    	files = taggedList.toArray(files);
    	
    	//TeaseLogger.getLogger().log(Level.INFO, "files " + files + " fileLabel " + this.fileLabel);
    	fileLabel.setText("File " + (currentFile + 1) + " of " + files.length + " in " + directory.getPath());
    	Image image = new Image(files[currentFile].getFile().toURI().toString());
    	//TeaseLogger.getLogger().log(Level.INFO, "file " + files[currentFile] + " image " + image + " taggedimage " + taggedImage);
    	taggedImage.setImage(image);
    	setUpButtons();
    	
    	nextButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
            	currentTaggedPicture.setTags(currentImageTags);            
                if ((currentFile + 1) < files.length)
                {
                	currentFile++;
                }
                else
                {
                	currentFile = 0;
                }
            	taggedImage.setImage(new Image(files[currentFile].getFile().toURI().toString()));
            	setUpButtons();
            	fileLabel.setText("File " + (currentFile + 1) + " of " + files.length + " in " + directory.getPath());
            }
        });
    	
    	previousButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
        		currentTaggedPicture.setTags(currentImageTags);
                if ((currentFile - 1) >= 0)
                {
                	currentFile--;
                }
                else
                {
                	currentFile = files.length - 1;
                }
            	taggedImage.setImage(new Image(files[currentFile].getFile().toURI().toString()));
            	setUpButtons();
            	fileLabel.setText("File " + (currentFile + 1) + " of " + files.length + " in " + directory.getPath());
            }
        });
    	
    }
	
}
