package seng302.Utilities;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

/**
 * Static logger setup. Should be called at launch before anything else
 * The logger can be used in any class by calling the following line then using the logger by LOGGER.severity(text);
 * private final static Logger LOGGER = Logger.getLogger(PageNavigator.class.getName());
 */
public class LoggerSetup {

    /**
     * Setup the logger. Writes to a file called "organz.log" in the app directory
     * @param logLevel The log level required
     */
    static public void setup(Level logLevel) {

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

        //Initialize a standard file based text logger
        SimpleFormatter formatterTxt = new SimpleFormatter();
        fileTxt.setFormatter(formatterTxt);
        logger.addHandler(fileTxt);

        logger.info("New session");
    }
}