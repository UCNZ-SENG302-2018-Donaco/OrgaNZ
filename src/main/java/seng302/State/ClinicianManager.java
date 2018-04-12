package seng302.State;

import java.util.ArrayList;

import seng302.Clinician;
import seng302.Utilities.Enums.Region;

/**
 * The class to handle the Clinician inputs, including adding,
 * setting attributes and updating the values of the clinicians.
 */

public class ClinicianManager {

    private ArrayList<Clinician> clinicians;

    public ClinicianManager() {
        clinicians = new ArrayList<>();
        clinicians.add(new Clinician("admin", null, "admin", "admin", Region.UNSPECIFIED, 0, "admin"));
    }

    public ClinicianManager(ArrayList<Clinician> clinicians) {
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
    public ArrayList<Clinician> getClinicians() {
        return clinicians;
    }

    /**
     * Remove a donor object
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
                .filter(c -> c.getStaffId() == id).findFirst().orElse(null);
    }
}
