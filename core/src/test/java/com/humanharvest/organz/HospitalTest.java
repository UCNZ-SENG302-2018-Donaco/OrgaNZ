package com.humanharvest.organz;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashSet;
import java.util.Set;

import com.humanharvest.organz.utilities.enums.Organ;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class HospitalTest {

    private Hospital hospital;
    private final Organ organ = Organ.LIVER;

    private final Set<Organ> organs = new HashSet<>();
    private final Organ organ1 = Organ.HEART;
    private final Organ organ2 = Organ.BONE;

    @BeforeEach
    public void setUp() {
        Set<Hospital> hospitals = Hospital.getDefaultHospitals();
        hospital = (Hospital) hospitals.toArray()[0];

        organs.add(organ1);
        organs.add(organ2);
    }

    @Test
    public void testAddTransplantProgramFor() {
        assertEquals(0, hospital.getTransplantPrograms().size());
        assertTrue(hospital.addTransplantProgramFor(organ));
        assertEquals(1, hospital.getTransplantPrograms().size());
    }

    @Test
    public void testRemoveTransplantProgramFor() {
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

    @Test
    public void testHospitalDistance() {
        Hospital hospital1 = new Hospital("Test1", 0, 0, "Test1");
        Hospital hospital2 = new Hospital("Test2", 0, 2.25, "Test2");
        assertEquals(250, hospital1.calculateDistanceTo(hospital2), 1);
        assertEquals(60, hospital1.calculateTimeTo(hospital2).toMinutes());
    }
}
