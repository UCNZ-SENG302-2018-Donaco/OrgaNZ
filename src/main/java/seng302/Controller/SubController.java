package seng302.Controller;

import seng302.Utilities.WindowContext;

/**
 * A Controller that holds it's parent controller state
 */
abstract public class SubController {
    protected MainController mainController;
    protected WindowContext windowContext;

    /**
     * Set the controllers parent controller
     * @param mainController The MainController
     */
    public void setup(MainController mainController) {
        this.mainController = mainController;
        this.windowContext = mainController.getWindowContext();
    }

    /**
     * Get the controllers parent controller
     * @return The MainController
     */
    public MainController getMainController() {
        return mainController;
    }
}
