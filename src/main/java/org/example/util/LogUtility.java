package org.example.util;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.FileHandler;
import java.util.logging.LogManager;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class LogUtility {
    private static final Logger LOGGER;
    static {
        String configString = """
                handlers=java.util.logging.FileHandler
                java.util.logging.FileHandler.formatter=java.util.logging.SimpleFormatter
                java.util.logging.FileHandler.pattern=log.txt
                java.util.logging.FileHandler.append=true
                org.hibernate.level=WARNING
                org.mockito.level=WARNING
                """;
        InputStream stream = new ByteArrayInputStream(configString.getBytes());
        try {
            LogManager.getLogManager().readConfiguration(stream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        LOGGER = Logger.getLogger(LogUtility.class.getName());
    }

    public static Logger getStaticLogger() {
        return LOGGER;
    }

    public static Logger createLogger(String filename) {
        Logger logger = Logger.getLogger(filename);
        logger.setUseParentHandlers(false);

        FileHandler fileHandler;
        try {
            fileHandler = new FileHandler(filename, false);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        fileHandler.setFormatter(new SimpleFormatter());
        logger.addHandler(fileHandler);

        return logger;
    }
}
