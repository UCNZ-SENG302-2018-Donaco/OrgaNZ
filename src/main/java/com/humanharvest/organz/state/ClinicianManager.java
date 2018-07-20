package com.humanharvest.organz.state;

import java.util.Collection;
import java.util.List;

import com.humanharvest.organz.Clinician;

/**
 * The class to handle the Client inputs, including adding,
 * setting attributes and updating the values of the client.
 */

public interface ClinicianManager {


    /**
     * Add a clinician
     * @param clinician Clinician to be added
     */
    void addClinician(Clinician clinician);

    void setClinicians(Collection<Clinician> clinicians);


    /**
     * Get the list of clinicians
     * @return ArrayList of current clinicians
     */
    List<Clinician> getClinicians();

    /**
     * Remove a client object
     * @param clinician Clinician to be removed
     */
    void removeClinician(Clinician clinician);

    void applyChangesTo(Clinician clinician);

    /**
     * Checks if a user already exists with that staff id
     * @param staffId The id of the clinician
     * @return Boolean
     */
    boolean collisionExists(int staffId);

    /**
     * Return a clinician matching that UID
     * @param id To be matched
     * @return Clinician object or null if none exists
     */
    Clinician getClinicianByStaffId(int id);

    /**
     * Return the default clinician
     * @return the default clinician
     */
    Clinician getDefaultClinician();

}