package com.humanharvest.organz.utilities;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

/**
 * Static logger setup. Should be called at launch before anything else
 * The logger can be used in any class by calling the following line then using the logger by LOGGER.severity(text);
 * private static final Logger LOGGER = Logger.getLogger(CLASS_NAME.class.getName());
 */
public class LoggerSetup {

    private LoggerSetup() {
    }

    /**
     * Setup the logger. Writes to a file called "organz.log" in the app directory
     * @param logLevel The log level required
     */
    public static void setup(Level logLevel) {

        Logger logger = Logger.getLogger("");

        logger.setLevel(logLevel);
        FileHandler fileTxt;

        try {
            fileTxt = new FileHandler("organz.log", true);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        //Remove default console logger
        logger.removeHandler(logger.getHandlers()[0]);

        //Initialise a standard file based text logger
        Formatter textFormatter = createCustomFormatter();
        fileTxt.setFormatter(textFormatter);
        logger.addHandler(fileTxt);

        logger.info("New session");
    }

    private static Formatter createCustomFormatter() {
        return  new Formatter() {
            @Override
            public String format(LogRecord record) {
                Instant instant = Instant.EPOCH.plus(record.getMillis(), ChronoUnit.MILLIS);
                String source;
                if (record.getSourceClassName() != null) {
                    source = record.getSourceClassName();
                    if (record.getSourceMethodName() != null) {
                        source += "::" + record.getSourceMethodName();
                    }
                } else {
                    source = record.getLoggerName();
                }
                String message = formatMessage(record);
                String throwable = "";
                if (record.getThrown() != null) {
                    StringWriter sw = new StringWriter();
                    try(PrintWriter pw = new PrintWriter(sw)) {
                        pw.println();
                        record.getThrown().printStackTrace(pw);
                    }
                    throwable = sw.toString();
                }
                return String.format("%s %s%n%s: %s%s%n",
                        instant,
                        source,
                        record.getLevel().toString(),
                        message,
                        throwable);
            }
        };
    }

    public static void enableConsole() {

        Logger logger = Logger.getLogger("");

        ConsoleHandler handler = new ConsoleHandler();
        Formatter formatter = new Formatter() {
            @Override
            public String format(LogRecord record) {
                return String.format("%s\n", record.getMessage());
            }
        };
        handler.setFormatter(formatter);

        logger.addHandler(handler);
    }
}