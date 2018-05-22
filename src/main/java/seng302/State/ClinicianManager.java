package seng302.State;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import seng302.Clinician;
import seng302.Utilities.Enums.Region;

/**
 * The class to handle the Client inputs, including adding,
 * setting attributes and updating the values of the client.
 */

public class ClinicianManager {

    private final List<Clinician> clinicians;
    private int defaultClinicianId = 0;
    private Clinician defaultClinician = new Clinician("admin", null, "admin", "admin", Region.UNSPECIFIED,
            defaultClinicianId, "admin");

    public ClinicianManager() {
        clinicians = new ArrayList<>();
        clinicians.add(defaultClinician);
    }

    public ClinicianManager(List<Clinician> clinicians) {
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
    public boolean collisionExists(int staffId) {
        for (Clinician clinician : clinicians) {
            if (clinician.getStaffId() == staffId) {
                return true;
            }
        }
        return false;
    }

    /**
     * Return a clinician matching that UID
     * @param id To be matched
     * @return Clinician object or null if none exists
     */
    public Clinician getClinicianByStaffId(int id) {
        return clinicians.stream()
                .filter(o -> o.getStaffId() == id)
                .findFirst()
                .orElse(null);
    }

    /**
     * Return the default clinician
     * @return the default clinician
     */
    public Clinician getDefaultClinician() {
        return getClinicianByStaffId(defaultClinicianId);
    }
}
