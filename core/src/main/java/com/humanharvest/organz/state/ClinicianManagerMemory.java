package com.humanharvest.organz.state;

import com.humanharvest.organz.Clinician;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * The class to handle the Client inputs, including adding,
 * setting attributes and updating the values of the client.
 */

public class ClinicianManagerMemory implements ClinicianManager{

    private static final int DEFAULT_CLINICIAN_ID = 0;

    private final List<Clinician> clinicians;

    private final Clinician defaultClinician = new Clinician(
            "Default",
            null,
            "Clinician",
            "Unspecified",
            "Unspecified",
            null,
        DEFAULT_CLINICIAN_ID,
            "clinician");

    public ClinicianManagerMemory() {
        clinicians = new ArrayList<>();
        clinicians.add(defaultClinician);
    }

    public ClinicianManagerMemory(List<Clinician> clinicians) {
        this.clinicians = clinicians;
        clinicians.add(defaultClinician);
    }

    /**
     * Add a clinician
     * @param clinician Clinician to be added
     */
    @Override
    public void addClinician(Clinician clinician) {
        clinicians.add(clinician);
    }

    /**
     * Get the list of clinicians
     * @return ArrayList of current clinicians
     */
    @Override
    public List<Clinician> getClinicians() {
        return Collections.unmodifiableList(clinicians);
    }

    /**
     * Remove a client object
     * @param clinician Clinician to be removed
     */
    @Override
    public void removeClinician(Clinician clinician) {
        clinicians.remove(clinician);
    }

    /**
     * Checks if a user already exists with that staff id
     * @param staffId The id of the clinician
     * @return Boolean
     */
    @Override
    public boolean doesStaffIdExist(int staffId) {
        for (Clinician clinician : clinicians) {
            if (clinician.getStaffId() == staffId) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void applyChangesTo(Clinician clinician){
        //Do Nothing for now....
    }

    /**
     * Return a clinician matching that UID
     * @param staffId To be matched
     * @return Clinician object or empty if none exists
     */
    @Override
    public Optional<Clinician> getClinicianByStaffId(int staffId) {
        return clinicians.stream()
                .filter(o -> o.getStaffId() == staffId)
                .findFirst();
    }

    /**
     * Return the default clinician
     * @return the default clinician
     */
    @Override
    public Clinician getDefaultClinician() {
        return getClinicianByStaffId(DEFAULT_CLINICIAN_ID).orElseThrow(IllegalStateException::new);
    }

    @Override
    public void setClinicians(Collection<Clinician> clinicians) {
        this.clinicians.clear();
        this.clinicians.addAll(clinicians);
    }

}