package me.goddragon.teaseai.api.chat;

import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import me.goddragon.teaseai.TeaseAI;
import me.goddragon.teaseai.api.chat.response.Response;
import me.goddragon.teaseai.api.chat.response.ResponseHandler;
import me.goddragon.teaseai.api.chat.vocabulary.VocabularyHandler;
import me.goddragon.teaseai.api.media.MediaHandler;
import me.goddragon.teaseai.api.picture.PictureSet;
import me.goddragon.teaseai.api.picture.TaggedPicture;
import me.goddragon.teaseai.api.session.Session;
import me.goddragon.teaseai.utils.RandomUtils;
import me.goddragon.teaseai.utils.TeaseLogger;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.Level;

/**
 * Created by GodDragon on 22.03.2018.
 */
public class ChatParticipant {

    private static int latestId = 0;

    private int id;
    private String name;
    private Contact contact;
    private SenderType type;
    private boolean active = false;
    private TypeSpeed typeSpeed;

    private PictureSet pictureSet;

    private Color nameColor = Color.BLACK;

    public ChatParticipant(SenderType type, Contact contact) {
        this(latestId + 1, type, contact);
    }

    public ChatParticipant(String name, SenderType type) {
        this(latestId + 1, name, type, null);
    }

    public ChatParticipant(int id, SenderType type, Contact contact) {
        this(id, contact.NAME.getValue(), type, contact);
    }

    public ChatParticipant(int id, String name, SenderType type, Contact contact) {
        this.id = id;

        //Update the latest id
        latestId = Math.max(id, latestId);

        this.name = name;
        this.type = type;
        this.contact = contact;
        this.typeSpeed = ChatHandler.getHandler().getTypeSpeed();

        if (type == SenderType.SUB) {
            active = true;
        }

        choosePictureSet();
    }

    public void sendJoin() {
        sendInteract("joined");
    }

    public void sendLeave() {
        sendInteract("left");
    }

    public void sendInteract(String type) {
        Text nameText = new Text(name + " ");
        nameText.setFont(Font.font(null, FontWeight.BOLD, TeaseAI.application.CHAT_TEXT_SIZE.getDouble() + 2));
        nameText.setFill(this.nameColor);

        Text messageText = new Text(type + " the chat room.");
        messageText.setFill(Color.AQUA);
        messageText.setFont(Font.font(null, FontWeight.BOLD, TeaseAI.application.CHAT_TEXT_SIZE.getDouble() + 2));

        ChatHandler.getHandler().addLine(nameText, messageText);
    }

    public void sendMessage(String message) {
        sendMessage(message, ChatHandler.getHandler().getMillisToPause(message));
    }

    public void sendMessage(String message, int secondsToWait) {
        sendMessage(message, secondsToWait * 1000L);
    }

    public void sendMessage(String message, long millisToWait) {
        //We need to wait BEFORE we replace the vocabularies. Otherwise any code triggered by the vocab will execute before the message is send
        startTyping(message);

        //Replace all vocabularies
        message = VocabularyHandler.getHandler().replaceAllVocabularies(message);

        Text messageText = new Text(message);
        messageText.setFont(Font.font(null, FontWeight.NORMAL, TeaseAI.application.CHAT_TEXT_SIZE.getDouble()));
        messageText.setFill(ChatHandler.getHandler().getDefaultChatColor());

        sendMessage(message, messageText, millisToWait);
    }

    public void sendMessage(String rawMessage, Text message, long millisToWait) {
        sendMessage(rawMessage, millisToWait, message);
    }

    public void sendMessage(String rawMessage, long millisToWait, Text... messages) {
        sendMessage(rawMessage, millisToWait, Arrays.asList(messages));
    }

    public void sendMessage(String rawMessage, long millisToWait, List<Text> messages) {
        DateFormat dateFormat = new SimpleDateFormat("hh:mm a");
        if (messages.size() == 1 && messages.get(0).getFill().equals(Color.BLACK)) {
            messages.get(0).setFill(ChatHandler.getHandler().getDefaultChatColor());
        } else {
            boolean allBlack = true;
            for (Text text : messages) {
                if (!text.getFill().equals(Color.BLACK)) {
                    allBlack = false;
                    break;
                }
            }
            if (allBlack) {
                for (Text text : messages) {
                    text.setFill(ChatHandler.getHandler().getDefaultChatColor());
                }
            }
        }

        Text dateText = new Text(dateFormat.format(new Date()) + " ");
        dateText.setFill(ChatHandler.getHandler().getDateColor());
        dateText.setFont(Font.font(null, FontWeight.MEDIUM, TeaseAI.application.CHAT_TEXT_SIZE.getDouble()));
        Text text = new Text(name + ": ");

        text.setFill(this.nameColor);

        text.setFont(Font.font(null, FontWeight.BOLD, TeaseAI.application.CHAT_TEXT_SIZE.getDouble() + 1));
        //Check whether we can find a response fitting right now
        if (type == SenderType.SUB) {
            Collection<Response> responses = ResponseHandler.getHandler().checkMessageForResponse(rawMessage);
            if (!responses.isEmpty()) {
                for(Response response : responses) {
                    //Set the message of the response so we know what triggered it later on
                    response.setMessage(rawMessage);

                    //Queue the response so we can call it later
                    ResponseHandler.getHandler().addQueuedResponse(response);
                }
            }
        } else {
            //If the dom sends a message we will check for queued responses that need to be handle before continuing

            //This should always run on the script thread but maybe I am stupid so we will catch this here
            if (Thread.currentThread() != TeaseAI.application.getScriptThread()) {
                throw new IllegalStateException("Dom can only send messages on the script thread");
            }


            if(TeaseAI.getApplication().checkForNewResponses()) {
                //If this returns true the trigger was successful and we won't send the current message that was supposed to be sent
                return;
            }

            /*
            Response queuedResponse = ResponseHandler.getHandler().getLatestQueuedResponse();
            if (queuedResponse != null) {
                ResponseHandler.getHandler().removeQueuedResponse(queuedResponse);


                if (queuedResponse.trigger()) {
                    return;
                }
            }*/
        }

        List<Text> lineMessages = new ArrayList<>();
        lineMessages.add(dateText);
        lineMessages.add(text);
        lineMessages.addAll(messages);
        ChatHandler.getHandler().addLine(lineMessages);

        //TeaseLogger.getLogger().log(Level.INFO, "Current PictureSet:" + pictureSet);
        if (type != SenderType.SUB && !MediaHandler.getHandler().isImagesLocked() && pictureSet != null) {
            Session session = TeaseAI.application.getSession();
            TaggedPicture taggedPicture = session.getActivePersonality().getPictureSelector().getPicture(session, this);
            if (taggedPicture != null) {
                MediaHandler.getHandler().showPicture(taggedPicture.getFile());
            }
        }

        //Wait some time after this message before continuing (only if it is not the sub who send the message)
        if (millisToWait > 0 && type != SenderType.SUB) {
            TeaseAI.application.sleepPossibleScripThread(millisToWait);
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
        answer.setStartedAt(System.currentTimeMillis());

        //Wait for answer
        TeaseAI.application.waitPossibleScripThread(answer.getMillisTimeout());
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
            text.setFont(Font.font(null, FontWeight.BOLD, TeaseAI.application.CHAT_TEXT_SIZE.getDouble() + 2));
            ChatHandler.getHandler().addTemporaryMessage(text);

            TeaseAI.application.sleepPossibleScripThread(millisToWait, true);

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

    public Contact getContact() {
        return contact;
    }

    public void choosePictureSet() {
        File imageFolder = contact == null ? null : contact.getImageFolder();
        if (imageFolder == null || !imageFolder.exists()) {
            return;
        }

        List<PictureSet> pictureSets = new ArrayList<>();
        for (File file : imageFolder.listFiles((current, name) -> new File(current, name).isDirectory())) {
            PictureSet pictureSet = new PictureSet(file);

            //No pictures => ignore the set
            if (pictureSet.getTaggedPictures().isEmpty() && pictureSet.getPicturesInFolder().length == 0) {
                continue;
            }

            pictureSets.add(pictureSet);
        }

        this.pictureSet = null;
        TeaseLogger.getLogger().log(Level.INFO, "Loaded " + pictureSets.size() + " picture sets for " + name);

        if (!pictureSets.isEmpty()) {
            int loops = 0;
            while (this.pictureSet == null && loops < 20) {
                PictureSet pictureSet = pictureSets.get(RandomUtils.randInt(0, pictureSets.size() - 1));
                if (!pictureSet.getTaggedPictures().isEmpty() || pictureSet.getPicturesInFolder().length > 0) {
                    this.pictureSet = pictureSet;
                }

                loops++;
            }
        }
    }

    public PictureSet getPictureSet() {
        return pictureSet;
    }

    public void setPictureSet(PictureSet pictureSet) {
        this.pictureSet = pictureSet;
    }


    public ChatParticipant setNameColor(Color nameColor) {
        this.nameColor = nameColor;
        return this;
    }

    public Color getNameColor() {
        return nameColor;
    }

    @Override
    public String toString() {
        return name;
    }
}
