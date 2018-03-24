package seng302.Utilities;

import seng302.Donor;

public class PageContext {
    private Donor viewDonor;
    private boolean sidebarEnabled;
    private boolean isClinViewDonorWindow;

    public PageContext() {}

    private PageContext(PageContextBuilder builder) {
        this.viewDonor = builder.viewDonor;
        this.sidebarEnabled = builder.sidebarEnabled;
        this.isClinViewDonorWindow = builder.isClinViewDonorWindow;
    }

    public static class PageContextBuilder {
        private Donor viewDonor;
        private boolean sidebarEnabled;
        private boolean isClinViewDonorWindow;

        public PageContextBuilder() {}

        public PageContextBuilder viewDonor(Donor donor) {
            this.viewDonor = donor;
            return this;
        }

        public PageContextBuilder sidebarEnabled(boolean enabled) {
            this.sidebarEnabled = enabled;
            return this;
        }

        public PageContextBuilder isClinViewDonorWindow(boolean is) {
            this.isClinViewDonorWindow = is;
            return this;
        }

        public PageContext build() {
            if (isClinViewDonorWindow && viewDonor == null) {
                throw new IllegalStateException("Cannot have a clinician donor view with no donor defined.");
            } else {
                return new PageContext(this);
            }
        }
    }

    public Donor getViewDonor() {
        return viewDonor;
    }

    public boolean isSidebarEnabled() {
        return sidebarEnabled;
    }

    public boolean isClinViewDonorWindow() {
        return isClinViewDonorWindow;
    }
}
