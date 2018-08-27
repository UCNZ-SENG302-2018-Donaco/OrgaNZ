package com.humanharvest.organz.state;

import java.util.ArrayList;
import java.util.List;

import com.humanharvest.organz.BaseTest;
import com.humanharvest.organz.Clinician;
import com.humanharvest.organz.database.DBManager;
import com.humanharvest.organz.utilities.enums.Country;
import com.humanharvest.organz.utilities.enums.Region;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

@Ignore
public class ClinicianManagerDBPureTest extends BaseTest {

    private DBManager dbManager;

    private Clinician c = new Clinician("Thomas", "Test", "Tety", "21 Ok", Region.NORTHLAND.name(), Country.NZ, 3,
            "password");

    @Ignore
    @Test
    public void retrieveAllUsers() {
        ClinicianManagerDBPure test = new ClinicianManagerDBPure();
        Clinician clinician = new Clinician("First", null, "Last", "Address", Region.UNSPECIFIED.name(), Country.NZ, 1,
                "pass");
        Clinician clinician2 = new Clinician("First2", null, "Last2", "Address", Region.UNSPECIFIED.name(), Country.NZ,
                2,
                "pass");
        ArrayList<Clinician> clinicians = new ArrayList<>();
        clinicians.add(clinician);
        clinicians.add(clinician2);
        test.setClinicians(clinicians);
        List<Clinician> result = test.getClinicians();
        Assert.assertEquals(clinicians, result);

    }

    @Ignore
    @Test
    public void addClinician() {
        ClinicianManagerDBPure test = new ClinicianManagerDBPure();
        test.addClinician(c);
    }

    @Test
    public void getAddedClinician() {
        ClinicianManagerDBPure test = new ClinicianManagerDBPure();
        test.addClinician(c);
        Clinician result = test.getClinicianByStaffId(3).orElseThrow(IllegalStateException::new);
        Assert.assertEquals(c, result);

    }

    @Ignore
    @Test
    public void mergeConflictClinician() {
        ClinicianManagerDBPure test = new ClinicianManagerDBPure();
        test.addClinician(c);
        Boolean result = test.doesStaffIdExist(3);
        Assert.assertEquals(true, result);

    }

    @Ignore
    @Test
    public void alterClientFirstName() {
        ClinicianManagerDBPure test = new ClinicianManagerDBPure();
        test.addClinician(c);
        c.setFirstName("New Name");
        dbManager.saveEntity(c);

        Clinician result = test.getClinicianByStaffId(3).orElseThrow(IllegalStateException::new);
        Assert.assertEquals("New Name", result.getFirstName());
    }
}
