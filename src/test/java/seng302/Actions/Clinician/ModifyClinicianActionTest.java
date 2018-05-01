package seng302.Actions.Clinician;


import static org.junit.Assert.assertEquals;

import seng302.Actions.ActionInvoker;
import seng302.Clinician;
import seng302.State.ClinicianManager;
import seng302.Utilities.Enums.Region;

import org.junit.Before;
import org.junit.Test;

public class ModifyClinicianActionTest {

    private ClinicianManager manager;
    private ActionInvoker invoker;
    private Clinician baseClinician;

    @Before
    public void init() {
        invoker = new ActionInvoker();
        manager = new ClinicianManager();
        baseClinician = new Clinician("First", null, "Last", "Address", Region.UNSPECIFIED, 1, "pass");
        manager.addClinician(baseClinician);
    }

    @Test
    public void CheckClinicianModifyNameTest() throws Exception {
        ModifyClinicianAction action = new ModifyClinicianAction(baseClinician);
        action.addChange("setFirstName", baseClinician.getFirstName(), "New");
        invoker.execute(action);
        assertEquals("New", manager.getClinicians().get(1).getFirstName());
    }

    @Test
    public void CheckClinicianModifyUndoNameTest() throws Exception {
        ModifyClinicianAction action = new ModifyClinicianAction(baseClinician);
        action.addChange("setFirstName", baseClinician.getFirstName(), "New");
        invoker.execute(action);
        invoker.undo();
        assertEquals("First", manager.getClinicians().get(1).getFirstName());
    }

    @Test
    public void CheckClinicianMultipleUpdateValuesTest() throws Exception {
        ModifyClinicianAction action = new ModifyClinicianAction(baseClinician);
        action.addChange("setFirstName", baseClinician.getFirstName(), "New");
        action.addChange("setRegion", baseClinician.getRegion(), Region.CANTERBURY);
        invoker.execute(action);
        assertEquals("New", manager.getClinicians().get(1).getFirstName());
        assertEquals(Region.CANTERBURY, manager.getClinicians().get(1).getRegion());
    }

    @Test
    public void CheckClinicianMultipleUpdateValuesUndoTest() throws Exception {
        ModifyClinicianAction action = new ModifyClinicianAction(baseClinician);
        action.addChange("setFirstName", baseClinician.getFirstName(), "New");
        action.addChange("setRegion", baseClinician.getRegion(), Region.CANTERBURY);
        invoker.execute(action);
        invoker.undo();
        assertEquals("First", manager.getClinicians().get(1).getFirstName());
        assertEquals(Region.UNSPECIFIED, manager.getClinicians().get(1).getRegion());
    }

    @Test(expected = NoSuchMethodException.class)
    public void InvalidSetterFieldTest() throws Exception {
        ModifyClinicianAction action = new ModifyClinicianAction(baseClinician);
        action.addChange("notAField", "Old", "New");
        invoker.execute(action);
    }

    @Test(expected = NoSuchFieldException.class)
    public void InvalidSetterAttributeTest() throws Exception {
        ModifyClinicianAction action = new ModifyClinicianAction(baseClinician);
        action.addChange("setFirstName", 1, "New");
        invoker.execute(action);
    }
}
