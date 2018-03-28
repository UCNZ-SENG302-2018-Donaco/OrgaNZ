package seng302.Utilities;

import seng302.Donor;

/**
 * A class to represent the parameters for a given window.
 * Is stored by that window's MainController, and can be accessed by SubControllers.
 */
public class WindowContext {
    private boolean sidebarEnabled;
    private boolean isClinViewDonorWindow;
    private Donor viewDonor;

    private WindowContext(WindowContextBuilder builder) {
        this.sidebarEnabled = builder.sidebarEnabled;
        this.isClinViewDonorWindow = builder.isClinViewDonorWindow;
        this.viewDonor = builder.viewDonor;
    }

    /**
     * A builder to allow easy customisation of the window parameters.
     * Has methods to set parameters that return the builder, in order to allow fluent interface usage.
     */
    public static class WindowContextBuilder {
        private boolean sidebarEnabled = true;
        private boolean isClinViewDonorWindow = false;
        private Donor viewDonor;

        public WindowContextBuilder() {}

        public WindowContextBuilder setSidebarDisabled() {
            this.sidebarEnabled = false;
            return this;
        }

        public WindowContextBuilder setAsClinViewDonorWindow() {
            this.isClinViewDonorWindow = true;
            return this;
        }

        public WindowContextBuilder viewDonor(Donor donor) {
            this.viewDonor = donor;
            return this;
        }

        /**
         * Checks that the parameters given for the window would be valid, then creates the WindowContext if so.
         * @return the new WindowContext.
         */
        public WindowContext build() {
            if (isClinViewDonorWindow && viewDonor == null) {
                throw new IllegalStateException("Cannot have a clinician donor view with no donor defined.");
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

    public boolean isClinViewDonorWindow() {
        return isClinViewDonorWindow;
    }

    public Donor getViewDonor() {
        return viewDonor;
    }
}
