package com.humanharvest.organz.state;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import com.humanharvest.organz.Clinician;
import com.humanharvest.organz.utilities.enums.Region;

/**
 * The class to handle the Client inputs, including adding,
 * setting attributes and updating the values of the client.
 */

public class ClinicianManagerMemory implements ClinicianManager{

    private final List<Clinician> clinicians;
    private int defaultClinicianId = 0;
    private Clinician defaultClinician = new Clinician("admin", null, "admin", "admin", Region.UNSPECIFIED.name(),
            defaultClinicianId, "admin");

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
    public void addClinician(Clinician clinician) {
        clinicians.add(clinician);
    }

    /**
     * Get the list of clinicians
     * @return ArrayList of current clinicians
     */
    public List<Clinician> getClinicians() {
        return Collections.unmodifiableList(clinicians);
    }

    /**
     * Remove a client object
     * @param clinician Clinician to be removed
     */
    public void removeClinician(Clinician clinician) {
        clinicians.remove(clinician);
    }

    /**
     * Checks if a user already exists with that staff id
     * @param staffId The id of the clinician
     * @return Boolean
     */
    public boolean doesStaffIdExist(int staffId) {
        for (Clinician clinician : clinicians) {
            if (clinician.getStaffId() == staffId) {
                return true;
            }
        }
        return false;
    }

    public void applyChangesTo(Clinician clinician){
        //Do Nothing for now....
    }

    /**
     * Return a clinician matching that UID
     * @param id To be matched
     * @return Clinician object or empty if none exists
     */
    public Optional<Clinician> getClinicianByStaffId(int id) {
        return clinicians.stream()
                .filter(o -> o.getStaffId() == id)
                .findFirst();
    }

    /**
     * Return the default clinician
     * @return the default clinician
     */
    public Clinician getDefaultClinician() {
        return getClinicianByStaffId(defaultClinicianId).orElseThrow(IllegalStateException::new);
    }

    public void setClinicians(Collection<Clinician> clinicians) {
        this.clinicians.clear();
        this.clinicians.addAll(clinicians);
    }

}