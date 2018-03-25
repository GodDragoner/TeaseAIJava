package me.goddragon.teaseai.api.chat;

import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import me.goddragon.teaseai.TeaseAI;
import me.goddragon.teaseai.api.chat.response.Response;
import me.goddragon.teaseai.api.chat.response.ResponseHandler;
import me.goddragon.teaseai.api.chat.vocabulary.VocabularyHandler;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by GodDragon on 22.03.2018.
 */
public class ChatParticipant {

    private static int latestId = 0;

    private int id;
    private String name;
    private SenderType type;
    private boolean active = false;
    private TypeSpeed typeSpeed;
    private Color chatColor;

    public ChatParticipant(String name, SenderType type) {
        this(latestId + 1, name, type);
    }

    public ChatParticipant(int id, String name, SenderType type) {
        this.id = id;

        //Update the latest id
        latestId = Math.max(id, latestId);

        this.name = name;
        this.type = type;
        this.typeSpeed = ChatHandler.getHandler().getTypeSpeed();

        switch (id) {
            case 0:
                chatColor = Color.DARKCYAN;
                break;
            case 1:
                chatColor = Color.RED;
                break;
            case 2:
                chatColor = Color.ORANGE;
                break;
            case 3:
                chatColor = Color.LIGHTGREEN;
                break;
            case 4:
                chatColor = Color.MEDIUMVIOLETRED;
                break;
            case 5:
                chatColor = Color.TEAL;
                break;
            default:
                chatColor = Color.SALMON;
                break;
        }

        if (type == SenderType.SUB) {
            active = true;
        }
    }


    public ChatParticipant(int id, String name, SenderType type, Color chatColor) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.chatColor = chatColor;

        if (type == SenderType.SUB) {
            active = true;
        }
    }

    public void sendMessage(String message) {
        sendMessage(message, ChatHandler.getHandler().getMillisToPause(message));
    }

    public void sendMessage(String message, int secondsToWait) {
        sendMessage(message, secondsToWait * 1000L);
    }

    private void sendMessage(String message, long millisToWait) {
        //Replace all vocabularies
        message = VocabularyHandler.getHandler().replaceAllVocabularies(message);

        DateFormat dateFormat = new SimpleDateFormat("hh:mm a");

        Text dateText = new Text(dateFormat.format(new Date()) + " ");
        dateText.setFill(Color.DARKGRAY);
        dateText.setFont(Font.font(null, FontWeight.MEDIUM, 12));
        Text text = new Text(name + ": ");

        text.setFill(chatColor);

        text.setFont(Font.font(null, FontWeight.BOLD, 13));
        Text messageText = new Text(message);

        startTyping(message);

        //Check whether we can find a response fitting right now
        if (type == SenderType.SUB) {
            Response response = ResponseHandler.getHandler().checkMessageForResponse(message);
            if (response != null) {
                //Set the message of the response so we know what triggered it later on
                response.setMessage(message);

                //Queue the response so we can call it later
                ResponseHandler.getHandler().addQueuedReponse(response);
            }
        } else {
            //If the dom sends a message we will check for queued responses that need to be handle before continuing

            //This should always run on the script thread but maybe I am stupid so we will catch this here
            if (Thread.currentThread() != TeaseAI.application.getScriptThread()) {
                throw new IllegalStateException("Dom can only send messages on the script thread");
            }

            Response queuedResponse = ResponseHandler.getHandler().getLatestQueuedReponse();
            if (queuedResponse != null) {
                ResponseHandler.getHandler().removeQueuedReponse(queuedResponse);

                //If this returns true the trigger was successful and we won't send the current message that was supposed to be sent
                if (queuedResponse.trigger()) {
                    return;
                }
            }
        }

        ChatHandler.getHandler().addLine(dateText, text, messageText);

        //Wait some time after this message before continuing (only if it is not the sub who send the message)
        if (millisToWait > 0 && type != SenderType.SUB) {
            TeaseAI.application.sleepThread(Thread.currentThread(), millisToWait);
        }
    }

    public Answer sendInput(String message) {
        return sendInput(message, 0);
    }

    public Answer sendInput(String message, int timeoutSeconds) {
        return sendInput(message, new Answer(timeoutSeconds));
    }

    public Answer sendInput(String message, Answer answer) {
        //No waiting here because we will wait later on anyway
        sendMessage(message, 0);

        ChatHandler.getHandler().setCurrentCallback(answer);

        //Reset timeout (normally the answer is a new object, but we don't know whether they might reuse an old answer)
        answer.setTimeout(false);

        //Reset the latest answer message
        answer.setAnswer(null);

        //Wait for answer
        TeaseAI.application.waitThread(Thread.currentThread(), answer.getMillisTimeout());
        answer.checkTimeout();

        return answer;
    }

    private void startTyping(String message) {
        if (type == SenderType.SUB) {
            return;
        }

        long millisToWait = typeSpeed.getTypeDuration(message);

        if (millisToWait > 0) {
            Text text = new Text(name + " is typing...");
            text.setFill(Color.AQUA);
            text.setFont(Font.font(null, FontWeight.BOLD, 14));
            ChatHandler.getHandler().addTemporaryMessage(text);

            TeaseAI.application.sleepThread(Thread.currentThread(), millisToWait);

            ChatHandler.getHandler().removeTemporaryMessage(text);
        }
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public SenderType getType() {
        return type;
    }

    public void setType(SenderType type) {
        this.type = type;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        if (type == SenderType.SUB) {
            throw new IllegalStateException("Sub cannot be inactive in chat");
        }

        this.active = active;
    }

    public TypeSpeed getTypeSpeed() {
        return typeSpeed;
    }

    public void setTypeSpeed(TypeSpeed typeSpeed) {
        this.typeSpeed = typeSpeed;
    }

    public Color getChatColor() {
        return chatColor;
    }

    public void setChatColor(Color chatColor) {
        this.chatColor = chatColor;
    }
}
