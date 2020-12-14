package me.goddragon.teaseai.gui.main;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import me.goddragon.teaseai.api.chat.ChatHandler;
import me.goddragon.teaseai.api.chat.vocabulary.VocabularyHandler;

/**
 * Created by GodDragon on 16.05.2018.
 */
public class LazySubController {

    private final FlowPane flowPane;

    public LazySubController(FlowPane flowPane) {
        this.flowPane = flowPane;
    }

    public void createDefaults() {
        Label setAnswers = new Label("Lazy Sub");
        setAnswers.setPrefWidth(flowPane.getWidth());
        setAnswers.setAlignment(Pos.BASELINE_CENTER);
        //setAnswers.setStyle("-fx-border-color: black;");

        this.flowPane.getChildren().add(setAnswers);
        addButton(createSendMessageButton("Hello", "Hello Mistress", flowPane.getWidth()));
        addButton(createSendMessageButton("Yes", "Yes Mistress", flowPane.getWidth() / 2D));
        addButton(createSendMessageButton("No", "No Mistress", flowPane.getWidth() / 2D));
        addButton(createSendMessageButton("Edge", "I am on the edge", flowPane.getWidth() / 2D));
        addButton(createSendMessageButton("Sorry", "I am sorry Mistress", flowPane.getWidth() / 2D));

        Label dynamicAnswers = new Label("Dynamic Answers");
        dynamicAnswers.setPrefWidth(flowPane.getWidth());
        dynamicAnswers.setAlignment(Pos.BASELINE_CENTER);

        //dynamicAnswers.setStyle("-fx-border-color: black;");
        //dynamicAnswers.setBackground(new Background(new BackgroundFill(Color.GRAY, CornerRadii.EMPTY, Insets.EMPTY)));

        this.flowPane.getChildren().add(dynamicAnswers);
    }

    public Button createSendMessageButton(String buttonTitle, String chatMessage) {
        return createSendMessageButton(buttonTitle, chatMessage, -1);
    }

    public Button createSendMessageButton(String buttonTitle, String chatMessage, double width) {
        Button button = new Button(buttonTitle);

        /*Button startChat = TeaseAI.getApplication().getController().getStartChatButton();

        button.setId("layzSubButton");
        button.setStyle(startChat.getStyle());
        //button.getStylesheets().add("primary-color");*/

        button.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                String innerMessage = chatMessage;

                if(VocabularyHandler.getHandler().isVocabulary("DomHonorific")) {
                    if(innerMessage.contains("Mistress")) {
                        innerMessage = VocabularyHandler.getHandler().replaceAllVocabularies(innerMessage.replaceAll("Mistress", "%DomHonorific%"));
                    }
                }

                ChatHandler.getHandler().getSubParticipant().sendMessage(innerMessage);

                //Call the sub message event
                ChatHandler.getHandler().onSubMessage(innerMessage);
            }
        });

        if (width > 0) {
            button.setPrefWidth(width);
        }

        return button;
    }

    public void addButton(Button button) {
        flowPane.getChildren().add(button);
    }

    public void clear() {
        flowPane.getChildren().clear();
    }

    public FlowPane getFlowPane() {
        return flowPane;
    }
}
