package seng302.State;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.OptionalInt;

import seng302.Clinician;
import seng302.Utilities.Enums.Region;

/**
 * The class to handle the Client inputs, including adding,
 * setting attributes and updating the values of the client.
 */

public class ClinicianManager {

    private final List<Clinician> clinicians;

    public ClinicianManager() {
        clinicians = new ArrayList<>();
        clinicians.add(new Clinician("admin", null, "admin", "admin", Region.UNSPECIFIED, 0, "admin"));
    }

    public ClinicianManager(List<Clinician> clinicians) {
        this.clinicians = clinicians;
        clinicians.add(new Clinician("admin", null, "admin", "admin", Region.UNSPECIFIED, 0, "admin"));
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
     * Returns the next unused staff id number for a new clinician.
     * @return The next free StaffID.
     */
    public int nextStaffId() {
        OptionalInt max = clinicians.stream()
                .mapToInt(Clinician::getStaffId)
                .max();

        return max.orElse(0) + 1;
    }
}
