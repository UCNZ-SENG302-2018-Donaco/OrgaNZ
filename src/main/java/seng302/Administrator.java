package seng302;

import seng302.Utilities.Enums.Region;

public class Administrator extends Clinician {

    /**
     * Create a new Administrator object
     * @param firstName First name string
     * @param middleName Middle name(s). May be null
     * @param lastName Last name string
     * @param workAddress Address string
     * @param region Region from the Region ENUM
     * @param staffId The unique staffId. Should be checked using the ClinicianManager to ensure uniqueness
     * @param password The clinicians password for logins. Stored in plaintext
     */
    public Administrator(String firstName, String middleName, String lastName, String workAddress,
            Region region, int staffId, String password) {
        super(firstName, middleName, lastName, workAddress, region, staffId, password);
    }
}
