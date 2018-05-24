package seng302.State;

import java.util.ArrayList;
import java.util.List;

import seng302.Clinician;
import seng302.Database.DBManager;
import seng302.Utilities.Enums.Region;

import org.hibernate.Session;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

@Ignore
public class ClinicianManagerDBPureTest {

    private DBManager dbManager;

    Clinician c = new Clinician("Thomas","Test","Tety","21 Ok", Region.NORTHLAND,3,"password");

    @Ignore
    public void retrieveAllUsers(){
        ClinicianManagerDBPure test = new ClinicianManagerDBPure();
        Clinician clinician = new Clinician("First", null, "Last", "Address", Region.UNSPECIFIED, 1, "pass");
        Clinician clinician2 = new Clinician("First2", null, "Last2", "Address", Region.UNSPECIFIED, 2, "pass");
        ArrayList<Clinician> clinicians = new ArrayList<>();
        clinicians.add(clinician);
        clinicians.add(clinician2);
        test.setClinicians(clinicians);
        List<Clinician> result = test.getClinicians();
        Assert.assertEquals(clinicians,result);

    }

    @Ignore
    public void addClinician(){
        ClinicianManagerDBPure test = new ClinicianManagerDBPure();
        test.addClinician(c);
    }

    @Test
    public void getAddedClinician(){
        ClinicianManagerDBPure test = new ClinicianManagerDBPure();
        test.addClinician(c);
        Clinician result = test.getClinicianByStaffId(3);
        Assert.assertEquals(c,result);

    }

    @Ignore
    public void mergeConflictClinician(){
        ClinicianManagerDBPure test = new ClinicianManagerDBPure();
        test.addClinician(c);
        Boolean result = test.collisionExists(3);
        Assert.assertEquals(true,result);

    }

    @Ignore
    public void alterClientFirstName(){
        ClinicianManagerDBPure test = new ClinicianManagerDBPure();
        test.addClinician(c);
        c.setFirstName("New Name");
        dbManager.saveEntity(c);

        Clinician result = test.getClinicianByStaffId(3);
        Assert.assertEquals("New Name",result.getFirstName());

    }



}
