package me.goddragon.teaseai.api.chat.response;

import me.goddragon.teaseai.api.scripts.ScriptHandler;
import me.goddragon.teaseai.api.scripts.personality.Personality;
import me.goddragon.teaseai.utils.StringUtils;
import me.goddragon.teaseai.utils.TeaseLogger;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.logging.Level;

/**
 * Created by GodDragon on 24.03.2018.
 */
public class ResponseHandler {

    private static ResponseHandler handler = new ResponseHandler();

    private final Collection<Response> responses = new HashSet<>();

    private final List<Response> queuedResponse = new ArrayList<>();

    private Response currentLoadingResponse;

    public void loadResponsesFromPersonality(Personality personality) {
        responses.clear();

        File folder = personality.getFolder();
        File vocabFolder = new File(folder.getAbsolutePath() + "\\Responses");
        vocabFolder.mkdir();

        for (File file : vocabFolder.listFiles()) {
            if (file.isFile() && file.getName().endsWith(".js")) {
                ScriptEngine engine = ScriptHandler.getHandler().getEngine();

                currentLoadingResponse = new Response() {
                    @Override
                    public boolean trigger() {
                        ScriptEngine engine = ScriptHandler.getHandler().getEngine();
                        String responseName = file.getName().substring(0, file.getName().length() - 3);

                        try {
                            Invocable invocable = (Invocable) engine;

                            Object result = invocable.invokeFunction(StringUtils.decapitalize(responseName) + "Response", getMessage());

                            if(result instanceof Boolean) {
                                return (Boolean) result;
                            }
                            return false;
                        } catch (ScriptException e) {
                            TeaseLogger.getLogger().log(Level.SEVERE, "Error while handling file '" + e.getFileName() + "' in line " + e.getLineNumber() + "\n" +
                                    "Error: " + e.getMessage(), false);
                        } catch (NoSuchMethodException e) {
                            TeaseLogger.getLogger().log(Level.SEVERE, "Response '" + responseName + " is missing the function to trigger it. Create the function '" + StringUtils.decapitalize(responseName)  + "Response(message)' for this to work." , false);
                        }
                        return false;
                    }
                };

                //Run the file to load everything after we set the current loading response
                try {
                    engine.eval(new FileReader(file));
                } catch (ScriptException | FileNotFoundException e) {
                    e.printStackTrace();
                }

                registerResponse(currentLoadingResponse);

                //To disallow later modifications out of accident
                currentLoadingResponse = null;
            }
        }

        TeaseLogger.getLogger().log(Level.INFO, "Loaded " + responses.size() + " responses.");
    }

    public void registerResponse(Response response) {
        synchronized (responses) {
            if(!responses.contains(response)) {
                responses.add(response);
            }
        }
    }

    public void unregisterResponse(Response response) {
        synchronized (responses) {
            responses.remove(response);
        }
    }

    public void addQueuedReponse(Response response) {
        synchronized (queuedResponse) {
            queuedResponse.add(response);
        }
    }

    public void removeQueuedReponse(Response response) {
        synchronized (queuedResponse) {
            queuedResponse.remove(response);
        }
    }

    public Response getLatestQueuedReponse() {
        synchronized (queuedResponse) {
            if (!queuedResponse.isEmpty()) {
                return queuedResponse.get(queuedResponse.size() - 1);
            }
        }

        return null;
    }

    public Response checkMessageForResponse(String message) {
        synchronized (responses) {
            for (Response response : responses) {
                if (response.containsLike(message) && !response.isDisabled()) {
                    return response;
                }
            }
        }

        return null;
    }

    public Response getCurrentLoadingResponse() {
        return currentLoadingResponse;
    }

    public static ResponseHandler getHandler() {
        return handler;
    }

    public static void setHandler(ResponseHandler handler) {
        ResponseHandler.handler = handler;
    }
}
