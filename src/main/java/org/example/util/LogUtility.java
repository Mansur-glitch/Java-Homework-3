package org.example.util;

import java.io.IOException;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class LogUtility {
    private static final Logger LOGGER = Logger.getLogger(LogUtility.class.getName());

    static {
        LOGGER.setUseParentHandlers(false);
        try {
            FileHandler fileHandler = new FileHandler("log.txt", true);
            fileHandler.setFormatter(new SimpleFormatter());
            LOGGER.addHandler(fileHandler);
        } catch (IOException e) {
            System.err.printf("Failed to open log file. %s\n", e.getMessage());
            System.err.println("Writing log to standard error stream.");
            LOGGER.addHandler(new ConsoleHandler());
        }
    }

    public static Logger logger() {
        return LOGGER;
    }

}
