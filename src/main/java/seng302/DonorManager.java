package seng302;

import seng302.Actions.ActionInvoker;

import java.time.LocalDate;
import java.util.ArrayList;

/**
 * The class to handle the Donor inputs, including adding,
 * setting attributes and updating the values of the donor.
 *
 * @author Dylan Carlyle, Jack Steel
 * @version sprint 1.
 * date 08/03/2018
 */

public class DonorManager {

    private ArrayList<Donor> donors;
    private int uid;

    public DonorManager() {
        donors = new ArrayList<>();
        uid = 1;
    }

    public DonorManager(ArrayList<Donor> donors) {
        this.donors = donors;
        uid = calculateNextId();
    }

    public void setDonors(ArrayList<Donor> donors) {
        this.donors = donors;
    }

    /**
     * Add a donor
     * @param donor Donor to be added
     */
    public void addDonor(Donor donor) {
        donors.add(donor);
    }

    /**
     * Get the list of donors
     * @return ArrayList of current donors
     */
    public ArrayList<Donor> getDonors() {
        return donors;
    }

    /**
     * Remove a donor object
     * @param donor Donor to be removed
     */
    public void removeDonor(Donor donor) {
        donors.remove(donor);
    }

    /**
     * Get the next user ID
     * @return Next userID to be used
     */
    public int getUid() {
        return uid++;
    }

	/**
	 * Set the user ID
	 * @param uid Value to set the user IF
	 */
	public void setUid(int uid) { this.uid = uid; }

    /**
     * Checks if a user already exists with that first + last name and date of birth
     * @param firstName First name
     * @param lastName Last name
     * @param dateOfBirth Date of birth (LocalDate)
     * @return Boolean
     */
    public boolean collisionExists(String firstName, String lastName, LocalDate dateOfBirth) {
        for (Donor donor : donors) {
            if (donor.getFirstName().equals(firstName) &&
                    donor.getLastName().equals(lastName) &&
                    donor.getDateOfBirth().isEqual(dateOfBirth)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Return a donor matching that UID
     * @param id To be matched
     * @return Donor object or null if none exists
     */
    public Donor getDonorByID(int id) {
        return donors.stream()
                .filter(d -> d.getUid() == id).findFirst().orElse(null);
    }

    private int calculateNextId() {
        int id = 1;
        for (Donor donor : donors) {
            if (donor.getUid() >= id) {
                id = donor.getUid() + 1;
            }
        }
        return id;
    }

}
