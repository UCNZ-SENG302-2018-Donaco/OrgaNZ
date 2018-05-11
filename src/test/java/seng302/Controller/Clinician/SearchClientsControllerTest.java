package seng302.Controller.Clinician;


import seng302.Clinician;
import seng302.Controller.ControllerTest;
import seng302.State.State;
import seng302.Utilities.Enums.Region;
import seng302.Utilities.View.Page;
import seng302.Utilities.View.WindowContext;

public class SearchClientsControllerTest extends ControllerTest{

    private Clinician testClinician = new Clinician("Admin", "Da", "Nimda", "2 Two Street", Region.CANTERBURY,
            55, "admin");

    @Override
    protected Page getPage() {
        return Page.SEARCH;
    }

    @Override
    protected void initState() {
        State.init();
        State.login(testClinician);
        WindowContext.defaultContext();

    }
}