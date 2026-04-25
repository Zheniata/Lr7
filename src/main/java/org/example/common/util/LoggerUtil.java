package org.example.common.util;

import java.io.IOException;
import java.util.logging.*;

public class LoggerUtil {
    private static final Logger logger = Logger.getLogger("ServerLogger");
    private static boolean initialized = false;

    public static Logger initLogger(String logFile) {
        if (initialized) {
            return logger;
        }

        try {
            logger.setUseParentHandlers(false);
            logger.setLevel(Level.ALL);

            FileHandler fileHandler = new FileHandler(logFile, true);
            fileHandler.setLevel(Level.ALL);
            fileHandler.setFormatter(new SimpleFormatter());
            logger.addHandler(fileHandler);

            ConsoleHandler consoleHandler = new ConsoleHandler();
            consoleHandler.setLevel(Level.INFO);
            consoleHandler.setFormatter(new SimpleFormatter());
            logger.addHandler(consoleHandler);

            initialized = true;
            logger.info("Логирование инициализировано");

        } catch (IOException e) {
            System.err.println("Ошибка инициализации логгера: " + e.getMessage());
            e.printStackTrace();
        }
        return logger;
    }

    public static Logger getLogger() {
        return logger;
    }
}
