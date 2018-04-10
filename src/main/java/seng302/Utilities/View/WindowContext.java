package seng302.Utilities.View;

import seng302.Person;

/**
 * A class to represent the parameters for a given window.
 * Is stored by that window's MainController, and can be accessed by SubControllers.
 */
public class WindowContext {

    private boolean sidebarEnabled;
    private boolean isClinViewPersonWindow;
    private Person viewPerson;

    private WindowContext(WindowContextBuilder builder) {
        this.sidebarEnabled = builder.sidebarEnabled;
        this.isClinViewPersonWindow = builder.isClinViewPersonWindow;
        this.viewPerson = builder.viewPerson;
    }

    /**
     * A builder to allow easy customisation of the window parameters.
     * Has methods to set parameters that return the builder, in order to allow fluent interface usage.
     */
    public static class WindowContextBuilder {

        private boolean sidebarEnabled = true;
        private boolean isClinViewPersonWindow = false;
        private Person viewPerson;

        public WindowContextBuilder() {
        }

        public WindowContextBuilder setSidebarDisabled() {
            this.sidebarEnabled = false;
            return this;
        }

        public WindowContextBuilder setAsClinViewPersonWindow() {
            this.isClinViewPersonWindow = true;
            return this;
        }

        public WindowContextBuilder viewPerson(Person person) {
            this.viewPerson = person;
            return this;
        }

        /**
         * Checks that the parameters given for the window would be valid, then creates the WindowContext if so.
         * @return the new WindowContext.
         */
        public WindowContext build() {
            if (isClinViewPersonWindow && viewPerson == null) {
                throw new IllegalStateException("Cannot have a clinician person view with no person defined.");
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

    public boolean isClinViewPersonWindow() {
        return isClinViewPersonWindow;
    }

    public Person getViewPerson() {
        return viewPerson;
    }
}
