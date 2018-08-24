package com.humanharvest.organz.actions.clinician;

import com.humanharvest.organz.BaseTest;
import com.humanharvest.organz.Clinician;
import com.humanharvest.organz.actions.ActionInvoker;
import com.humanharvest.organz.state.ClinicianManager;
import com.humanharvest.organz.state.ClinicianManagerMemory;
import com.humanharvest.organz.utilities.enums.Region;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ModifyClinicianActionTest extends BaseTest {

    private ClinicianManager manager;
    private ActionInvoker invoker;
    private Clinician baseClinician;

    @Before
    public void init() {
        invoker = new ActionInvoker();
        manager = new ClinicianManagerMemory();
        baseClinician = new Clinician("First", null, "Last", "Address", Region.UNSPECIFIED.name(), null, 1, "pass");
        manager.addClinician(baseClinician);
    }

    @Test
    public void CheckClinicianModifyNameTest() throws NoSuchMethodException, NoSuchFieldException {
        ModifyClinicianAction action = new ModifyClinicianAction(baseClinician, manager);
        action.addChange("setFirstName", baseClinician.getFirstName(), "New");
        invoker.execute(action);
        assertEquals("New", manager.getClinicians().get(1).getFirstName());
    }

    @Test
    public void CheckClinicianModifyUndoNameTest() throws NoSuchMethodException, NoSuchFieldException {
        ModifyClinicianAction action = new ModifyClinicianAction(baseClinician, manager);
        action.addChange("setFirstName", baseClinician.getFirstName(), "New");
        invoker.execute(action);
        invoker.undo();
        assertEquals("First", manager.getClinicians().get(1).getFirstName());
    }

    @Test
    public void CheckClinicianMultipleUpdateValuesTest() throws NoSuchMethodException, NoSuchFieldException {
        ModifyClinicianAction action = new ModifyClinicianAction(baseClinician, manager);
        action.addChange("setFirstName", baseClinician.getFirstName(), "New");
        action.addChange("setRegion", baseClinician.getRegion(), Region.CANTERBURY.toString());
        invoker.execute(action);
        assertEquals("New", manager.getClinicians().get(1).getFirstName());
        assertTrue(Region.CANTERBURY.toString().equalsIgnoreCase(manager.getClinicians().get(1).getRegion()));
    }

    @Test
    public void CheckClinicianMultipleUpdateValuesUndoTest() throws NoSuchMethodException, NoSuchFieldException {
        ModifyClinicianAction action = new ModifyClinicianAction(baseClinician, manager);
        action.addChange("setFirstName", baseClinician.getFirstName(), "New");
        action.addChange("setRegion", baseClinician.getRegion(), Region.CANTERBURY.toString());
        invoker.execute(action);
        invoker.undo();
        assertEquals("First", manager.getClinicians().get(1).getFirstName());
        assertTrue(Region.UNSPECIFIED.toString().equalsIgnoreCase(manager.getClinicians().get(1).getRegion()));
    }

    @Test(expected = NoSuchMethodException.class)
    public void InvalidSetterFieldTest() throws NoSuchMethodException, NoSuchFieldException {
        ModifyClinicianAction action = new ModifyClinicianAction(baseClinician, manager);
        action.addChange("notAField", "Old", "New");
        invoker.execute(action);
    }

    @Test(expected = NoSuchFieldException.class)
    public void InvalidSetterAttributeTest() throws NoSuchMethodException, NoSuchFieldException {
        ModifyClinicianAction action = new ModifyClinicianAction(baseClinician, manager);
        action.addChange("setFirstName", 1, "New");
        invoker.execute(action);
    }
}
