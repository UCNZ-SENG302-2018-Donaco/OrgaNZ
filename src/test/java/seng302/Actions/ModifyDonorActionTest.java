package seng302.Actions;


import org.junit.Before;
import org.junit.Test;
import seng302.Donor;
import seng302.DonorManager;
import seng302.Utilities.Gender;

import java.time.LocalDate;

import static org.junit.Assert.assertEquals;

public class ModifyDonorActionTest {

    private DonorManager manager;
    private ActionInvoker invoker;
    private Donor baseDonor;

    @Before
    public void init() {
        invoker = new ActionInvoker();
        manager = new DonorManager();
        baseDonor = new Donor("First", null, "Last", LocalDate.of(1970, 1, 1), 1);
        manager.addDonor(baseDonor);
    }

    @Test
    public void CheckDonorModifyNameTest() throws Exception {
        ModifyDonorAction action = new ModifyDonorAction(baseDonor);
        action.addChange("setFirstName", baseDonor.getFirstName(), "New");
        invoker.execute(action);
        assertEquals("New", manager.getDonors().get(0).getFirstName());
    }

    @Test
    public void CheckDonorModifyUndoNameTest() throws Exception {
        ModifyDonorAction action = new ModifyDonorAction(baseDonor);
        action.addChange("setFirstName", baseDonor.getFirstName(), "New");
        invoker.execute(action);
        invoker.undo();
        assertEquals("First", manager.getDonors().get(0).getFirstName());
    }

    @Test
    public void CheckDonorMultipleUpdateValuesTest() throws Exception {
        ModifyDonorAction action = new ModifyDonorAction(baseDonor);
        action.addChange("setFirstName", baseDonor.getFirstName(), "New");
        action.addChange("setGender", baseDonor.getGender(), Gender.FEMALE);
        invoker.execute(action);
        assertEquals("New", manager.getDonors().get(0).getFirstName());
        assertEquals(Gender.FEMALE, manager.getDonors().get(0).getGender());
    }

    @Test
    public void CheckDonorMultipleUpdateValuesUndoTest() throws Exception {
        ModifyDonorAction action = new ModifyDonorAction(baseDonor);
        action.addChange("setFirstName", baseDonor.getFirstName(), "New");
        action.addChange("setGender", baseDonor.getGender(), Gender.FEMALE);
        invoker.execute(action);
        invoker.undo();
        assertEquals("First", manager.getDonors().get(0).getFirstName());
        assertEquals(Gender.UNSPECIFIED, manager.getDonors().get(0).getGender());
    }

    @Test(expected = NoSuchMethodException.class)
    public void InvalidSetterFieldTest() throws Exception {
        ModifyDonorAction action = new ModifyDonorAction(baseDonor);
        action.addChange("notAField", "Old", "New");
        invoker.execute(action);
    }

    @Test(expected = NoSuchFieldException.class)
    public void InvalidSetterAttributeTest() throws Exception {
        ModifyDonorAction action = new ModifyDonorAction(baseDonor);
        action.addChange("setFirstName", 1, "New");
        invoker.execute(action);
    }
}
