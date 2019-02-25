package me.goddragon.teaseai.gui.main;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.media.MediaView;
import javafx.scene.paint.Color;
import javafx.scene.text.TextFlow;
import javafx.stage.FileChooser;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import me.goddragon.teaseai.Main;
import me.goddragon.teaseai.TeaseAI;
import me.goddragon.teaseai.api.chat.ChatHandler;
import me.goddragon.teaseai.api.chat.ChatParticipant;
import me.goddragon.teaseai.api.scripts.personality.Personality;
import me.goddragon.teaseai.api.scripts.personality.PersonalityManager;
import me.goddragon.teaseai.gui.settings.AppearanceSettings;
import me.goddragon.teaseai.gui.settings.SettingsController;
import me.goddragon.teaseai.utils.FileUtils;
import me.goddragon.teaseai.utils.media.ImageUtils;

import java.io.File;

import com.sun.tools.classfile.Opcode.Set;

public class MainGuiController {

    private static MainGuiController thisController;
    private final Stage stage;
    private LazySubController lazySubController;
    
    @FXML
    public AnchorPane baseAnchorPane;
    
    @FXML
    public AnchorPane chatBackground;
    
    @FXML
    public StackPane chatPane;
    
    @FXML
    public SplitPane chatSplitPane;
    
    @FXML
    public AnchorPane leftWidgetBar;
    
    @FXML
    public AnchorPane rightWidgetBar;
    
    @FXML
    private MediaView mediaView;

    @FXML
    private ImageView imageView;

    @FXML
    private StackPane mediaViewBox;

    @FXML
    public TextFlow chatWindow;

    @FXML
    private TextField chatTextField;

    @FXML
    public ScrollPane chatScrollPane;

    @FXML
    private ChoiceBox personalityChoiceBox;

    @FXML
    private Button startChatButton;

    @FXML
    private Menu menuSettingsButton;

    @FXML
    private TextField subNameTextField;

    @FXML
    private TextField domNameTextField;

    @FXML
    private ImageView domImageView;

    @FXML
    private StackPane domImageViewStackPane;

    //Sidebar
    @FXML
    private FlowPane lazySubPane;

    @FXML
    private GridPane contactImageGrid;

    //Run Script Menu Item
    @FXML
    private MenuItem runScriptMenuItem;
    
    @FXML
    private Region draggableRegion;
    
    @FXML
    private Button minimize;
    
    @FXML
    private Button expand;
    
    @FXML
    private Button close;
    
    private double initialX;
    
    private double initialY;
    
    private static final Rectangle2D SCREEN_BOUNDS = Screen.getPrimary()
            .getVisualBounds();
    
    private static double prefWidth;
    
    private static double prefHeight;
    
    private boolean isMaximized = false;

    public MainGuiController(Stage stage) {
        thisController = this;
        this.stage = stage;
        this.stage.initStyle(StageStyle.UNDECORATED);
        this.stage.getIcons().add(new Image(MainGuiController.class.getResourceAsStream("/TAJSYSLOGO.png")));
    }
    

    public void initiate() {
        prefWidth = this.stage.getWidth();
        prefHeight = this.stage.getHeight();
        draggableRegion.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override public void handle(MouseEvent mouseEvent) {
              // record a delta distance for the drag and drop operation.
                initialX = stage.getX() - mouseEvent.getScreenX();
                initialY = stage.getY() - mouseEvent.getScreenY();
            }
          });
        draggableRegion.setOnMouseDragged(new EventHandler<MouseEvent>() {
            @Override public void handle(MouseEvent mouseEvent) {
                if (!isMaximized)
                {
                    stage.setX(mouseEvent.getScreenX() + initialX);
                    stage.setY(mouseEvent.getScreenY() + initialY);
                }
            }
          });
        
        close.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent actionEvent) {
                Platform.exit();
            }
        });
        minimize.setOnAction(e -> {
            ((Stage)((Button)e.getSource()).getScene().getWindow()).setIconified(true);
        });
        
        expand.setOnAction(e -> {
            if (isMaximized)
            {
                stage.setX((SCREEN_BOUNDS.getMaxX() - prefWidth)/2);
                stage.setY((SCREEN_BOUNDS.getMaxY() - prefHeight)/2);
                stage.setWidth(prefWidth);
                stage.setHeight(prefHeight);
                expand.setText("⛶");
                isMaximized = false;
            }
            else
            {
                stage.setX(SCREEN_BOUNDS.getMinX());
                stage.setY(SCREEN_BOUNDS.getMinY());
                stage.setWidth(SCREEN_BOUNDS.getWidth());
                stage.setHeight(SCREEN_BOUNDS.getHeight());
                expand.setText("\u2317");
                isMaximized = true;
            }
        });
        
        personalityChoiceBox.setTooltip(new Tooltip("Select the personality"));

        mediaView.setPreserveRatio(true);
        mediaView.fitWidthProperty().bind(mediaViewBox.widthProperty());
        mediaView.fitHeightProperty().bind(mediaViewBox.heightProperty());

        imageView.setPreserveRatio(true);
        imageView.fitWidthProperty().bind(mediaViewBox.widthProperty());
        imageView.fitHeightProperty().bind(mediaViewBox.heightProperty());
        mediaViewBox.setBackground(new Background(new BackgroundFill(Color.BLACK, CornerRadii.EMPTY, Insets.EMPTY)));

        chatTextField.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent ke) {
                if (ke.getCode().equals(KeyCode.ENTER)) {
                    String currentText = chatTextField.getText();

                    if (currentText.length() == 0 || currentText == null) {
                        return;
                    }

                    currentText = currentText.trim();

                    ChatHandler.getHandler().getSubParticipant().sendMessage(currentText);

                    //Clear the chat field
                    chatTextField.setText("");

                    //Call the sub message event
                    ChatHandler.getHandler().onSubMessage(currentText);
                }
            }
        });

        startChatButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                if (PersonalityManager.getManager().getActivePersonality() == null) {
                    return;
                }

                if (TeaseAI.application.getSession().isStarted()) {
                    startChatButton.setDisable(true);
                    //Notify the thread because we want it continue and then end anyway
                    synchronized (TeaseAI.application.getScriptThread()) {
                        TeaseAI.application.getSession().setHaltSession(true);
                        TeaseAI.application.getScriptThread().notify();
                    }

                } else {
                    PersonalityManager.getManager().setActivePersonality((Personality) getPersonalityChoiceBox().getSelectionModel().getSelectedItem());

                    /*synchronized (TeaseAI.application.getScriptThread()) {
                        TeaseAI.application.getScriptThread().notify();
                    }*/

                    TeaseAI.application.getSession().start();
                }
            }
        });

        runScriptMenuItem.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                if (PersonalityManager.getManager().getActivePersonality() == null) {
                    return;
                }

                FileChooser chooser = new FileChooser();
                chooser.setTitle("Select Script");

                File defaultDirectory = TeaseAI.application.getSession().getActivePersonality().getFolder();
                chooser.setInitialDirectory(defaultDirectory);

                chooser.getExtensionFilters().addAll(
                        new FileChooser.ExtensionFilter("Javascript", "*.js")
                );

                File script = chooser.showOpenDialog(stage);

                if (script != null && script.exists()) {
                    String extension = FileUtils.getExtension(script);
                    if ((extension.equalsIgnoreCase("js"))) {
                        PersonalityManager.getManager().setActivePersonality((Personality) getPersonalityChoiceBox().getSelectionModel().getSelectedItem());

                        TeaseAI.application.getSession().startWithScript(script);
                    } else {
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setTitle("Invalid File");
                        alert.setHeaderText(null);
                        alert.setContentText("The given file is not a supported script file.");

                        alert.showAndWait();
                    }
                }
            }
        });

        personalityChoiceBox.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Personality>() {
            @Override
            public void changed(ObservableValue<? extends Personality> observableValue, Personality oldValue, Personality newValue) {
                if (TeaseAI.application.getSession() != null) {
                    TeaseAI.application.getSession().setActivePersonality(newValue);
                    TeaseAI.application.LAST_SELECTED_PERSONALITY.setValue(newValue.getName().getValue()).save();
                }
            }
        });

        Label settingsLabel = new Label("Settings");
        menuSettingsButton.setGraphic(settingsLabel);
        settingsLabel.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent e) {
                SettingsController.openGUI();
            }
        });

        lazySubController = new LazySubController(lazySubPane);
        lazySubController.createDefaults();
        
        AppearanceSettings.loadSelectedTheme();
    }

    public void loadDomInfo() {
        domImageView.setPreserveRatio(true);
        Pane pane = new Pane();
        contactImageGrid.add(pane, 0, 1);
        domImageView.fitWidthProperty().bind(pane.widthProperty());
        domImageView.fitHeightProperty().bind(pane.heightProperty());

        ChatParticipant domParticipant = ChatHandler.getHandler().getMainDomParticipant();
        ChatParticipant subParticipant = ChatHandler.getHandler().getSubParticipant();

        File domImage = domParticipant.getContact().getImage();
        if (domImage != null && domImage.exists()) {
            ImageUtils.setImageInView(domImage, domImageView);
        } else {
            domImageView.setImage(null);
        }

        pane.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                FileChooser chooser = new FileChooser();
                chooser.setTitle("Select Media Folder");

                String dir;
                if (new File(domParticipant.getContact().IMAGE_PATH.getValue()).exists()) {
                    dir = domParticipant.getContact().IMAGE_PATH.getValue();
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

                File image = chooser.showOpenDialog(stage);

                if (image != null && image.exists()) {
                    String extension = FileUtils.getExtension(image);

                    if ((extension.equalsIgnoreCase("jpg") || extension.equalsIgnoreCase("png"))) {
                        domParticipant.getContact().IMAGE_PATH.setValue(image.getPath());
                        domParticipant.getContact().IMAGE_PATH.save();

                        if (image != null && image.exists()) {
                            ImageUtils.setImageInView(image, domImageView);
                        } else {
                            domImageView.setImage(null);
                        }
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

        domNameTextField.setText(domParticipant.getContact().NAME.getValue());
        subNameTextField.setText(subParticipant.getContact().NAME.getValue());

        domNameTextField.focusedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> arg0, Boolean oldPropertyValue, Boolean newPropertyValue) {
                if (newPropertyValue) {
                    //On focus
                } else {
                    //Lost focus
                    domParticipant.getContact().NAME.setValue(domNameTextField.getText());
                    domParticipant.setName(domParticipant.getContact().NAME.getValue());
                    domParticipant.getContact().save();
                }
            }
        });

        subNameTextField.focusedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> arg0, Boolean oldPropertyValue, Boolean newPropertyValue) {
                if (newPropertyValue) {
                    //On focus
                } else {
                    //Lost focus
                    subParticipant.getContact().NAME.setValue(subNameTextField.getText());
                    subParticipant.setName(subParticipant.getContact().NAME.getValue());
                    subParticipant.getContact().save();
                }
            }
        });
    }

    public TextField getChatTextField() {
        return chatTextField;
    }

    public MediaView getMediaView() {
        return mediaView;
    }

    public ImageView getImageView() {
        return imageView;
    }

    public StackPane getMediaViewBox() {
        return mediaViewBox;
    }

    public TextFlow getChatWindow() {
        return chatWindow;
    }

    public ScrollPane getChatScrollPane() {
        return chatScrollPane;
    }

    public ChoiceBox getPersonalityChoiceBox() {
        return personalityChoiceBox;
    }

    public Button getStartChatButton() {
        return startChatButton;
    }

    public LazySubController getLazySubController() {
        return lazySubController;
    }
    
    public static MainGuiController getController()
    {
        return thisController;
    }
    
    public TextField getDomNameTextField()
    {
        return domNameTextField;
    }
}