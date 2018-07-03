package com.humanharvest.organz.state;

import java.util.Collection;
import java.util.List;

import com.humanharvest.organz.Clinician;

public class ClinicianManagerRest implements ClinicianManager {

    @Override
    public void addClinician(Clinician clinician) {

    }

    @Override
    public void setClinicians(Collection<Clinician> clinicians) {

    }

    @Override
    public List<Clinician> getClinicians() {
        return null;
    }

    @Override
    public void removeClinician(Clinician clinician) {

    }

    @Override
    public void applyChangesTo(Clinician clinician) {

    }

    @Override
    public boolean collisionExists(int staffId) {
        return false;
    }

    @Override
    public Clinician getClinicianByStaffId(int id) {
        return null;
    }

    @Override
    public Clinician getDefaultClinician() {
        return null;
    }
}
