package seng302.Controller;

/**
 * A Controller that holds it's parent controller state
 */
public interface SubController {
    /**
     * Set the controllers parent controller
     * @param mainController The MainController
     */
    void setMainController(MainController mainController);

    /**
     * Get the controllers parent controller
     * @return The MainController
     */
    MainController getMainController();
}
