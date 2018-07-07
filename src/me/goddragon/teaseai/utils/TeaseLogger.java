package me.goddragon.teaseai.utils;

import java.io.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.*;

/**
 * Created by GodDragon on 24.03.2018.
 */
public class TeaseLogger {

    private static TeaseLogger logger = new TeaseLogger();
    private PrintStream outStream;
    private boolean fileLog = true;
    //private Logger javaLogger;

    public TeaseLogger() {
        if(!fileLog) {
            return;
        }

        /*javaLogger = Logger.getLogger("MyLog");
        javaLogger.setLevel(Level.ALL);
        FileHandler fileHandler;*/

        try {
            if (!new File("Logs").exists()) {
                new File("Logs").mkdir();
            }

            // This block configure the logger with handler and formatter
            DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy H-mm-ss");
            Date date = new Date();

            //Set the system error output to the log file
            outStream = new PrintStream(new FileOutputStream("Logs" + File.separator + "log-" + dateFormat.format(date) + ".txt"));
            System.setOut(outStream);
            System.setErr(outStream);

            /*fileHandler = new FileHandler("Logs" + File.separator + "log-" + dateFormat.format(date) + ".txt");


            javaLogger.setUseParentHandlers(false);
            javaLogger.addHandler(fileHandler);
            Formatter formatter = new SimpleFormatter() {
                private final Date dat = new Date();

                @Override
                public synchronized String format(LogRecord record) {
                    dat.setTime(record.getMillis());
                    String message = formatMessage(record);

                    if(message.trim().isEmpty()) {
                        return "";
                    }

                    String throwable = "";

                    if (record.getThrown() != null) {
                        StringWriter sw = new StringWriter();
                        PrintWriter pw = new PrintWriter(sw);
                        pw.println();
                        record.getThrown().printStackTrace(pw);
                        pw.close();
                        throwable = sw.toString();
                    }

                    String level = record.getLevel().toString();
                    if(record.getLevel() == Level.FINE) {
                        level = "CHAT";
                    }

                    DateFormat dateFormat = new SimpleDateFormat("hh:mm:ss a");

                    return dateFormat.format(dat) + " " + level + ": " + message + "\n";
                }
            };
            fileHandler.setFormatter(formatter);*/
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private String formatMessage(String message, Level level) {
        Date dat = new Date();
        dat.setTime(System.currentTimeMillis());
        if (message.trim().isEmpty()) {
            return "";
        }

        String levelString = level.toString();
        if (level == Level.FINE) {
            levelString = "CHAT";
        }

        DateFormat dateFormat = new SimpleDateFormat("hh:mm:ss a");

        return dateFormat.format(dat) + " " + levelString + ": " + message.replaceAll("[\\x00]", "");
    }

    public void log(Level level, String message) {
        log(level, message, level == Level.SEVERE);
    }

    public void log(Level level, String message, Exception e) {
        //javaLogger.log(level, message);
        String logMessage = formatMessage(message, level);
        if(!logMessage.isEmpty()) {
            System.out.println(logMessage);
        }
    }

    public void log(Level level, String message, boolean stacktrace) {
        //javaLogger.log(level, message);
        String logMessage = formatMessage(message, level);
        if(!logMessage.isEmpty()) {
            System.out.println(logMessage);
        }
    }

    public static TeaseLogger getLogger() {
        return logger;
    }

    public static void setLogger(TeaseLogger logger) {
        TeaseLogger.logger = logger;
    }
}
