package com.humanharvest.organz.controller.administrator;

import static org.testfx.api.FxAssert.verifyThat;
import static org.testfx.matcher.control.ListViewMatchers.hasItems;
import static org.testfx.matcher.control.ListViewMatchers.hasListCell;
import static org.testfx.matcher.control.TableViewMatchers.hasNumRows;
import static org.testfx.util.NodeQueryUtils.hasText;

import com.humanharvest.organz.Administrator;
import com.humanharvest.organz.Clinician;
import com.humanharvest.organz.controller.ControllerTest;
import com.humanharvest.organz.state.State;
import com.humanharvest.organz.utilities.enums.Country;
import com.humanharvest.organz.utilities.enums.Region;
import com.humanharvest.organz.utilities.view.Page;
import com.humanharvest.organz.utilities.view.WindowContext;
import org.junit.Test;

public class StaffListControllerTest extends ControllerTest {

    private Administrator admin1 = new Administrator("admin1", "password");
    private Clinician clinician1 = new Clinician("First", "Middle", "Last", "UC", Region.CANTERBURY.toString(), Country.NZ, 50,
            "password");

    @Override
    protected Page getPage() {
        return Page.STAFF_LIST;
    }

    @Override
    protected void initState() {
        State.reset();

        // Add clinician and admins
        State.getAdministratorManager().addAdministrator(admin1);
        State.getClinicianManager().addClinician(clinician1);

        // Login as an admin and open staff list page
        State.login(admin1);
        mainController.setWindowContext(WindowContext.defaultContext());
    }

    @Test
    public void clinicianIsVisible() {
        verifyThat("#tableView",hasNumRows(2)); // Should have default and clinician1

    }
}
