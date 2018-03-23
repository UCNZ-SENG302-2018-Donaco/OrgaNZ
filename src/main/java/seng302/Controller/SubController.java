package seng302.Controller;

/**
 * A Controller that holds it's parent controller state
 */
abstract public class SubController {

    protected MainController mainController;

    /**
     * Set the controllers parent controller
     * @param mainController The MainController
     */
    public void setMainController(MainController mainController){
        this.mainController = mainController;
    }

    /**
     * Get the controllers parent controller
     * @return The MainController
     */
    public MainController getMainController() {
        return mainController;
    }
}
