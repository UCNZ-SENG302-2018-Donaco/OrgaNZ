package seng302;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.*;
import java.lang.reflect.Type;
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
    }

    private void setDonors(ArrayList<Donor> donors) {
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

    /**
     * Saves the current donors list to a specified file
     * @param file The file to be saved to
     * @throws IOException Throws IOExceptions
     */
    public void saveToFile(File file) throws IOException {
        Writer writer = new FileWriter(file);
        Gson gson = new GsonBuilder()
                .setPrettyPrinting()
                .enableComplexMapKeySerialization()
                .create();

        gson.toJson(donors, writer);
        writer.close();
    }

    /**
     * Loads the donors from a specified file. Overwrites any current donors
     * @param file The file to be loaded from
     * @throws IOException Throws IOExceptions
     */
    public void loadFromFile(File file) throws IOException {
        Reader reader = new FileReader(file);
        Gson gson = new Gson();
        ArrayList<Donor> donors;
        Type collectionType = new TypeToken<ArrayList<Donor>>() {}.getType();
        donors = gson.fromJson(reader, collectionType);
        setDonors(donors);
        for (Donor donor : donors) {
            if (donor.getUid() >= uid) {
                uid = donor.getUid() + 1;
            }
        }
    }
}
