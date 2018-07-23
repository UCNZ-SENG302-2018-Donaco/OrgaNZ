package com.humanharvest.organz.utilities.view;

import com.humanharvest.organz.Client;

/**
 * A class to represent the parameters for a given window.
 * Is stored by that window's MainController, and can be accessed by SubControllers.
 */
public class WindowContext {

    private boolean sidebarEnabled;
    private boolean isClinViewClientWindow;
    private Client viewClient;

    private WindowContext(WindowContextBuilder builder) {
        this.sidebarEnabled = builder.sidebarEnabled;
        this.isClinViewClientWindow = builder.isClinicianViewClientWindow;
        this.viewClient = builder.viewClient;
    }

    /**
     * A builder to allow easy customisation of the window parameters.
     * Has methods to set parameters that return the builder, in order to allow fluent interface usage.
     */
    public static class WindowContextBuilder {

        private boolean sidebarEnabled = true;
        private boolean isClinicianViewClientWindow;
        private Client viewClient;

        public WindowContextBuilder() {
        }

        public WindowContextBuilder setSidebarDisabled() {
            sidebarEnabled = false;
            return this;
        }

        public WindowContextBuilder setAsClinicianViewClientWindow() {
            isClinicianViewClientWindow = true;
            return this;
        }

        public WindowContextBuilder viewClient(Client client) {
            viewClient = client;
            return this;
        }

        /**
         * Checks that the parameters given for the window would be valid, then creates the WindowContext if so.
         * @return the new WindowContext.
         */
        public WindowContext build() {
            if (isClinicianViewClientWindow && viewClient == null) {
                throw new IllegalStateException("Cannot have a clinician client view with no client defined.");
            } else {
                return new WindowContext(this);
            }
        }
    }

    /**
     * Creates a default WindowContext.
     * @return a WindowContext with default parameters.
     */
    public static WindowContext defaultContext() {
        return new WindowContextBuilder().build();
    }

    public boolean isSidebarEnabled() {
        return sidebarEnabled;
    }

    public boolean isClinViewClientWindow() {
        return isClinViewClientWindow;
    }

    public Client getViewClient() {
        return viewClient;
    }
}
