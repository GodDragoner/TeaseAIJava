package me.goddragon.teaseai.utils;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.*;

/**
 * Created by GodDragon on 24.03.2018.
 */
public class TeaseLogger {

    private static TeaseLogger logger = new TeaseLogger();
    private Logger javaLogger;

    public TeaseLogger() {
        javaLogger = Logger.getLogger("MyLog");
        javaLogger.setLevel(Level.ALL);
        FileHandler fh;

        try {
            if(!new File("Logs").exists()) {
                new File("Logs").mkdir();
            }

            // This block configure the logger with handler and formatter
            DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy H-mm-ss");
            Date date = new Date();
            fh = new FileHandler("Logs\\log-" + dateFormat.format(date) + ".txt");
            javaLogger.addHandler(fh);
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
            fh.setFormatter(formatter);
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void log(Level level, String message) {
        log(level, message, level == Level.SEVERE);
    }

    public void log(Level level, String message, Exception e) {
        javaLogger.log(level, message);
    }

    public void log(Level level, String message, boolean stacktrace) {
        javaLogger.log(level, message);
    }

    public static TeaseLogger getLogger() {
        return logger;
    }

    public static void setLogger(TeaseLogger logger) {
        TeaseLogger.logger = logger;
    }
}
