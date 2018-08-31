package com.humanharvest.organz;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.HashSet;
import java.util.Set;

import com.humanharvest.organz.utilities.enums.Organ;

import org.junit.Before;
import org.junit.Test;

public class HospitalTest {

    private Hospital hospital;
    private Organ organ = Organ.LIVER;

    private Set<Organ> organs = new HashSet<>();
    private Organ organ1 = Organ.HEART;
    private Organ organ2 = Organ.BONE;

    @Before
    public void setUp() {
        Set<Hospital> hospitals = Hospital.getDefaultHospitals();
        hospital = (Hospital) hospitals.toArray()[0];

        organs.add(organ1);
        organs.add(organ2);
    }

    @Test
    public void testaddTransplantProgramFor() {
        assertEquals(0, hospital.getTransplantPrograms().size());
        assertTrue(hospital.addTransplantProgramFor(organ));
        assertEquals(1, hospital.getTransplantPrograms().size());
    }

    @Test
    public void testremoveTransplantProgramFor() {
        assertEquals(0, hospital.getTransplantPrograms().size());
        assertTrue(hospital.addTransplantProgramFor(organ));
        assertEquals(1, hospital.getTransplantPrograms().size());
        assertTrue(hospital.removeTransplantProgramFor(organ));
        assertEquals(0, hospital.getTransplantPrograms().size());
    }

    @Test
    public void testSetOrgans() {
        assertEquals(0, hospital.getTransplantPrograms().size());
        assertTrue(hospital.addTransplantProgramFor(organ));
        assertEquals(1, hospital.getTransplantPrograms().size());
        hospital.setTransplantPrograms(organs);

        // Check that heart and bone have been added, and liver is no longer there
        assertEquals(2, hospital.getTransplantPrograms().size());
        assertTrue(hospital.getTransplantPrograms().contains(organ1));
        assertTrue(hospital.getTransplantPrograms().contains(organ2));
    }
}
