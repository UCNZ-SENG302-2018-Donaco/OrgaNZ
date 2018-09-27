package com.humanharvest.organz.controller;

import com.humanharvest.organz.utilities.view.WindowContext;

/**
 * An abstract class that can be extended by controller classes for individual pages and other SubControllers within
 * the GUI. It holds a reference to the {@link MainController} for the window it is in.
 */
public abstract class SubController {

    protected MainController mainController;
    protected WindowContext windowContext;

    /**
     * Method to setup the subcontroller after its window (main controller) has been assigned. Sets the
     * subcontroller's {@link MainController}, associated with the window it is in, also sets the window context.
     *
     * @param mainController The main controller that defines which window this SubController belongs to.
     */
    public void setup(MainController mainController) {
        this.mainController = mainController;
        this.windowContext = mainController.getWindowContext();
    }

    /**
     * Refreshes all data on this page, so that it may be updated if any changes have been made elsewhere.
     * Implementors should refresh as much data as possible, but not overwrite user made changes.
     */
    public abstract void refresh();
}
